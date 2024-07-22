package github.com.hukuta94.delivery.api.adapter.http.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCourierResponse
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.domain.courier.newCourier
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc()
//TODO Настроить конфигурацию, чтобы не поднимался весь контекст
@SpringBootTest(
    classes = [
        github.com.hukuta94.delivery.api.startup.configuration.Configuration::class,
    ]
)
internal class CourierControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var getBusyCouriersQuery: GetBusyCouriersQuery

    @Test
    fun `response ok if no couriers`() {
        given(getBusyCouriersQuery.execute()).willReturn(emptyList())

        mockMvc.get("/api/v1/couriers/")
            .andExpect {
                content {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("[]")
                    }
                }
            }
    }

    @Test
    fun `response ok if found couriers`() {
        val courier = newCourier()

        given(getBusyCouriersQuery.execute())
            .willReturn(
                listOf(
                    GetBusyCourierResponse(
                        id = courier.id,
                        name = courier.name.value,
                        locationAbscissa = courier.location.abscissa,
                        locationOrdinate = courier.location.ordinate,
                    )
            )
        )

        val expected = "[{\"id\":\"${courier.id}\",\"name\":\"${courier.name.value}\"}]"

        mockMvc.get("/api/v1/couriers/")
            .andExpect {
                content {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(expected)
                    }
                }
            }
    }
}