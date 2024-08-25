package uk.matvey.utka.ktor.ftl

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.freemarker.FreeMarker
import io.ktor.server.freemarker.FreeMarkerContent
import io.ktor.server.response.respond

object FreeMarkerKit {

    fun Application.installFreeMarker(
        path: String,
        configure: freemarker.template.Configuration.() -> Unit = {}
    ) {
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, path)
            configure()
        }
    }

    suspend fun ApplicationCall.respondFtl(template: String, model: Any? = null) {
        respond(FreeMarkerContent("$template.ftl", model))
    }

    suspend fun ApplicationCall.respondFtl(template: String, vararg values: Pair<String, Any?>) {
        respondFtl(template, values.toMap())
    }
}