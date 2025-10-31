package github.com.hukuta94.delivery.core.domain.common

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE

fun newLocation(
    x: Int,
    y: Int,
) = Location.of(x, y).getOrElse { error("Invalid coordinates in test fixture: $x $y") }

fun newLocationWithMinCoords() = newLocation(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

fun newLocationWithMaxCoords() = newLocation(MAX_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

fun newLocationWithRandomCoords() = Location.random()