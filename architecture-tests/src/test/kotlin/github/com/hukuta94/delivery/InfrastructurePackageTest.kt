package github.com.hukuta94.delivery

import io.kotest.core.spec.style.StringSpec

class InfrastructurePackageTest : StringSpec({

    "Infrastructure package dependencies are correct" {
        INFRASTRUCTURE_LAYER_PACKAGE onlyDependsOn packages(
            APPLICATION_PORT_PACKAGE,
            APPLICATION_EVENT_PACKAGE,
            APPLICATION_QUERY_PACKAGE,
            DOMAIN_LAYER_PACKAGE,
        )
    }
})
