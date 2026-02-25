package io.github.frankois944.googleAnalyticsKMPTracker.queue

import io.github.frankois944.googleAnalyticsKMPTracker.events.Event

internal interface Queue {
    suspend fun eventCount(): Long

    suspend fun enqueue(events: List<Event>)

    /**
     *Returns the first `limit` events ordered by Event.date
     */
    suspend fun first(limit: Long): List<Event>

    /**
     * Removes the events from the queue
     */
    suspend fun remove(events: List<Event>)

    /**
     * Removes all events from the queue
     */
    suspend fun removeAll()
}

internal suspend fun Queue.enqueue(event: Event) {
    enqueue(events = listOf(event))
}
