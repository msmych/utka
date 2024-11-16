package uk.matvey.utka.ktor.htmx

import kotlinx.html.HEAD
import kotlinx.html.HtmlBlockTag
import kotlinx.html.ScriptCrossorigin.anonymous
import kotlinx.html.script

object HtmxKit {

    const val HTMX_VERSION = "2.0.3"
    const val HTMX_INTEGRITY = "sha384-0895/pl2MU10Hqc6jd4RvrthNlDiE9U1tWmX7WRESftEDRosgxNsQG/Ze9YMRzHq"

    fun HEAD.htmxScript(version: String = HTMX_VERSION, integrity: String = HTMX_INTEGRITY) = script {
        this.src = "https://unpkg.com/htmx.org@$version"
        this.integrity = integrity
        this.crossorigin = anonymous
    }

    fun HtmlBlockTag.hxGet(
        path: String,
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        attributes["hx-get"] = path
        hx(target, swap, trigger, pushUrl, indicator)
    }

    fun HtmlBlockTag.hxPost(
        path: String,
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        attributes["hx-post"] = path
        hx(target, swap, trigger, pushUrl, indicator)
    }

    fun HtmlBlockTag.hxPut(
        path: String,
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        attributes["hx-post"] = path
        hx(target, swap, trigger, pushUrl, indicator)
    }

    fun HtmlBlockTag.hxPatch(
        path: String,
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        attributes["hx-patch"] = path
        hx(target, swap, trigger, pushUrl, indicator)
    }

    fun HtmlBlockTag.hxDelete(
        path: String,
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        attributes["hx-delete"] = path
        hx(target, swap, trigger, pushUrl, indicator)
    }

    fun HtmlBlockTag.hxSse(
        connect: String,
        swap: String,
    ) {
        attributes["hx-ext"] = "sse"
        attributes["sse-connect"] = connect
        attributes["sse-swap"] = swap
    }

    fun HtmlBlockTag.hx(
        target: String? = null,
        swap: String? = null,
        trigger: String? = null,
        pushUrl: Boolean? = null,
        indicator: String? = null,
    ) {
        target?.let { attributes["hx-target"] = it }
        swap?.let { attributes["hx-swap"] = it }
        trigger?.let { attributes["hx-trigger"] = it }
        pushUrl?.let { attributes["hx-push-url"] = it.toString() }
        indicator?.let { attributes["hx-indicator"] = it }
    }
}
