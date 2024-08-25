package uk.matvey.utka

import io.ktor.server.routing.Route

interface Resource {

    fun Route.routing()
}
