package github.com.hukuta94.delivery.api.http.courier

import com.jayway.jsonpath.JsonPath
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryFake
import github.com.hukuta94.delivery.core.application.port.repository.domain.CourierRepositoryPort
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.GetCouriersResponse
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Courier
import github.com.hukuta94.delivery.core.domain.aggregate.courier.Transport
import github.com.hukuta94.delivery.core.domain.aggregate.courier.courierName
import github.com.hukuta94.delivery.core.domain.aggregate.courier.unknownTransportError
import github.com.hukuta94.delivery.core.domain.common.location
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.UUID

internal class CourierControllerV1Test : StringSpec({

    val courierRepository = CourierRepositoryFake()
    val noBusyCouriersQuery = object : GetBusyCouriersQuery {
        override fun execute() = GetCouriersResponse(couriers = emptyList())
    }

    fun courierController(courierRepository: CourierRepositoryPort) = CourierControllerV1(
        getBusyCouriersQuery = noBusyCouriersQuery,
        registerCourierUseCase = RegisterCourierUseCase(
            courierRepository = courierRepository,
        ),
    )

    val sut = courierController(courierRepository)

    fun CourierControllerV1.registerCourier(body: String): MockHttpServletResponse = MockMvcBuilders
        .standaloneSetup(this)
        .build()
        .post("/api/v1/couriers/") {
            contentType = MediaType.APPLICATION_JSON
            content = body
        }
        .andReturn()
        .response

    fun <T> MockHttpServletResponse.json(path: String): T = JsonPath.read(
        getContentAsString(Charsets.UTF_8),
        path,
    )

    beforeTest {
        courierRepository.clear()
    }

    "courier is registered with the name, transport and location of the request" {
        // Given
        val body = """{ "name": "Ivan", "transport": "${Transport.BICYCLE.name}", "location": { "x": 3, "y": 7 } }"""

        // When
        val response = sut.registerCourier(body)

        // Then
        response.status shouldBe HttpStatus.CREATED.value()
        val courier = courierRepository.getById(UUID.fromString(response.json("$.id")))
        assertSoftly {
            courier.name shouldBe courierName(value = "Ivan")
            courier.transport shouldBe Transport.BICYCLE
            courier.location shouldBe location(3, 7)
        }
    }

    "request with an invalid name, transport and location is rejected with an error for each of them" {
        // Given
        val body = """{ "name": " ", "transport": "BIKE", "location": { "x": 0, "y": 11 } }"""

        // When
        val response = sut.registerCourier(body)

        // Then
        assertSoftly {
            response.status shouldBe HttpStatus.BAD_REQUEST.value()
            response.contentType shouldBe MediaType.APPLICATION_PROBLEM_JSON_VALUE
            response.json<List<String>>("$.errors[*].field") shouldContainExactly listOf(
                "#/name",
                "#/transport",
                "#/location",
            )
            courierRepository.getAllFree().shouldBeEmpty()
        }
    }

    "error of an invalid field tells the business rule that the field breaks" {
        // Given
        val body = """{ "name": "Ivan", "transport": "BIKE", "location": { "x": 3, "y": 7 } }"""

        // When
        val response = sut.registerCourier(body)

        // Then
        assertSoftly {
            response.status shouldBe HttpStatus.BAD_REQUEST.value()
            response.json<String>("$.errors[0].detail") shouldBe unknownTransportError(code = "BIKE").message
        }
    }

    "request without a courier name is rejected" {
        // Given
        val body = """{ "transport": "${Transport.BICYCLE.name}", "location": { "x": 3, "y": 7 } }"""

        // When
        val response = sut.registerCourier(body)

        // Then
        assertSoftly {
            response.status shouldBe HttpStatus.BAD_REQUEST.value()
            courierRepository.getAllFree().shouldBeEmpty()
        }
    }

    "registration of a courier whose id is already taken is a conflict" {
        // Given
        val repositoryWithTakenIds = object : CourierRepositoryPort by courierRepository {
            override fun add(aggregate: Courier) = false
        }
        val sutWithTakenIds = courierController(repositoryWithTakenIds)
        val body = """{ "name": "Ivan", "transport": "${Transport.BICYCLE.name}", "location": { "x": 3, "y": 7 } }"""

        // When
        val response = sutWithTakenIds.registerCourier(body)

        // Then
        assertSoftly {
            response.status shouldBe HttpStatus.CONFLICT.value()
            response.contentType shouldBe MediaType.APPLICATION_PROBLEM_JSON_VALUE
            response.json<String>("$.detail") shouldMatch "Courier with id: [0-9a-f-]{36} already exists"
        }
    }
})
