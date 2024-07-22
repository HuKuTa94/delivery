package github.com.hukuta94.delivery.api.startup.configuration.adapter.inmemory

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    InMemoryOrderRepositoryConfiguration::class,
    InMemoryCourierRepositoryConfiguration::class,
)
open class InMemoryRepositoryConfiguration