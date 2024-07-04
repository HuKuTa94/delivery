package github.com.hukuta94.delivery.api.startup

import github.com.hukuta94.delivery.api.startup.configuration.Configuration
import org.springframework.boot.runApplication

fun main(args: Array<String>) {
    runApplication<Configuration>(*args)
}