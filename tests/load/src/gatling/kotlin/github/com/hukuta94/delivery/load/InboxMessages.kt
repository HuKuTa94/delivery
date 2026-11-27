package github.com.hukuta94.delivery.load

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toKotlinDuration

/**
 * Reads the inbox of the running application: when its messages were stored and when the relay processed them.
 * The application sets both times, so the lag between them doesn't depend on the clock of the load test.
 */
class InboxMessages(
    url: String,
    user: String,
    password: String,
) : AutoCloseable {
    private val connection: Connection = DriverManager.getConnection(url, user, password)

    override fun close() {
        connection.close()
    }

    /**
     * @return creation time of the newest message, `null` when the inbox is empty
     */
    fun newestCreatedAt(): LocalDateTime? = connection
        .prepareStatement("SELECT max(created_at) AS newest FROM inbox_messages")
        .use { statement ->
            statement.executeQuery().use { rows ->
                rows.next()
                rows.getObject("newest", LocalDateTime::class.java)
            }
        }

    /**
     * @return number of messages the relay is still going to process: the new ones and the ones it retries
     */
    fun pendingMessages(): Int = connection
        .prepareStatement(PENDING_SQL)
        .use { statement ->
            statement.executeQuery().use { rows ->
                rows.next()
                rows.getInt("pending")
            }
        }

    /**
     * @return lag of the messages created after [after], of all messages when it is `null`
     */
    fun lagOfMessagesCreatedAfter(after: LocalDateTime?): InboxLag = connection
        .prepareStatement(LAG_SQL)
        .use { statement ->
            statement.setCreatedAfter(after)
            statement.executeQuery().use { rows ->
                rows.next()
                InboxLag(
                    messages = rows.getInt("messages"),
                    unprocessed = rows.getInt("unprocessed"),
                    p50 = rows.millis("p50"),
                    p95 = rows.millis("p95"),
                    p99 = rows.millis("p99"),
                    max = rows.millis("max"),
                )
            }
        }

    /**
     * @return number of messages created after [after] (of all messages when it is `null`) and when the first and
     * the last of them were stored
     */
    fun messagesCreatedAfter(after: LocalDateTime?): StoredMessages = connection
        .prepareStatement(STORED_SQL)
        .use { statement ->
            statement.setCreatedAfter(after)
            statement.executeQuery().use { rows ->
                rows.next()
                StoredMessages(
                    messages = rows.getInt("messages"),
                    first = rows.getObject("first", LocalDateTime::class.java),
                    last = rows.getObject("last", LocalDateTime::class.java),
                )
            }
        }

    private fun PreparedStatement.setCreatedAfter(after: LocalDateTime?) {
        if (after == null) {
            setNull(1, Types.TIMESTAMP)
        } else {
            setObject(1, after)
        }
    }

    private fun ResultSet.millis(column: String): Duration? = getObject(column, Double::class.javaObjectType)?.let {
        Duration.ofMillis(it.toLong())
    }

    private companion object {
        val PENDING_SQL = """
            SELECT count(*) AS pending
            FROM inbox_messages m
            JOIN message_statuses s ON s.id = m.status_id
            WHERE s.code IN ('TO_BE_PROCESSED', 'DELIVERY_ERROR')
        """.trimIndent()

        // Lag = processed_at - created_at of the messages the relay processed successfully; the order is created
        // in the same transaction, so it is also the time from storing the event to the order in the database
        val LAG_SQL = """
            WITH run AS (
                SELECT s.code = 'SUCCESS' AS processed,
                    (extract(epoch FROM m.processed_at - m.created_at) * 1000)::double precision AS lag_millis
                FROM inbox_messages m
                JOIN message_statuses s ON s.id = m.status_id
                WHERE m.created_at > coalesce(?::timestamp, '-infinity'::timestamp)
            )
            SELECT count(*) AS messages,
                count(*) FILTER (WHERE NOT processed) AS unprocessed,
                percentile_cont(0.50) WITHIN GROUP (ORDER BY lag_millis) FILTER (WHERE processed) AS p50,
                percentile_cont(0.95) WITHIN GROUP (ORDER BY lag_millis) FILTER (WHERE processed) AS p95,
                percentile_cont(0.99) WITHIN GROUP (ORDER BY lag_millis) FILTER (WHERE processed) AS p99,
                max(lag_millis) FILTER (WHERE processed) AS max
            FROM run
        """.trimIndent()

        // The application sets created_at when it stores a message, so first..last is the time it took to store them
        val STORED_SQL = """
            SELECT count(*) AS messages, min(created_at) AS first, max(created_at) AS last
            FROM inbox_messages
            WHERE created_at > coalesce(?::timestamp, '-infinity'::timestamp)
        """.trimIndent()
    }
}

/**
 * Lag of inbox messages, percentiles are of the processed messages only, `null` when none is processed yet.
 */
data class InboxLag(
    val messages: Int,
    val unprocessed: Int,
    val p50: Duration?,
    val p95: Duration?,
    val p99: Duration?,
    val max: Duration?,
) {
    override fun toString() = "$messages messages, $unprocessed not processed, lag of the processed ones: " +
        "p50 ${p50.inMillis()}, p95 ${p95.inMillis()}, p99 ${p99.inMillis()}, max ${max.inMillis()}"

    private fun Duration?.inMillis(): String = this?.let { "${it.toMillis()} ms" } ?: "-"
}

/**
 * Messages stored in the inbox and when the first and the last of them were stored, `null` when there are none.
 */
data class StoredMessages(
    val messages: Int,
    val first: LocalDateTime?,
    val last: LocalDateTime?,
) {
    override fun toString() = "$messages messages stored in the inbox, " +
        "${perSecond()?.let { "%.0f".format(it) } ?: "-"} per second from the first to the last"

    private fun perSecond(): Double? = if (first != null && last != null && last > first) {
        messages / Duration.between(first, last).toKotlinDuration().toDouble(DurationUnit.SECONDS)
    } else {
        null
    }
}
