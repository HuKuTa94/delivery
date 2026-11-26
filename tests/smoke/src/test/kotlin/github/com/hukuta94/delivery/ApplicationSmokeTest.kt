package github.com.hukuta94.delivery

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

private val baseUrl: String = System.getProperty("delivery.base-url")
private val requestTimeout: Duration = Duration.ofSeconds(5)
private val client: HttpClient = HttpClient.newHttpClient()

class ApplicationSmokeTest : StringSpec({

    "running application lists couriers" {
        // Given
        val request = get("/api/v1/couriers/")

        // When
        val response = send(request)

        // Then
        response.statusCode() shouldBe 200
    }

    "running application registers a courier" {
        // Given
        val request = post(
            path = "/api/v1/couriers/",
            json = """{"name": "Smoke Courier", "transport": "BICYCLE", "location": {"x": 1, "y": 1}}""",
        )

        // When
        val response = send(request)

        // Then
        response.statusCode() shouldBe 201
    }

    "running application lists active orders" {
        // Given
        val request = get("/api/v1/orders/active/")

        // When
        val response = send(request)

        // Then
        response.statusCode() shouldBe 200
    }
})

private fun get(path: String): HttpRequest = request(path)
    .GET()
    .build()

private fun post(
    path: String,
    json: String,
): HttpRequest = request(path)
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(json))
    .build()

private fun request(path: String): HttpRequest.Builder = HttpRequest
    .newBuilder(URI.create("$baseUrl$path"))
    .timeout(requestTimeout)

/**
 * Sends the [request] without blocking the coroutine thread the test runs on.
 */
private suspend fun send(request: HttpRequest): HttpResponse<String> = client
    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .await()
