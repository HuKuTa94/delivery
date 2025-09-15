package github.com.hukuta94.delivery.infrastructure.grpc

import github.com.hukuta94.delivery.core.domain.common.Location
import github.com.hukuta94.delivery.core.application.port.GetLocationPort
import github.com.hukuta94.delivery.infrastructure.grpc.GeoGrpc.newBlockingStub
import io.grpc.ManagedChannel

class GeoServiceGrpcClient(
    private val managedChannel: ManagedChannel,
): GeoGrpc.GeoImplBase(), GetLocationPort {

    override fun getFromStreet(street: String): Location {
        val stub = newBlockingStub(managedChannel)

        val request = GetGeolocationRequest.newBuilder()
            .setStreet(street)
            .build()

        val response = stub.getGeolocation(request)

        return Location(
            x = response.location.x,
            y = response.location.y,
        )
    }
}