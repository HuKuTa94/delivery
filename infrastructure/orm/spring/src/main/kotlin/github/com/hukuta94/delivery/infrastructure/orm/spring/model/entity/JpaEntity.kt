package github.com.hukuta94.delivery.infrastructure.orm.spring.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Basic abstract JPA entity class with basic logic of conversion JPA entity to domain aggregate
 */
abstract class JpaEntity<DOMAIN_AGGREGATE : Aggregate<*>> {

    /**
     * This information is needed in basic conversion function toDomain()
     * for automatic conversion JPA entity to domain aggregate
     */
    protected abstract val domainAggregateClass: KClass<DOMAIN_AGGREGATE>

    /**
     * Converts JPA entity to domain aggregate
     *
     * @throws IllegalArgumentException if fields of JPA entity are different for domain aggregate
     */
    fun toDomain(): DOMAIN_AGGREGATE {
        val jpaEntityFields = jpaEntityFields().associateBy { it.name }
        val domainAggregate = createDomainAggregate(jpaEntityFields)
        mapJpaEntityFieldsToDomainAggregate(domainAggregate, jpaEntityFields)
        return domainAggregate
    }

    /**
     * Creates instance of domain aggregate using its primary constructor
     *
     * @param jpaEntityFields values of JPA entity's fields that are used in domain aggregate constructor
     */
    private fun createDomainAggregate(
        jpaEntityFields: Map<String, Field>
    ): DOMAIN_AGGREGATE {
        val constructor = domainAggregateClass.primaryConstructor
            ?: throw IllegalArgumentException(
                "Primary constructor is missing for domain ${domainAggregateClass.simpleName}"
            )

        val parameters = constructor.parameters

        val arguments = Array(parameters.size) { i ->
            val name = parameters[i].name
            when {
                name != null -> getFieldValue(jpaEntityFields.ofName(name))
                else -> null
            }
        }

        @Suppress(
            "SpreadOperator",
            "Reason: reflection API requires spread operator"
        )
        return constructor.call(*arguments)
    }

    private fun <DOMAIN_AGGREGATE : Aggregate<*>> mapJpaEntityFieldsToDomainAggregate(
        domainAggregateInstance: DOMAIN_AGGREGATE,
        jpaEntityFields: Map<String, Field>
    ) {
        domainAggregateInstance.fields().forEach { domainField ->
            val jpaEntityField = jpaEntityFields.ofName(domainField.name)
            try {
                domainAggregateInstance.mapJpaEntityFieldToDomainField(jpaEntityField, domainField)
            } catch (ex: IllegalArgumentException) {
                // Convert "Can not set..." message into more explicit with more information
                if (ex.message?.contains("Can not set", true) == true) {
                    throw IllegalArgumentException(
                        "Mismatched type of the field ${jpaEntityField.name} " +
                        "for domain ${domainAggregateClass.simpleName}. " +
                        "Must be ${domainField.type}. Actual is ${jpaEntityField.type}"
                    )
                }
                throw ex
            }
        }
    }

    private fun jpaEntityFields() = getFieldsOf(this::class)

    private fun Aggregate<*>.fields() = getFieldsOf(this::class)

    /**
     * @return public and private fields of the class excluding static fields
     */
    private fun getFieldsOf(kClass: KClass<*>): Collection<Field> {
        return kClass.java.declaredFields
            .filterNot { field ->
                Modifier.isStatic(field.modifiers) ||
                field.name in FIELD_EXCLUSIONS
            }
    }

    private fun Aggregate<*>.mapJpaEntityFieldToDomainField(jpaEntityField: Field, domainField: Field) {
        domainField.isAccessible = true
        domainField.set(
            this,
            getFieldValue(jpaEntityField)
        )
    }

    private fun getFieldValue(field: Field): Any? {
        field.isAccessible = true
        return field.get(this)
    }

    private fun Map<String, Field>.ofName(fieldName: String): Field {
        return this[fieldName]
            ?: throw IllegalArgumentException(
                missingFieldForEntityErrorMessage(fieldName)
            )
    }

    private fun missingFieldForEntityErrorMessage(fieldName: String): String {
        return "Field $fieldName of domain ${domainAggregateClass.simpleName} " +
               "is missing for entity ${this::class.simpleName}"
    }

    companion object {
        /**
         * Excluded fields from mapping
         */
        private val FIELD_EXCLUSIONS = setOf(
            JpaEntity<*>::domainAggregateClass.name
        )
    }
}
