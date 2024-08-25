package uk.matvey.utka.ktor

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.kit.random.RandomKit.randomAlphanumeric
import uk.matvey.utka.ktor.KtorKit.pathParam
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
        }
        val pathParam = randomAlphanumeric(8)

        // when
        val rs = client.get("/tests/$pathParam/path")

        // then
        assertThat(rs.status).isEqualTo(OK)
        assertThat(rs.bodyAsText()).isEqualTo(pathParam)
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
}