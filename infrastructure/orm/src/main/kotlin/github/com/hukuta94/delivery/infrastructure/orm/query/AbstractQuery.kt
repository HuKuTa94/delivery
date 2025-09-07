package github.com.hukuta94.delivery.infrastructure.orm.query

import java.sql.ResultSet

abstract class AbstractQuery<RESPONSE_DTO> {

    abstract fun mapRowToResponseDto(rs: ResultSet): RESPONSE_DTO
}