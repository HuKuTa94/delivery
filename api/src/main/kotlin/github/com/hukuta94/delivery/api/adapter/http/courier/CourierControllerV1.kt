package github.com.hukuta94.delivery.api.adapter.http.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.query.GetBusyCouriersQuery
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
        val busyCouriers = getBusyCouriersQuery.execute().map {
            Courier(
                id = it.id,
                name = it.name
            )
        }
        return ResponseEntity.ok(busyCouriers)
    }
}

