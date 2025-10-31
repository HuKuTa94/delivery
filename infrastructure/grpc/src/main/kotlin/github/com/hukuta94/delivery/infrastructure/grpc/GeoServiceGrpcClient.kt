package github.com.hukuta94.delivery.infrastructure.grpc

import arrow.core.Either
import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.infrastructure.grpc.GeoGrpc.newBlockingStub
import io.grpc.ManagedChannel

class GeoServiceGrpcClient(
    private val managedChannel: ManagedChannel,
): GeoGrpc.GeoImplBase(), GetLocationPort {

    override fun fromStreet(street: String): Either<Location.Error, Location> {
        val stub = newBlockingStub(managedChannel)

        val request = GetGeolocationRequest.newBuilder()
            .setStreet(street)
            .build()

        val response = stub.getGeolocation(request)

        return Location.of(
            x = response.location.x,
            y = response.location.y,
        )
    }
}