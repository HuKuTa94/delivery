package github.com.hukuta94.delivery.core.application.event

import github.com.hukuta94.delivery.core.domain.DomainEvent
import kotlin.reflect.KClass

/**
 * The value object stores information about event class type and package
 */
abstract class EventClassType<EVENT : DomainEvent>(
    val value: KClass<out EVENT>
) {
    /**
     * Full path to package of event class
     */
    protected abstract val eventClassPackage: String

    /**
     * Regex expression to cut package path and leave class name of event type
     */
    protected abstract val eventClassPackageRegexPattern: Regex

    fun stringValue() = value.qualifiedName
        ?.replace(eventClassPackageRegexPattern, "")
        ?: error(qualifiedNameIsAbsentErrorMessage())

    private fun qualifiedNameIsAbsentErrorMessage(): String {
        return "Qualified name is absent for class: ${value.simpleName}"
    }
}
