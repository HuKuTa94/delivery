package github.com.hukuta94.delivery

import org.junit.jupiter.api.Test

class InfrastructurePackageTest {

    @Test
    fun `Infrastructure package dependencies are correct`() {
        INFRASTRUCTURE_LAYER_PACKAGE onlyDependsOn packages(
            APPLICATION_PORT_PACKAGE,
            APPLICATION_EVENT_PACKAGE,
            APPLICATION_QUERY_PACKAGE,
            DOMAIN_LAYER_PACKAGE,
        )
    }
}