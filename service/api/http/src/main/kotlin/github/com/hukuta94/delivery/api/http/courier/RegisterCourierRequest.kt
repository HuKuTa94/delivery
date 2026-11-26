package github.com.hukuta94.delivery.api.http.courier

import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierCommand

data class RegisterCourierRequest(
    val name: String,
    val transport: String,
    val location: Location,
) {
    fun toCommand() = RegisterCourierCommand(
        name = name,
        transport = transport,
        location = RegisterCourierCommand.Location(
            x = location.x,
            y = location.y,
        ),
    )

    data class Location(
        val x: Int,
        val y: Int,
    )
}
