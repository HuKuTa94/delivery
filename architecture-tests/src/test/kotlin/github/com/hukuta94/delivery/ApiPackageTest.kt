package github.com.hukuta94.delivery

import org.junit.jupiter.api.Test

class ApiPackageTest {

    @Test
    fun `API package dependencies are correct`() {
        API_LAYER_PACKAGE onlyDependsOn packages(
            APPLICATION_EVENT_PACKAGE,
            APPLICATION_QUERY_PACKAGE,
            APPLICATION_USECASE_PACKAGE,
        )
    }
}