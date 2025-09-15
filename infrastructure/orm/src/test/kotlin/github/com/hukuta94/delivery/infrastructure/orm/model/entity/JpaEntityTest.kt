package github.com.hukuta94.delivery.infrastructure.orm.model.entity

import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.*
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith

class JpaEntityTest : StringSpec({

    "creates domain aggregate from valid jpa entity" {
        // Given
        val jpaEntity = JpaEntityFake(
            domainAggregateClass = DomainAggregateFake::class,
            id = UUID.randomUUID(),
            publicField = "Public string field",
            readOnlyField = 12345L,
        )

        // When
        val domainAggregate = jpaEntity.toDomain()

        // Then
        assertSoftly {
            domainAggregate.id shouldBe jpaEntity.id
            domainAggregate.publicField shouldBe jpaEntity.publicField
            domainAggregate.readOnlyField shouldBe jpaEntity.readOnlyField
        }
    }

    "creates domain aggregate from valid jpa entity with nullable fields" {
        // Given
        val jpaEntity = JpaEntityFake(
            domainAggregateClass = DomainAggregateFake::class,
            id = UUID.randomUUID(),
            publicField = "Public string field",
            readOnlyField = null,
        )

        // When
        val domainAggregate = jpaEntity.toDomain()

        // Then
        assertSoftly {
            domainAggregate.id shouldBe jpaEntity.id
            domainAggregate.publicField shouldBe jpaEntity.publicField
            domainAggregate.readOnlyField shouldBe jpaEntity.readOnlyField
        }
    }

    "can not create domain aggregate from jpa entity with missing fields" {
        // Given
        class JpaEntityWithMissingFields(
            override val domainAggregateClass: KClass<DomainAggregateFake>,
            var id: UUID = UUID.randomUUID(),
            var publicField: String = "Public string field",
            // readOnlyField is missing
        ) : JpaEntity<DomainAggregateFake>()

        val jpaEntity = JpaEntityWithMissingFields(
            domainAggregateClass = DomainAggregateFake::class,
            id = UUID.randomUUID(),
            publicField = "Public string field",
        )

        // When/Then
        assertFailsWith<IllegalArgumentException> {
            jpaEntity.toDomain()
        }.message shouldBe
                "Field ${DomainAggregateFake::readOnlyField.name} of domain ${DomainAggregateFake::class.simpleName} " +
                "is missing for entity ${JpaEntityWithMissingFields::class.simpleName}"
    }

    "can not create domain aggregate from jpa entity with mismatched type fields" {
        // Given
        class JpaEntityWithMismatchedFieldType(
            override val domainAggregateClass: KClass<DomainAggregateFake>,
            var id: UUID = UUID.randomUUID(),
            var publicField: String = "Public string field",
            var readOnlyField: String = "Not a Long", // Mismatched field type
        ) : JpaEntity<DomainAggregateFake>()

        val mismatchedFieldName = DomainAggregateFake::readOnlyField.name

        val jpaEntity = JpaEntityWithMismatchedFieldType(
            domainAggregateClass = DomainAggregateFake::class,
            id = UUID.randomUUID(),
            publicField = "Public string field",
            readOnlyField = "Not a Long",
        )

        // When/Then
        assertFailsWith<IllegalArgumentException> {
            jpaEntity.toDomain()
        }.message shouldBe
                "Mismatched type of the field $mismatchedFieldName for domain ${DomainAggregateFake::class.simpleName}. " +
                "Must be ${DomainAggregateFake::class.field(mismatchedFieldName).type}. " +
                "Actual is ${JpaEntityWithMismatchedFieldType::class.field(mismatchedFieldName).type}"
    }
}) {
    companion object {
        private fun KClass<*>.field(name: String) = this.java.getDeclaredField(name)
    }

    class DomainAggregateFake(
        override val id: UUID,
        val publicField: String,
    ) : Aggregate<UUID>() {
        var readOnlyField: Long? = null
            private set

        companion object {
            private const val STATIC_FIELD_MUST_BE_EXCLUDED_FROM_MAPPING = 1
        }
    }

    class JpaEntityFake(
        override val domainAggregateClass: KClass<DomainAggregateFake>,
        var id: UUID,
        var publicField: String,
        var readOnlyField: Long? = null,
    ) : JpaEntity<DomainAggregateFake>() {
        companion object {
            private const val STATIC_FIELD_MUST_BE_EXCLUDED_FROM_MAPPING = 1
        }
    }
}
