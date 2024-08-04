package github.com.hukuta94.delivery.infrastructure.adapter.orm.model.converter

import github.com.hukuta94.delivery.core.domain.common.Location
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class LocationConverter : AttributeConverter<Location, String> {

    override fun convertToDatabaseColumn(attribute: Location): String {
        return "${attribute.abscissa},${attribute.ordinate}"
    }

    override fun convertToEntityAttribute(dbData: String): Location {
        val coordinate = dbData.split(',')
        return Location(
            abscissa = coordinate[0].toInt(),
            ordinate = coordinate[1].toInt(),
        )
    }
}