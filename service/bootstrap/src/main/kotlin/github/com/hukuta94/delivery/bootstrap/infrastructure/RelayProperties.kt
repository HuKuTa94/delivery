package github.com.hukuta94.delivery.bootstrap.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionOperations
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration

/**
 * Settings of the inbox and outbox relay jobs. Their interval, `delivery.relay.poll-interval`, isn't here:
 * the jobs' `@Scheduled` reads it as a placeholder.
 */
@ConfigurationProperties(prefix = "delivery.relay")
data class RelayProperties(
    val batchSize: Int = 50,
    val maxBatchesPerRun: Int = 20,
    val transactionTimeout: Duration = DEFAULT_TRANSACTION_TIMEOUT,
) {
    /**
     * @return the transaction of one relay batch, limited to [transactionTimeout]
     */
    fun batchTransaction(transactionManager: PlatformTransactionManager): TransactionOperations =
        TransactionTemplate(transactionManager).apply {
            timeout = transactionTimeout.toSeconds().toInt()
        }

    private companion object {
        val DEFAULT_TRANSACTION_TIMEOUT: Duration = Duration.ofSeconds(12)
    }
}
