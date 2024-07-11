package github.com.hukuta94.delivery.infrastructure.adapter.grpc

import github.com.hukuta94.delivery.core.domain.sharedkernel.Location
import github.com.hukuta94.delivery.core.port.GetLocationPort
import github.com.hukuta94.delivery.infrastructure.adapter.grpc.GeoGrpc.newBlockingStub
import io.grpc.ManagedChannelBuilder

class GeoServiceGrpcClient: GeoGrpc.GeoImplBase(), GetLocationPort {

    override fun getFromStreet(street: String): Location {
        //TODO конфигурацию адреса и порта можно вынести в конфиг
        val channel = ManagedChannelBuilder.forAddress("localhost", 8084)
            .usePlaintext() // disable SSL/TLS
            .build()

        val stub = newBlockingStub(channel)

        val request = GetGeolocationRequest.newBuilder()
            .setStreet(street)
            .build()

        val response = stub.getGeolocation(request)

        channel.shutdown()

        return Location(
            abscissa = response.location.x,
            ordinate = response.location.y,
        )
    }
}