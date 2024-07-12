package github.com.hukuta94.delivery.core.domain

import java.util.*

open class DomainEvent protected constructor() {
    val id: UUID = UUID.randomUUID()
}