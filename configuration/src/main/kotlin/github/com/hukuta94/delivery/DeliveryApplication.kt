package github.com.hukuta94.delivery

import github.com.hukuta94.delivery.configuration.DeliveryApplicationConfiguration
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<DeliveryApplicationConfiguration>(*args)
}