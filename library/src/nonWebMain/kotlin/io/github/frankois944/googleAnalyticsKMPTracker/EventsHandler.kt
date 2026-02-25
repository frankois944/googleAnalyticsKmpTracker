@file:OptIn(ExperimentalTime::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.Dispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.HttpClientDispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.events.Event
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection
import io.github.frankois944.googleAnalyticsKMPTracker.queue.database.DatabaseQueue
import io.github.frankois944.googleAnalyticsKMPTracker.queue.database.factory.DriverFactory
import io.github.frankois944.googleAnalyticsKMPTracker.queue.database.factory.createDatabase
import io.github.frankois944.googleAnalyticsKMPTracker.queue.enqueue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class EventsHandler(
    val measurementId: String,
    url: String?,
    apiSecret: String?,
    val userId: String?,
    val isOptedOut: Boolean,
) {
    val dispatcher: Dispatcher =
        HttpClientDispatcher(
            url ?: "https://www.google-analytics.com/mp/collect",
            requireNotNull(apiSecret) { "API secret cannot be null for non web platform" },
        ) { message ->
            println(message)
        }
    private val database =
        createDatabase(
            driverFactory = DriverFactory,
            dbName = measurementId.hashCode().toString(),
        )
    val queue = DatabaseQueue(database)
    val sessionId: Long = Clock.System.now().epochSeconds
    val visitor = Visitor.current()

    fun sendEvent(
        eventName: String,
        params: Map<String, Any>,
    ) {
        val event = Event()
        queue.enqueue(event)
    }

    fun config(
        configName: String,
        params: Map<String, Any?>,
    ) {
    }

    fun config(
        configName: String,
        value: String?,
    ) {
    }

    fun set(
        parameterName: String,
        params: Map<String, Any?>?,
    ) {
    }

    fun set(
        parameterName: String,
        value: String?,
    ) {
    }

    fun consent(selection: ConsentSelection) {
    }
}
