package github.com.hukuta94.delivery.infrastructure.orm.ktorm

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import org.springframework.transaction.annotation.Transactional

open class KtormUnitOfWork : UnitOfWorkPort {

    @Transactional
    override fun executeInTransaction(action: () -> Unit) {
        action.invoke()
    }
}
