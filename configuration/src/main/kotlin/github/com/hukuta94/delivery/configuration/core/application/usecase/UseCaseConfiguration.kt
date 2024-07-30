package github.com.hukuta94.delivery.configuration.core.application.usecase

import github.com.hukuta94.delivery.core.application.usecase.event.impl.SaveIntegrationEventUseCaseImpl
import github.com.hukuta94.delivery.core.port.repository.event.InboxEventRepositoryPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    CourierUseCaseConfiguration::class,
    OrderUseCaseConfiguration::class,
)
open class UseCaseConfiguration {

    @Bean
    open fun saveIntegrationEventUseCase(
        inboxEventRepositoryPort: InboxEventRepositoryPort,
    ) = SaveIntegrationEventUseCaseImpl(
        inboxEventRepositoryPort = inboxEventRepositoryPort,
    )
}