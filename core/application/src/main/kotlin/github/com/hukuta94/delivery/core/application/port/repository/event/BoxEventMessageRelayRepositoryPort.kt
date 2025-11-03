package github.com.hukuta94.delivery.core.application.port.repository.event

import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessage
import github.com.hukuta94.delivery.core.application.event.inoutbox.BoxEventMessageStatus

interface BoxEventMessageRelayRepositoryPort {

    fun saveAll(messages: List<BoxEventMessage>)

    fun findMessagesInStatuses(statuses: Set<BoxEventMessageStatus>): List<BoxEventMessage>
}