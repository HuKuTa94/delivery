package github.com.hukuta94.delivery.core.domain.common

import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE

fun newLocation(abscissa: Int, ordinate: Int) = Location(abscissa, ordinate)

fun newLocationWithMinCoords() = Location(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

fun newLocationWithMaxCoords() = Location(MAX_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

fun newLocationWithRandomCoords() = Location.random()