package github.com.hukuta94.delivery.infrastructure.orm.commons.integration.concurrency

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Start gate for maximum contention on one action: parks every thread on a latch and releases them at once.
 * Races depend on timing, so run it for many rounds. A hang fails with an [AssertionError] after the timeout
 * instead of blocking the build.
 */
object StartGate {

    /**
     * Runs [action] on [threads] platform threads at the same moment and returns the outcome of every thread in
     * thread order. An action that threw is a failure here; whether it breaks the scenario is for the scenario to
     * decide. The whole run must finish within [timeout].
     */
    fun <T> run(
        threads: Int,
        timeout: Duration,
        action: (thread: Int) -> T,
    ): List<Result<T>> {
        require(threads > 0) { "threads must be positive: $threads" }
        val deadline = TimeSource.Monotonic.markNow() + timeout
        val ready = CountDownLatch(threads)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(threads)
        try {
            val outcomes = List(threads) { thread ->
                executor.submit<Result<T>> {
                    ready.countDown()
                    start.await()
                    runCatching { action(thread) }
                }
            }
            if (!ready.await(deadline.remainingMillis(), MILLISECONDS)) {
                throw AssertionError("$threads threads did not reach the start gate within $timeout")
            }
            start.countDown()
            return outcomes.map { outcome ->
                outcome.awaitUntil(
                    deadline = deadline,
                    timeout = timeout,
                )
            }
        } finally {
            executor.shutdownNow()
        }
    }

    private fun <T> Future<Result<T>>.awaitUntil(
        deadline: TimeMark,
        timeout: Duration,
    ): Result<T> = try {
        get(
            deadline.remainingMillis(),
            MILLISECONDS,
        )
    } catch (exception: TimeoutException) {
        throw AssertionError("start gate run did not finish within $timeout", exception)
    }

    private fun TimeMark.remainingMillis(): Long = (-elapsedNow()).inWholeMilliseconds.coerceAtLeast(0)
}

/**
 * Failed outcomes of [StartGate.run] counted by exception class: an assertion on it shows what failed and how often,
 * not only that something did.
 */
fun List<Result<*>>.failuresByType(): Map<String?, Int> = mapNotNull { it.exceptionOrNull() }
    .groupingBy { it::class.qualifiedName }
    .eachCount()
