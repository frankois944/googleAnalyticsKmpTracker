package io.github.frankois944.googleAnalyticsKmpTracker.sample

class JsPlatform: Platform {
    override val name: String = "Web with Kotlin/JS"
}

actual fun getPlatform(): Platform = JsPlatform()