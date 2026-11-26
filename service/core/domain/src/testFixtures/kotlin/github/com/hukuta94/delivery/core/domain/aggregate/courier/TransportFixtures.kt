package github.com.hukuta94.delivery.core.domain.aggregate.courier

fun unknownTransportError(code: String = "BIKE") = Transport.Error.UnknownTransport(code)
