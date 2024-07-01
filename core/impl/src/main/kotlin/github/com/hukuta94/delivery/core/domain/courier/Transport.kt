package github.com.hukuta94.delivery.core.domain.courier

enum class Transport(val speed: Int) {
    PEDESTRIAN(speed = 1),
    BICYCLE(speed = 2),
    CAR(speed = 3),
    DRONE(speed = 4),
}