package github.com.hukuta94.delivery.api.adapter.http.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.Courier
import github.com.hukuta94.delivery.core.application.query.courier.response.GetCouriersResponse
import github.com.hukuta94.delivery.core.application.query.common.Location
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
    fun `response ok if no busy couriers`() {
        given(getBusyCouriersQuery.execute())
            .willReturn(
                GetCouriersResponse.empty()
            )

        mockMvc.get("/api/v1/couriers/")
            .andExpect {
                content {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("{\"couriers\":[]}")
                    }
                }
            }
    }

    @Test
    fun `response ok if found busy couriers`() {
        val courier = newCourier()

        given(getBusyCouriersQuery.execute())
            .willReturn(
                GetCouriersResponse(
                    couriers = listOf(
                        Courier(
                            id = courier.id,
                            name = courier.name.value,
                            location = Location(
                                x = courier.location.abscissa,
                                y = courier.location.ordinate,
                            ),
                            transportId = courier.transport.id,
                        )
                    )
                )
            )

        //TODO Вынести ожидаемый результат в файл json
        val expected = "{\"couriers\":[{\"id\":\"${courier.id}\",\"name\":\"${courier.name.value}\",\"location\":{\"x\":${courier.location.abscissa},\"y\":${courier.location.ordinate}},\"transportId\":${courier.transport.id}}]}"

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