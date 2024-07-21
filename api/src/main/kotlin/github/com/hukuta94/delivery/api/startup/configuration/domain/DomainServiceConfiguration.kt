package github.com.hukuta94.delivery.api.startup.configuration.domain

import github.com.hukuta94.delivery.core.domain.service.impl.CompleteOrderServiceImpl
import github.com.hukuta94.delivery.core.domain.service.impl.DispatchServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainServiceConfiguration {

    @Bean
    open fun dispatchService() = DispatchServiceImpl()

    @Bean
    open fun completeOrderService() = CompleteOrderServiceImpl()
}