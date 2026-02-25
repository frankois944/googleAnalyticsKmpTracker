@file:OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.queue.database

import io.github.frankois944.googleAnalyticsKMPTracker.events.Event
import io.github.frankois944.googleAnalyticsKMPTracker.queue.Queue
import io.github.frankois944.googleAnalyticsKmpTracker.database.schema.CacheDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.time.Duration.Companion.hours
import kotlin.uuid.ExperimentalUuidApi

internal class DatabaseQueue(
    val database: CacheDatabase,
) : Queue {
    private val mutex = Mutex()
    private var requireCleanup = true

    override suspend fun eventCount(): Long = database.trackingCacheQueries.count().executeAsOne()

    private val cbor =
        Cbor {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

    override suspend fun enqueue(events: List<Event>): Unit =
        mutex.withLock {
            events.forEach { event ->
                database.trackingCacheQueries.insertUuid(
                    uuid = event.uuid,
                    measurementId = event.measurementId,
                    eventDateInMs = event.dateCreatedInMs,
                    event = cbor.encodeToByteArray(event),
                )
            }
        }

    override suspend fun first(limit: Long): List<Event> =
        mutex.withLock {
            if (requireCleanup) {
                database.trackingCacheQueries.deleteExpiredEvent(70.hours.inWholeMilliseconds)
                requireCleanup = false
            }
            database.trackingCacheQueries
                .selectWithLimit(limit)
                .executeAsList()
                .map { item ->
                    cbor.decodeFromByteArray(item.event)
                }
        }

    override suspend fun remove(events: List<Event>) {
        mutex.withLock {
            database.trackingCacheQueries
                .deleteUuids(
                    uuid =
                        events.map {
                            it.uuid
                        },
                )
        }
    }

    override suspend fun removeAll() {
        mutex.withLock {
            database.trackingCacheQueries
                .deleteAll()
        }
    }
}
