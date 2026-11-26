package github.com.hukuta94.delivery.api.http.courier

import arrow.core.NonEmptyList
import github.com.hukuta94.delivery.core.application.query.courier.GetBusyCouriersQuery
import github.com.hukuta94.delivery.core.application.query.courier.CourierResponse
import github.com.hukuta94.delivery.core.application.usecase.courier.RegisterCourierUseCase
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class CourierControllerV1(
    private val getBusyCouriersQuery: GetBusyCouriersQuery,
    private val registerCourierUseCase: RegisterCourierUseCase,
) {

    @GetMapping("couriers/")
    fun getBusyCouriers(): ResponseEntity<List<CourierResponse>> {
        val response = getBusyCouriersQuery.execute()
        return ResponseEntity.ok(response.couriers)
    }

    /**
     * @return `201` with the id of the registered courier; `400` with a problem detail that has an entry
     * in its `errors` for every invalid field of the [request]; `409` with a problem detail
     * when a courier with the same id is already stored
     */
    @PostMapping("couriers/")
    fun registerCourier(@RequestBody request: RegisterCourierRequest): ResponseEntity<*> =
        registerCourierUseCase(request.toCommand()).fold(
            ifLeft = { errors ->
                val problemDetail = errors.toProblemDetail()
                ResponseEntity.status(problemDetail.status).body(problemDetail)
            },
            ifRight = { courierId ->
                ResponseEntity.status(HttpStatus.CREATED).body(RegisterCourierResponse(courierId))
            },
        )

    /**
     * The use case reports either invalid fields of the request, or an error of the request as a whole.
     */
    private fun NonEmptyList<RegisterCourierUseCase.Error>.toProblemDetail(): ProblemDetail {
        val invalidFields = mapNotNull { error ->
            error.pointer()?.let { pointer ->
                InvalidFieldResponse(
                    field = pointer,
                    detail = error.message,
                )
            }
        }
        if (invalidFields.isEmpty()) {
            return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                head.message,
            )
        }
        return ProblemDetail
            .forStatusAndDetail(HttpStatus.BAD_REQUEST, "Courier can not be registered")
            .apply { setProperty("errors", invalidFields) }
    }

    /**
     * @return JSON pointer to the field of the request the error is about, `null` if it is about no field
     */
    private fun RegisterCourierUseCase.Error.pointer(): String? = when (this) {
        is RegisterCourierUseCase.Error.InvalidName -> "#/name"
        is RegisterCourierUseCase.Error.InvalidTransport -> "#/transport"
        is RegisterCourierUseCase.Error.InvalidLocation -> "#/location"
        is RegisterCourierUseCase.Error.CourierAlreadyExists -> null
    }
}
