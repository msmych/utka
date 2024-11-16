package uk.matvey.utka.ktor

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.setBody
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveParameters
import io.ktor.server.util.getOrFail

object KtorKit {

    fun ApplicationCall.pathParam(name: String) = parameters.getOrFail(name)

    fun ApplicationCall.pathParamOrNull(name: String) = parameters[name]

    fun ApplicationCall.queryParamOrNull(name: String) = request.queryParameters[name]

    fun ApplicationCall.queryParam(name: String) = request.queryParameters.getOrFail(name)

    fun ApplicationCall.queryParams(name: String) = request.queryParameters.entries()
        .find { (k, _) -> k == name }
        ?.value
        ?: listOf()

    suspend fun ApplicationCall.receiveParamsMap() = receiveParameters().entries()
        .associate { (k, v) -> k to v.joinToString(";") }

    fun HttpRequestBuilder.setFormData(params: Map<String, String>) {
        setBody(
            FormDataContent(
                Parameters.build {
                    params.forEach { (k, v) -> append(k, v) }
                }
            )
        )
    }

    fun HttpRequestBuilder.setFormData(vararg params: Pair<String, String>) = setFormData(params.toMap())
}
