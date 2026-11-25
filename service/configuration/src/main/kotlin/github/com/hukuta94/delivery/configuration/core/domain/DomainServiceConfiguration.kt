package github.com.hukuta94.delivery.configuration.core.domain

import github.com.hukuta94.delivery.core.domain.rule.impl.CompleteOrderBusinessRuleImpl
import github.com.hukuta94.delivery.core.domain.rule.impl.DispatchOrderToCourierBusinessRuleImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class DomainServiceConfiguration {

    @Bean
    open fun dispatchService() = DispatchOrderToCourierBusinessRuleImpl()

    @Bean
    open fun completeOrderService() = CompleteOrderBusinessRuleImpl()
}
