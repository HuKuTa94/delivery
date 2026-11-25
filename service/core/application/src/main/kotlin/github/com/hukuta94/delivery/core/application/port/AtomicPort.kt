package github.com.hukuta94.delivery.core.application.port

/**
 * Runs a suspend block atomically: everything it changes is committed together or not at all.
 * A coroutine-friendly, more granular alternative to `@Transactional` that also lets the block
 * roll back without throwing (see [AtomicOperationPort.rollback]).
 *
 * - The block completes: changes are committed and its result is returned,
 *   unless [AtomicOperationPort.rollback] was called, then they are rolled back.
 * - The block throws: changes are rolled back and the exception is rethrown.
 * - Called inside another atomic block: joins it, so a rollback in the inner block rolls back the outer one.
 *
 * ```
 * atomicOperator.executeAndAwait { operation ->
 *     debit(from, amount)
 *     credit(to, amount)
 *     if (!invariantsHold()) {
 *         operation.rollback()
 *     }
 * }
 * ```
 */
interface AtomicOperatorPort {

    suspend fun <OUT> executeAndAwait(block: suspend (AtomicOperationPort) -> OUT): OUT
}

/**
 * Handle of the atomic block that is running, passed by [AtomicOperatorPort.executeAndAwait].
 */
interface AtomicOperationPort {

    /**
     * Marks the atomic block for rollback. It returns normally: the block keeps running and its result is still
     * returned, but nothing it changed is committed. Calling it more than once has no further effect.
     */
    fun rollback()
}

/** Automatically rolls back when exceptions are thrown. */
suspend inline fun <T> doAtomicallyWith(
    atomicOperator: AtomicOperatorPort,
    crossinline block: suspend () -> T,
) = atomicOperator.executeAndAwait { block() }
