package github.com.hukuta94.delivery.core.domain.aggregate.order

enum class OrderStatus(
    val id: Int,
) {
    CREATED(id = 1),
    ASSIGNED(id = 2),
    COMPLETED(id = 3),
    ;

    companion object {
        fun from(id: Int) = entries
            .firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Enum value is not found by id: $id")
    }
}
