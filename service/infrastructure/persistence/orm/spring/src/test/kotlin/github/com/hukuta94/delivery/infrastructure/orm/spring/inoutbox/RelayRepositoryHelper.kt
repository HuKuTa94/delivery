package github.com.hukuta94.delivery.infrastructure.orm.spring.inoutbox

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import org.springframework.transaction.support.TransactionTemplate

/**
 * Reads relay messages in [status] inside a transaction. Both Spring relay repositories require one:
 * the outbox uses FOR UPDATE SKIP LOCKED and JPA queries need an active transaction in this context.
 * Shared by the inbox/outbox relay and repository integration specs.
 */
internal fun TransactionTemplate.messagesInStatus(
    repository: BoxEventMessageRelayRepositoryPort,
    status: BoxEventMessageStatus,
    batchSize: Int = 50,
): List<BoxEventMessage> = execute {
    repository.findMessagesInStatuses(setOf(status), batchSize)
}.orEmpty()
