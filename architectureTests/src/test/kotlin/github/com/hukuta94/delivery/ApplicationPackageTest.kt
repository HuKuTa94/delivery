package github.com.hukuta94.delivery

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import io.kotlintest.assertSoftly
import org.junit.jupiter.api.Test

class ApplicationPackageTest {

    @Test
    fun `application event package dependencies are correct`() {
        APPLICATION_EVENT_PACKAGE onlyDependsOn packages(
            APPLICATION_USECASE_PACKAGE,
            APPLICATION_PORT_PACKAGE,
            DOMAIN_LAYER_PACKAGE
        )
    }

    @Test
    fun `application port package dependencies are correct`() {
        APPLICATION_PORT_PACKAGE onlyDependsOn DOMAIN_LAYER_PACKAGE
    }

    @Test
    fun `application query package does not have dependencies on other packages`() {
        APPLICATION_QUERY_PACKAGE onlyDependsOn self()
    }

    @Test
    fun `application usecase package can not depend on other application packages`() {
        APPLICATION_USECASE_PACKAGE onlyDependsOn packages(
            APPLICATION_PORT_PACKAGE,
            DOMAIN_LAYER_PACKAGE,
        )
    }

    @Test
    fun `application usecase package contains correct classes and interfaces`() {
        classes()
            .that()
            .resideInAPackage(APPLICATION_USECASE_PACKAGE)
            .and().areNotMemberClasses()
            .and().areNotAnonymousClasses()
            .should().haveSimpleNameEndingWith("UseCase").andShould().beInterfaces()
            .orShould().haveSimpleNameEndingWith("Impl").andShould().beAssignableTo(UseCase::class.java)
            .orShould().haveSimpleNameEndingWith("Command")
            .check(APPLICATION_USECASE_PACKAGE.classes())
    }

    @Test
    fun `application query package contains correct classes and interfaces`() {
        val packageClasses = APPLICATION_QUERY_PACKAGE.classes()

        assertSoftly {
            classes()
                .that()
                .haveSimpleNameEndingWith("Query").should().beInterfaces()
                .check(packageClasses)

            classes()
                .that()
                .haveSimpleNameEndingWith("Response").should().notBeInterfaces()
                .check(packageClasses)

            noClasses()
                .that()
                .resideInAPackage(APPLICATION_QUERY_PACKAGE)
                .should().haveSimpleNameEndingWith("Impl")
                .check(packageClasses)

            noClasses()
                .that()
                .resideInAPackage(APPLICATION_QUERY_PACKAGE)
                .should().implement(Query::class.java)
                .check(packageClasses)
        }
    }

    @Test
    fun `application port package contains only interfaces that have name ending with 'Port'`() {
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_PACKAGE)
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Port")
            .check(APPLICATION_PORT_PACKAGE.classes())
    }
}