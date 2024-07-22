package github.com.hukuta94.delivery.core.port

import github.com.hukuta94.delivery.core.application.event.DomainEventPublisher
import github.com.hukuta94.delivery.core.domain.Aggregate
import java.util.*

//TODO Переименовать дженерик в DOMAIN_AGGREGATE и параметры в domainAggregate
abstract class Repository<DOMAIN_ENTITY : Aggregate<*>> (
    private val domainEventPublisher: DomainEventPublisher,
) {

    abstract fun add(domainEntity: DOMAIN_ENTITY)

    abstract fun update(domainEntity: DOMAIN_ENTITY)

    abstract fun update(domainEntities: Collection<DOMAIN_ENTITY>)

    abstract fun getById(id: UUID): DOMAIN_ENTITY

    //TODO Обязанность за публикаую доменных событий должна быть в UnitOfWork.
    // Пока реализовано здесь до появления настоящей интеграции с БД
    /**
     * Must be called before save entity state in storage
     */
    protected fun publishDomainEvents(domainEntity: DOMAIN_ENTITY) {
        val domainEvents = domainEntity.domainEvents()
        domainEntity.clearDomainEvents()
        domainEventPublisher.publish(domainEvents)
    }
}