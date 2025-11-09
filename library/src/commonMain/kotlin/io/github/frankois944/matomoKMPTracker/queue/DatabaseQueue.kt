@file:OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package io.github.frankois944.matomoKMPTracker.queue

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import io.github.frankois944.matomoKMPTracker.CacheDatabase
import io.github.frankois944.matomoKMPTracker.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.uuid.ExperimentalUuidApi

internal class DatabaseQueue(
    val database: CacheDatabase,
    val scope: String,
) : Queue {
    private val mutex = Mutex()

    override suspend fun eventCount(): Long = database.trackingCacheQueries.count(scope).awaitAsOne()

    override suspend fun enqueue(events: List<Event>) =
        mutex.withLock {
            events.forEach { event ->
                val data = Cbor.encodeToByteArray(event)
                database.trackingCacheQueries.insertUuid(
                    uuid = event.uuid,
                    scope = scope,
                    value_ = data,
                    timestamp = event.dateCreatedInSecond,
                    nanoSecond = event.dateCreatedOfNanoSecond.toLong(),
                )
            }
        }

    override suspend fun first(limit: Long): List<Event> =
        mutex.withLock {
            database.trackingCacheQueries
                .selectWithLimit(scope, limit)
                .awaitAsList()
                .map { item ->
                    Cbor.decodeFromByteArray<Event>(item.value_)
                }
        }

    override suspend fun remove(events: List<Event>) =
        mutex.withLock {
            database.trackingCacheQueries
                .deleteUuids(
                    uuid =
                        events.map {
                            it.uuid
                        },
                    scope = scope,
                )
            Unit
        }

    override suspend fun removeAll() =
        mutex.withLock {
            database.trackingCacheQueries
                .deleteAll(
                    scope = scope,
                )
            Unit
        }
}
