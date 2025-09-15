package github.com.hukuta94.delivery

import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import github.com.hukuta94.delivery.core.application.query.Query
import github.com.hukuta94.delivery.core.application.usecase.UseCase
import io.kotest.core.spec.style.StringSpec

class ApplicationPackageTest : StringSpec({

    "application event package dependencies are correct" {
        APPLICATION_EVENT_PACKAGE onlyDependsOn packages(
            APPLICATION_USECASE_PACKAGE,
            APPLICATION_PORT_PACKAGE,
            DOMAIN_LAYER_PACKAGE
        )
    }

    "application port package can depend on domain package" {
        APPLICATION_PORT_PACKAGE onlyDependsOn DOMAIN_LAYER_PACKAGE
    }

    "application query package can not depend on other packages" {
        APPLICATION_QUERY_PACKAGE onlyDependsOn self()
    }

    "application usecase package can not depend on other application packages" {
        APPLICATION_USECASE_PACKAGE onlyDependsOn packages(
            APPLICATION_PORT_PACKAGE,
            DOMAIN_LAYER_PACKAGE,
        )
    }

    "application usecase package contains correct classes and interfaces" {
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

    "application query package must contain only interfaces with suffix Query" {
        classes()
            .that()
            .haveSimpleNameEndingWith("Query")
            .should().beInterfaces()
            .check(APPLICATION_QUERY_CLASSES)
    }

    "application query package must contain only classes (not interfaces) with suffix Response" {
        classes()
            .that()
            .haveSimpleNameEndingWith("Response")
            .should().notBeInterfaces()
            .check(APPLICATION_QUERY_CLASSES)
    }

    "application query package must not contain classes with suffix Impl" {
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_QUERY_PACKAGE)
            .should().haveSimpleNameEndingWith("Impl")
            .check(APPLICATION_QUERY_CLASSES)
    }

    "application query package must not contain classes implementing Query interface" {
        noClasses()
            .that()
            .resideInAPackage(APPLICATION_QUERY_PACKAGE)
            .should().implement(Query::class.java)
            .check(APPLICATION_QUERY_CLASSES)
    }

    "application port package contains only interfaces that have name ending with 'Port'" {
        classes()
            .that()
            .resideInAPackage(APPLICATION_PORT_PACKAGE)
            .should().beInterfaces()
            .andShould().haveSimpleNameEndingWith("Port")
            .check(APPLICATION_PORT_PACKAGE.classes())
    }
}) {
    companion object {
        private val APPLICATION_QUERY_CLASSES = APPLICATION_QUERY_PACKAGE.classes()
    }
}
