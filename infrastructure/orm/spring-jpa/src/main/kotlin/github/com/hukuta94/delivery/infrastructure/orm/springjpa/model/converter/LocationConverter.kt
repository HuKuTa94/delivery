package github.com.hukuta94.delivery.infrastructure.orm.springjpa.model.converter

import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.infrastructure.orm.commons.fromDb
import github.com.hukuta94.delivery.infrastructure.orm.commons.toDb
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class LocationConverter : AttributeConverter<Location, String> {

    override fun convertToDatabaseColumn(attribute: Location): String {
        return attribute.toDb()
    }

    override fun convertToEntityAttribute(dbData: String): Location {
        return Location.fromDb(dbData)
    }
}