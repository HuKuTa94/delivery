package github.com.hukuta94.delivery

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import github.com.hukuta94.delivery.core.domain.DomainEvent
import github.com.hukuta94.delivery.core.domain.ValueObject
import github.com.hukuta94.delivery.core.domain.aggregate.Aggregate
import github.com.hukuta94.delivery.core.domain.service.DomainService
import org.junit.jupiter.api.Test

class DomainPackageTest {

    @Test
    fun `domain aggregate package can contain only aggregates, events and value objects`() {
        DOMAIN_AGGREGATE_PACKAGE onlyContains classes(
            Aggregate::class,
            DomainEvent::class,
            ValueObject::class,
        )
    }

    @Test
    fun `domain common package can contain only value objects`() {
        DOMAIN_COMMON_PACKAGE onlyContains ValueObject::class
    }

    @Test
    fun `domain service package can contain only domain services`() {
        DOMAIN_SERVICE_PACKAGE onlyContains DomainService::class
    }

    @Test
    fun `domain classes can not depend on classes that outside of the domain layer package`() {
        noClasses()
            .that()
            .resideInAnyPackage(DOMAIN_LAYER_PACKAGE)
            .should()
            .dependOnClassesThat()
            .resideOutsideOfPackages(
                DOMAIN_LAYER_PACKAGE,
                *DOMAIN_ALLOWED_OUTSIDE_PACKAGES,
            )
            .check(DOMAIN_LAYER_PACKAGE.classes())
    }
}