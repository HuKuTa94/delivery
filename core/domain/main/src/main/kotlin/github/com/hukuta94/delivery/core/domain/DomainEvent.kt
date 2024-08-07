package github.com.hukuta94.delivery.core.domain

import java.util.*

/**
 * These events are generated by core in domain layer.
 * ID is computed when instance is being creating.
 *
 * Domain events are used inside application and can be sent to
 * external services by mapping into specific transport protocol.
 */
open class DomainEvent {

    var id: UUID
        private set

    /**
     * For domain events id are generated automatically
     */
    protected constructor() {
        this.id = UUID.randomUUID()
    }

    protected constructor(id: UUID) {
        this.id = id
    }
}