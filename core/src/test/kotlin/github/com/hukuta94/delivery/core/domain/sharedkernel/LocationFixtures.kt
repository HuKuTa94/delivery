package github.com.hukuta94.delivery.core.domain.sharedkernel

fun newLocation(abscissa: Int, ordinate: Int) = Location(abscissa, ordinate)

fun minimalLocation() = Location.minimal()

fun maximalLocation() = Location.maximal()

fun randomLocation() = Location.random()