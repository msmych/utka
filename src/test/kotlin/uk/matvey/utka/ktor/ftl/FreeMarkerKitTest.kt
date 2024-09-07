package uk.matvey.utka.ktor.ftl

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.kit.random.RandomKit.randomAlphanumeric
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.installFreeMarker
import uk.matvey.utka.ktor.ftl.FreeMarkerKit.respondFtl

class FreeMarkerKitTest {

    @Test
    fun `should respond free marker`() = testApplication {
        // given
        application {
            installFreeMarker("templates")
        }
        routing {
            get("/tests/free-marker/object") {
                data class UserRs(val name: String)
                call.respondFtl("tests/free-marker", UserRs(call.queryParam("name")))
            }
            get("/tests/free-marker/object-ftl") {
                data class UserRs(val name: String)
                call.respondFtl("tests/free-marker.ftl", UserRs(call.queryParam("name")))
            }
            get("/tests/free-marker/map") {
                call.respondFtl("tests/free-marker", "name" to call.queryParam("name"))
            }
        }
        val name = randomAlphanumeric(8)

        // when / then
        client.get("/tests/free-marker/object?name=$name").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).contains("<h1>Hello, $name!</h1>")
        }
        client.get("/tests/free-marker/object-ftl?name=$name").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).contains("<h1>Hello, $name!</h1>")
        }
        client.get("/tests/free-marker/map?name=$name").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).contains("<h1>Hello, $name!</h1>")
        }
    }
}