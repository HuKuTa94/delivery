package github.com.hukuta94.delivery.core.application.event.domain

import github.com.hukuta94.delivery.core.application.event.EventClassType
import github.com.hukuta94.delivery.core.domain.DomainEvent
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class DomainEventClassType(
    eventClass: KClass<out DomainEvent>
): EventClassType<DomainEvent>(eventClass) {

    override val eventClassPackage = EVENT_CLASS_PACKAGE
    override val eventClassPackageRegexPattern = EVENT_CLASS_PACKAGE_REGEX_PATTERN

    companion object {
        private const val EVENT_CLASS_PACKAGE = "github.com.hukuta94.delivery.core.domain."
        private val EVENT_CLASS_PACKAGE_REGEX_PATTERN = Regex(".*domain.")

        fun from(type: String): DomainEventClassType {
            return try {
                val eventClass = Class.forName(EVENT_CLASS_PACKAGE + type).kotlin
                if (DomainEvent::class.java.isAssignableFrom(eventClass.java)) {
                    DomainEventClassType(eventClass as KClass<out DomainEvent>)
                } else {
                    throw IllegalArgumentException(
                        "Class ${eventClass.qualifiedName} is not a subtype of IntegrationEvent"
                    )
                }
            } catch (ex: ClassNotFoundException) {
                throw IllegalArgumentException(
                    "Can't instantiate value object from type $type", ex
                )
            }
        }
    }
}
