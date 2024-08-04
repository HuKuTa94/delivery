package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.application.port.repository.UnitOfWorkPort
import org.springframework.transaction.annotation.Transactional

open class OrmUnitOfWorkAdapter : UnitOfWorkPort {

    @Transactional
    override fun executeInTransaction(action: () -> Unit) {
        action.invoke()
    }
}