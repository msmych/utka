package uk.matvey.utka.ktor

import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.NoContent
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.sse.sse
import io.ktor.server.testing.testApplication
import io.ktor.sse.ServerSentEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.kit.random.RandomKit.randomAlphanumeric
import uk.matvey.utka.ktor.KtorKit.pathParam
import uk.matvey.utka.ktor.KtorKit.pathParamOrNull
import uk.matvey.utka.ktor.KtorKit.queryParam
import uk.matvey.utka.ktor.KtorKit.queryParamOrNull
import uk.matvey.utka.ktor.KtorKit.queryParams
import uk.matvey.utka.ktor.KtorKit.receiveParamsMap
import uk.matvey.utka.ktor.KtorKit.setFormData

class KtorKitTest {

    @Test
    fun `should access path params`() = testApplication {
        // given
        routing {
            get("/tests/{id}/path") {
                call.respondText(call.pathParam("id"))
            }
            get("/tests/optional/{id?}") {
                call.pathParamOrNull("id")?.let {
                    call.respondText(it)
                } ?: call.respond(NoContent, null)
            }
        }

        val pathParam = randomAlphanumeric(8)

        // when
        var rs = client.get("/tests/$pathParam/path")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo(pathParam)

        // when
        rs = client.get("/tests/optional/$pathParam")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo(pathParam)

        // when
        rs = client.get("/tests/optional")

        // then
        assertThat(rs.status).isEqualTo(NoContent)
    }

    @Test
    fun `should access query params`() = testApplication {
        // given
        routing {
            get("/tests/query-or-null") {
                call.respondText(call.queryParamOrNull("q") ?: "null")
            }
            get("/tests/query") {
                call.respondText(call.queryParam("q"))
            }
            get("/tests/queries") {
                call.respondText(call.queryParams("q").joinToString(","))
            }
        }
        val q = randomAlphanumeric(8)
        val qq = randomAlphanumeric(8)

        // when / then
        client.get("/tests/query-or-null").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).isEqualTo("null")
        }
        client.get("/tests/query-or-null?q=$q").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).isEqualTo(q)
        }
        client.get("/tests/query?q=$q").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText()).isEqualTo(q)
        }
        client.get("/tests/queries?q=$q&q=$qq").apply {
            assertThat(status).isEqualTo(OK)
            assertThat(bodyAsText().split(',')).containsExactlyInAnyOrder(q, qq)
        }
    }

    @Test
    fun `should send and receive form data`() = testApplication {
        // given
        routing {
            get("/tests/form-data") {
                val params = call.receiveParamsMap()
                call.respondText(params.entries.joinToString(",") { (k, v) -> "$k=$v" })
            }
        }
        val k1 = randomAlphanumeric(8)
        val v1 = randomAlphanumeric(8)
        val k2 = randomAlphanumeric(8)
        val v2 = randomAlphanumeric(8)

        // when
        val rs = client.get("/tests/form-data") {
            setFormData(k1 to v1, k2 to v2)
        }

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo("$k1=$v1,$k2=$v2")
    }

    @Test
    fun `should support SSE`() = testApplication {
        // given
        val s = randomAlphanumeric(8)
        install(io.ktor.server.sse.SSE)
        routing {
            sse("/tests/events") {
                send(ServerSentEvent(s))
            }
        }
        val client = createClient {
            install(SSE)
        }
        val flow = MutableSharedFlow<String?>(replay = 1)

        // when
        client.sse("/tests/events") {
            incoming.collect { event ->
                flow.emit(event.data)
            }
        }

        // then
        val result = withTimeoutOrNull(1000) {
            flow.firstOrNull()
        }
        assertThat(result).isEqualTo(s)
    }
}