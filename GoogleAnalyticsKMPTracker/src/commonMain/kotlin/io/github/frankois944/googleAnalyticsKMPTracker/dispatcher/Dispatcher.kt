package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event

public interface Dispatcher {
    public val baseURL: String

    public val apiSecret: String

    @Throws(Throwable::class, IllegalArgumentException::class)
    public suspend fun sendBulkEvent(events: List<Event>)

    @Throws(Throwable::class, IllegalArgumentException::class)
    public suspend fun sendSingleEvent(event: Event)
}
