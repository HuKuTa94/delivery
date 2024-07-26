package github.com.hukuta94.delivery.core.port

interface UnitOfWork {

    fun executeInTransaction(action: () -> Unit)
}