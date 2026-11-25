package github.com.hukuta94.delivery

import io.kotest.core.spec.style.StringSpec

class ApiPackageTest : StringSpec({

    "API package dependencies are correct" {
        API_LAYER_PACKAGE onlyDependsOn packages(
            APPLICATION_EVENT_PACKAGE,
            APPLICATION_QUERY_PACKAGE,
            APPLICATION_USECASE_PACKAGE,
        )
    }
})
