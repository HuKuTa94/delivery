package github.com.hukuta94.delivery

import github.com.hukuta94.delivery.configuration.DeliveryApplicationConfiguration
import org.springframework.boot.runApplication

@Suppress(
    "SpreadOperator",
    "Reason: Spring requires spread operator to run application",
)
fun main(args: Array<String>) {
    runApplication<DeliveryApplicationConfiguration>(*args)
}
