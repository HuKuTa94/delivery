package github.com.hukuta94.delivery.core.domain.common

import arrow.core.getOrElse
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MAX_COORDINATE_VALUE
import github.com.hukuta94.delivery.core.domain.LocationSpecification.MIN_COORDINATE_VALUE

fun location(
    x: Int,
    y: Int,
) = Location.of(x, y).getOrElse { error("Invalid coordinates in test fixture: $x $y") }

fun minLocation() = location(MIN_COORDINATE_VALUE, MIN_COORDINATE_VALUE)

fun maxLocation() = location(MAX_COORDINATE_VALUE, MAX_COORDINATE_VALUE)

fun randomLocation() = Location.random()