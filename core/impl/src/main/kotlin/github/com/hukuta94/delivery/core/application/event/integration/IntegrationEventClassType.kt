package github.com.hukuta94.delivery.core.application.event.integration

import github.com.hukuta94.delivery.core.application.event.EventClassType
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class IntegrationEventClassType(
    eventClass: KClass<out IntegrationEvent>
): EventClassType<IntegrationEvent>(eventClass) {

    override val eventClassPackage = EVENT_CLASS_PACKAGE
    override val eventClassPackageRegexPattern = EVENT_CLASS_PACKAGE_REGEX_PATTERN

    companion object {
        private const val EVENT_CLASS_PACKAGE = "github.com.hukuta94.delivery.core.application.event.integration."
        private val EVENT_CLASS_PACKAGE_REGEX_PATTERN = Regex(".*integration.")

        fun from(type: String): IntegrationEventClassType {
            return try {
                val eventClass = Class.forName(EVENT_CLASS_PACKAGE + type).kotlin
                if (IntegrationEvent::class.java.isAssignableFrom(eventClass.java)) {
                    IntegrationEventClassType(eventClass as KClass<out IntegrationEvent>)
                } else {
                    throw IllegalArgumentException(
                        "Class ${eventClass.qualifiedName} is not a subtype of IntegrationEvent"
                    )
                }
            } catch (ex: ClassNotFoundException) {
                throw IllegalArgumentException(
                    "Can't instantiate value object from type $type",
                    ex
                )
            }
        }
    }
}
