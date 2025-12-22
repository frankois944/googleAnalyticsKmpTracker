package io.github.frankois944.googleAnalyticsKMPTracker.sample

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}