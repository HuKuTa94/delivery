package github.com.hukuta94.delivery.infrastructure.orm.commons

import github.com.hukuta94.delivery.core.application.event.EventDeserializer
import github.com.hukuta94.delivery.core.application.event.EventPublisher
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageRelay
import github.com.hukuta94.delivery.core.application.port.repository.event.BoxEventMessageRelayRepositoryPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.support.TransactionOperations

/**
 * Relays the outbox every `delivery.relay.poll-interval`: a run processes batches one after another, each in its own
 * [transaction], while they are full and fully processed, so a backlog doesn't wait one interval per batch.
 * At most [maxBatchesPerRun] batches per run: all scheduled jobs share one thread, a run must not keep it.
 */
class OutboxEventMessageRelayJob(
    eventRepository: BoxEventMessageRelayRepositoryPort,
    eventDeserializer: EventDeserializer,
    eventPublisher: EventPublisher,
    batchSize: Int,
    private val maxBatchesPerRun: Int,
    private val transaction: TransactionOperations,
) : BoxEventMessageRelay(
    eventRepository = eventRepository,
    eventDeserializer = eventDeserializer,
    eventPublisher = eventPublisher,
    batchSize = batchSize,
) {
    @Scheduled(fixedDelayString = "\${delivery.relay.poll-interval}")
    fun pullMessagesFromOutbox() {
        repeat(maxBatchesPerRun) {
            if (transaction.execute { processBatch() } != true) return
        }
    }
}
