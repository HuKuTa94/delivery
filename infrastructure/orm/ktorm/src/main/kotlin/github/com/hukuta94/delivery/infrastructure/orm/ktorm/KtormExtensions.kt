package github.com.hukuta94.delivery.infrastructure.orm.ktorm

import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.Column

/**
 * Extracts non-nullable data from [QueryRowSet] otherwise raises error
 */
inline fun <reified T : Any> QueryRowSet.require(column: Column<T>): T =
    this[column] ?: error("Column ${column.name} is null but expected ${T::class.simpleName}")
