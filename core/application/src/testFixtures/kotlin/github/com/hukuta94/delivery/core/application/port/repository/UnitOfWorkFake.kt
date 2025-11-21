package github.com.hukuta94.delivery.core.application.port.repository

class UnitOfWorkFake : UnitOfWorkPort {

    override fun executeInTransaction(action: () -> Unit) {
        action.invoke()
    }
}