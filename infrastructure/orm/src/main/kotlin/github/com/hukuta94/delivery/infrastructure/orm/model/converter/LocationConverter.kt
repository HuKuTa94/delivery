package github.com.hukuta94.delivery.infrastructure.orm.model.converter

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.common.Location
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class LocationConverter : AttributeConverter<Location, String> {

    override fun convertToDatabaseColumn(attribute: Location): String {
        return "${attribute.x},${attribute.y}"
    }

    override fun convertToEntityAttribute(dbData: String): Location {
        val coordinate = dbData.split(',')
        return Location.of(
            x = coordinate[0].toInt(),
            y = coordinate[1].toInt(),
        ).getOrElse { error(it.message) }
    }
}