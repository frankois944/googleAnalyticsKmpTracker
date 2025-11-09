package io.github.frankois944.matomoKMPTracker.queue

import io.github.frankois944.matomoKMPTracker.Event

public interface Queue {
    public suspend fun eventCount(): Long

    public suspend fun enqueue(events: List<Event>)

    /**
     *Returns the first `limit` events ordered by Event.date
     */
    public suspend fun first(limit: Long): List<Event>

    /**
     * Removes the events from the queue
     */
    public suspend fun remove(events: List<Event>)

    /**
     * Removes all events from the queue
     */
    public suspend fun removeAll()
}

public suspend fun Queue.enqueue(event: Event) {
    enqueue(events = listOf(event))
}
