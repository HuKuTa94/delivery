package github.com.hukuta94.delivery.infrastructure.adapter.orm.repository

import github.com.hukuta94.delivery.core.port.UnitOfWork
import org.springframework.transaction.annotation.Transactional

open class OrmUnitOfWork : UnitOfWork {

    @Transactional
    override fun executeInTransaction(action: () -> Unit) {
        action.invoke()
    }
}