package io.github.frankois944.googleAnalyticsKMPTracker.core.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event

public interface Dispatcher {
    public val baseURL: String

    public val userAgent: String?

    @Throws(Throwable::class)
    public suspend fun sendPing(event: Event)

    @Throws(Throwable::class, IllegalArgumentException::class)
    public suspend fun send(events: List<Event>)
}
