package github.com.hukuta94.delivery.api.adapter.http.courier

import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.response.Courier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1")
class CourierControllerV1(
    private val getBusyCouriersQuery: GetBusyCouriersQuery,
) {

    @GetMapping("couriers/")
    fun getBusyCouriers(): ResponseEntity<List<Courier>> {
        val response = getBusyCouriersQuery.execute()
        return ResponseEntity.ok(response.couriers)
    }
}

