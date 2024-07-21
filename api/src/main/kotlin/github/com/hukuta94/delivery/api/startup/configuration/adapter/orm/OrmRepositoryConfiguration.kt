package github.com.hukuta94.delivery.api.startup.configuration.adapter.orm

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.adapter.orm.model.entity"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "github.com.hukuta94.delivery.infrastructure.adapter.orm.repository"
    ]
)
@Import(
    OrmOrderRepositoryConfiguration::class,
    OrmCourierRepositoryConfiguration::class,
)
open class OrmRepositoryConfiguration