package github.com.hukuta94.delivery.core.application.port.repository

interface UnitOfWorkPort {

    fun executeInTransaction(action: () -> Unit)
}
