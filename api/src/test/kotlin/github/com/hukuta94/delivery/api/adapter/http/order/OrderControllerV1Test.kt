package github.com.hukuta94.delivery.api.adapter.http.order

import github.com.hukuta94.delivery.core.application.usecase.order.CreateOrderUseCase
import github.com.hukuta94.delivery.core.application.usecase.order.GetNotCompletedOrdersQuery
import github.com.hukuta94.delivery.core.application.usecase.order.GetNotCompletedOrderResponse
import github.com.hukuta94.delivery.core.domain.order.newOrder
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
internal class OrderControllerV1Test {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var createOrderUseCase: CreateOrderUseCase

    @MockBean
    lateinit var getNotCompletedOrdersQuery: GetNotCompletedOrdersQuery

    @Test
    fun `response ok if no active orders`() {
        given(getNotCompletedOrdersQuery.execute()).willReturn(emptyList())

        mockMvc.get("/api/v1/orders/active/")
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
    fun `response ok if found active orders`() {
        val order = newOrder()

        given(getNotCompletedOrdersQuery.execute())
            .willReturn(
                listOf(
                    GetNotCompletedOrderResponse(
                        orderId = order.id,
                        locationAbscissa = order.location.abscissa,
                        locationOrdinate = order.location.ordinate,
                    )
                )
            )

        val expected = "[{\"id\":\"${order.id}\",\"courierLocation\":{\"abscissaValue\":${order.location.abscissa},\"ordinateValue\":${order.location.ordinate}}}]"

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