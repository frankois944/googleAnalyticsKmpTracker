@file:OptIn(ExperimentalWasmJsInterop::class)

package io.github.frankois944.matomoKMPTracker

// internal val version: String = js("process.version")
internal val userAgent: String = js("navigator.userAgent")

internal val width: Int = js("window.screen.width")

internal val height: Int = js("window.screen.height")

internal val languages: JsArray<JsString> = js("navigator.languages")

internal val hostname: String = js("window.location.hostname")
