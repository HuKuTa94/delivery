package github.com.hukuta94.delivery.core.application.event.integration

import github.com.hukuta94.delivery.core.domain.IntegrationEvent
import java.util.*

sealed class BasketIntegrationEvent(id: UUID) : IntegrationEvent(id)

class BasketConfirmedIntegrationEvent(id: UUID, val basketId: UUID, val street: String) : BasketIntegrationEvent(id)