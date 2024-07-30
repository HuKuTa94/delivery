package github.com.hukuta94.delivery.startup

import github.com.hukuta94.delivery.startup.configuration.DeliveryApplicationConfiguration
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<DeliveryApplicationConfiguration>(*args)
}