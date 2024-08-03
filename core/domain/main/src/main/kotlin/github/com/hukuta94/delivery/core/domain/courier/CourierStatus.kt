package github.com.hukuta94.delivery.core.domain.courier

enum class CourierStatus(
    val id: Int,
) {
    FREE(id = 1),
    BUSY(id = 2),
    ;

    companion object {
        fun from(id: Int) = CourierStatus.values()
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Enum value is not found by id: $id")
    }
}