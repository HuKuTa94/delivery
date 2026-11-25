package github.com.hukuta94.delivery

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.GivenClasses
import kotlin.reflect.KClass

// Kinds of `@kotlin.Metadata(k = ...)` the compiler writes into every class file.
private const val KOTLIN_FILE_FACADE_KIND = 2
private const val KOTLIN_SYNTHETIC_CLASS_KIND = 3

/**
 * Class the Kotlin compiler generates for the top-level declarations of a file (`FooKt`).
 */
val KOTLIN_FILE_FACADE: DescribedPredicate<JavaClass> = kotlinClassOfKind(
    description = "Kotlin file facades",
    KOTLIN_FILE_FACADE_KIND,
)

/**
 * [KOTLIN_FILE_FACADE] or a synthetic class, e.g. a lambda of a top-level function (`FooKt$bar$1`).
 */
val KOTLIN_FILE_FACADE_OR_SYNTHETIC_CLASS: DescribedPredicate<JavaClass> = kotlinClassOfKind(
    description = "Kotlin file facades or synthetic classes",
    KOTLIN_FILE_FACADE_KIND,
    KOTLIN_SYNTHETIC_CLASS_KIND,
)

/**
 * The class declares no fields. On a [KOTLIN_FILE_FACADE] it means the file has no top-level properties (state).
 */
val HAVE_NO_FIELDS: ArchCondition<JavaClass> = object : ArchCondition<JavaClass>("have no fields") {
    override fun check(
        item: JavaClass,
        events: ConditionEvents,
    ) {
        item.fields.forEach { field ->
            events.add(SimpleConditionEvent.violated(field, "${field.description} is not allowed"))
        }
    }
}

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

    if (javaClasses.isEmpty()) return

    val base = ArchRuleDefinition.classes()
        .that().resideInAnyPackage(this)
        .and().areNotEnums()
        .and().areNotMemberClasses()
        .should()

    var rule = base.beAssignableTo(javaClasses.first())

    for (clazz in javaClasses.drop(1)) {
        rule = rule.orShould().beAssignableTo(clazz)
    }

    rule.check(importedClasses)
}

infix fun String.onlyDependsOn(shouldDependOn: String) {
    this.onlyDependsOn(packages(shouldDependOn))
}

infix fun String.onlyDependsOn(shouldDependOn: Collection<String>) {
    val allowedPackages = mutableSetOf(this)

    shouldDependOn.forEach { currentPackage ->
        ALL_PROJECT_PACKAGES[currentPackage]?.let { nestingPackages ->
            allowedPackages.addAll(nestingPackages)
        }
    }

    val shouldNotDependOn = ALL_PROJECT_PACKAGES.values
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
            PROGRAMMING_LANGUAGE_PACKAGES +
            frameworkPackages,
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

private fun kotlinClassOfKind(
    description: String,
    vararg kinds: Int,
): DescribedPredicate<JavaClass> = DescribedPredicate.describe(description) { javaClass ->
    javaClass.tryGetAnnotationOfType(Metadata::class.java)
        .map { metadata -> metadata.kind in kinds }
        .orElse(false)
}

@Suppress(
    "SpreadOperator",
    "Reason: ArchUnit requires spread operator"
)
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
