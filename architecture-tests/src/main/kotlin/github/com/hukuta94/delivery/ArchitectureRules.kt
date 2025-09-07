package github.com.hukuta94.delivery

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.ClassesShould
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction
import com.tngtech.archunit.lang.syntax.elements.GivenClasses
import kotlin.reflect.KClass

fun <T> self(): Set<T> = emptySet()

fun <T> packages(vararg packages: T): Collection<T> = setOf(*packages)

fun classes(vararg classes: KClass<*>): Collection<KClass<*>> = setOf(*classes)

fun String.classes(): JavaClasses = ClassFileImporter()
    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
    .importPackages(this)

infix fun String.onlyContains(clazz: KClass<*>) {
    this.onlyContains(classes(clazz))
}

infix fun String.onlyContains(classes: Collection<KClass<*>>) {
    val javaClasses = classes.map { it.java }
    val importedClasses = this.classes()

    var rule: Any = ArchRuleDefinition.classes()
        .that().resideInAnyPackage(this)
        .and().areNotEnums()
        .and().areNotMemberClasses()
        .should()

    javaClasses.forEachIndexed { index, clazz ->
        val isLast = index == javaClasses.lastIndex

        rule = when (rule) {
            is ClassesShould -> when {
                isLast -> (rule as ClassesShould).beAssignableTo(clazz)
                else -> (rule as ClassesShould).beAssignableTo(clazz).orShould()
            }
            else -> error("Impossible and unreachable error of \"else\" branch")
        }
    }

    (rule as ClassesShouldConjunction).check(importedClasses)
}

infix fun String.onlyDependsOn(shouldDependOn: String) {
    this.onlyDependsOn(packages(shouldDependOn))
}

infix fun String.onlyDependsOn(shouldDependOn: Collection<String>) {
    val allowedPackages = mutableSetOf(this)

    shouldDependOn.forEach { currentPackage ->
        ALL_EXISTING_PACKAGES[currentPackage]?.let { nestingPackages ->
            allowedPackages.addAll(nestingPackages)
        }
    }

    val shouldNotDependOn = ALL_EXISTING_PACKAGES.values
        .flatten()
        .filterNot {
            val isParentPackage = it.contains(APPLICATION_LAYER_PACKAGE) || it.contains(DOMAIN_LAYER_PACKAGE)
            allowedPackages.contains(it) || shouldDependOn.contains(it) || isParentPackage
        }

    val canDependOnFrameworkPackages = !this.contains(CORE_DOMAIN)
    val frameworkPackages = if (canDependOnFrameworkPackages) { FRAMEWORK_PACKAGES } else { arrayOf() }

    val importedClasses = this.classes()

    // Check allowed dependencies
    checkDependencies(
        fromPackage = this,
        toPackages =
            shouldDependOn.toTypedArray() +
            PROGRAMMING_LANGUAGE_PACKAGES + // include programming language packages
            frameworkPackages,              // include frameworks for adapters only
        givenClasses = ArchRuleDefinition.classes(),
        importedClasses = importedClasses,
    )

    // Check forbidden dependencies
    checkDependencies(
        fromPackage = this,
        toPackages = shouldNotDependOn.toTypedArray(),
        givenClasses = ArchRuleDefinition.noClasses(),
        importedClasses = importedClasses,
    )
}

private fun checkDependencies(
    fromPackage: String,
    toPackages: Array<String>,
    givenClasses: GivenClasses,
    importedClasses: JavaClasses,
) {
    givenClasses
        .that().resideInAnyPackage(fromPackage)
        .and().areNotEnums()
        .and().areNotMemberClasses()
        .and().areNotAnonymousClasses()
        .should().dependOnClassesThat()
        .resideInAnyPackage(*toPackages)
        .allowEmptyShould(true)
        .check(importedClasses)
}
