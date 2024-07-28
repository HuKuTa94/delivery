package github.com.hukuta94.delivery.core.port.repository

interface UnitOfWorkPort {

    fun executeInTransaction(action: () -> Unit)
}