package github.com.hukuta94.delivery.core.domain.aggregate.courier

enum class Transport(
    val id: Int,
    val speed: Int,
) {
    PEDESTRIAN(id = 1, speed = 1),
    BICYCLE(id = 2, speed = 2),
    CAR(id = 3, speed = 3),
    DRONE(id = 4, speed = 4),
    ;

    companion object {
        fun from(id: Int): Transport = entries
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Enum value is not found by id: $id")
    }
}
