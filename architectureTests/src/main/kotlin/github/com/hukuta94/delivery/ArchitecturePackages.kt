package github.com.hukuta94.delivery

/**
 * Root group ID package of project.
 */
private const val GROUP_ID_PACKAGE = "github.com.hukuta94.delivery"

val PROGRAMMING_LANGUAGE_PACKAGES = arrayOf(
    "java..",
    "kotlin..",
    "org.jetbrains..",
)

/**
 * Frameworks are allowed in adapters and application layers only.
 * Put here framework package if you use it in layers above.
 */
val FRAMEWORK_PACKAGES = arrayOf(
    "com.google.protobuf..",
)

/* ------------------------------------------ API (primary/input) adapters ------------------------------------------ */
const val API_LAYER_PACKAGE = "$GROUP_ID_PACKAGE.api.."
/* ------------------------------------------------------------------------------------------------------------------ */


/* -------------------------------------------- Core - application layer -------------------------------------------- */
const val CORE_APPLICATION = "core.application"

const val APPLICATION_LAYER_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_APPLICATION.."
const val APPLICATION_PORT_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_APPLICATION.port.."
const val APPLICATION_EVENT_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_APPLICATION.event.."
const val APPLICATION_QUERY_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_APPLICATION.query.."
const val APPLICATION_USECASE_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_APPLICATION.usecase.."
/* ------------------------------------------------------------------------------------------------------------------ */


/* ---------------------------------------------- Core - domain layer ----------------------------------------------- */
const val CORE_DOMAIN = "core.domain"

const val DOMAIN_LAYER_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_DOMAIN.."
const val DOMAIN_COMMON_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_DOMAIN.common.."
const val DOMAIN_SERVICE_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_DOMAIN.service.."
const val DOMAIN_AGGREGATE_PACKAGE = "$GROUP_ID_PACKAGE.$CORE_DOMAIN.aggregate.."

val DOMAIN_ALLOWED_OUTSIDE_PACKAGES = PROGRAMMING_LANGUAGE_PACKAGES
/* ------------------------------------------------------------------------------------------------------------------ */


/* ----------------------------------- Infrastructure (secondary/output) adapters ----------------------------------- */
const val INFRASTRUCTURE_LAYER_PACKAGE = "$GROUP_ID_PACKAGE.infrastructure.."
/* ------------------------------------------------------------------------------------------------------------------ */


/**
 * All existing packages by layer with nesting packages.
 *
 * K - Layer package (parent)
 * V - Nesting packages
 */
val ALL_EXISTING_PACKAGES = mapOf(
    API_LAYER_PACKAGE to setOf(
        API_LAYER_PACKAGE,
    ),

    APPLICATION_LAYER_PACKAGE to setOf(
        APPLICATION_LAYER_PACKAGE,
        APPLICATION_PORT_PACKAGE,
        APPLICATION_EVENT_PACKAGE,
        APPLICATION_QUERY_PACKAGE,
        APPLICATION_USECASE_PACKAGE,
    ),

    DOMAIN_LAYER_PACKAGE to setOf(
        DOMAIN_LAYER_PACKAGE,
        DOMAIN_COMMON_PACKAGE,
        DOMAIN_SERVICE_PACKAGE,
        DOMAIN_AGGREGATE_PACKAGE,
    ),

    INFRASTRUCTURE_LAYER_PACKAGE to setOf(
        INFRASTRUCTURE_LAYER_PACKAGE,
    ),
)