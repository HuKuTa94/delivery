package github.com.hukuta94.delivery.api.adapter.http.order

import github.com.hukuta94.delivery.core.application.query.common.Location
import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.query.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.query.order.response.GetNotCompletedOrdersResponse
import github.com.hukuta94.delivery.core.application.query.order.response.Order
import github.com.hukuta94.delivery.core.domain.order.newOrder
import github.com.hukuta94.delivery.startup.configuration.DeliveryApplicationConfiguration
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
        DeliveryApplicationConfiguration::class,
    ]
)
internal class OrderControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var createOrderUseCase: CreateOrderUseCase

    @MockBean
    lateinit var getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery

    @Test
    fun `response ok if no active orders`() {
        given(getNotCompletedOrdersQuery.execute())
            .willReturn(
                GetNotCompletedOrdersResponse.empty()
            )

        mockMvc.get("/api/v1/orders/active/")
            .andExpect {
                content {
                    status { isOk() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("{\"orders\":[]}")
                    }
                }
            }
    }

    @Test
    fun `response ok if found active orders`() {
        val order = newOrder()

        given(getNotCompletedOrdersQuery.execute())
            .willReturn(
                GetNotCompletedOrdersResponse(
                    orders = listOf(
                        Order(
                            id = order.id,
                            location = Location(
                                x = order.location.abscissa,
                                y = order.location.ordinate,
                            )
                        )
                    )
                )
            )

        //TODO Вынести ожидаемый результат в файл json
        val expected = "{\"orders\":[{\"id\":\"${order.id}\",\"location\":{\"x\":${order.location.abscissa},\"y\":${order.location.ordinate}}}]}"

        mockMvc.get("/api/v1/orders/active/")
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