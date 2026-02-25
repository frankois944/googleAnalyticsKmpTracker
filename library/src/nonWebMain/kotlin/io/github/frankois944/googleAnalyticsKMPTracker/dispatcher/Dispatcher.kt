package io.github.frankois944.googleAnalyticsKMPTracker.dispatcher

import io.github.frankois944.googleAnalyticsKMPTracker.events.Event

internal interface Dispatcher {
    val baseURL: String?

    val apiSecret: String

    suspend fun sendBulkEvent(events: List<Event>)

    suspend fun sendSingleEvent(event: Event)
}
