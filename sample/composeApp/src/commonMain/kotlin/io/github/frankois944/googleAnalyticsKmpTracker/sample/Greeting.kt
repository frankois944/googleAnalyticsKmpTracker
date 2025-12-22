package io.github.frankois944.googleAnalyticsKmpTracker.sample

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}