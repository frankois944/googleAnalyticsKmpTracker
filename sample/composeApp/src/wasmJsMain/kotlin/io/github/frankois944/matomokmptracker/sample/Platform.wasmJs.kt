package io.github.frankois944.googleAnalyticsKMPTracker.sample

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()