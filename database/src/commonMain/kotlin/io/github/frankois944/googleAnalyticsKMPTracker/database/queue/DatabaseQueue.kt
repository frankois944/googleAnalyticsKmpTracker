@file:OptIn(ExperimentalUuidApi::class, ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.database.queue

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import io.github.frankois944.googleAnalyticsKMPTracker.database.serializer.eventFromTrackingCache
import io.github.frankois944.matomoKMPTracker.core.Event
import io.github.frankois944.matomoKMPTracker.core.queue.Queue
import io.github.frankois944.googleAnalyticsKMPTracker.database.serializer.toSerializedString
import io.github.frankois944.matomoKMPTracker.schema.CacheDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.encodeToByteArray
import kotlin.uuid.ExperimentalUuidApi

public class DatabaseQueue(
    public val database: CacheDatabase,
    public val scope: String,
) : Queue {
    private val mutex = Mutex()

    override suspend fun eventCount(): Long = database.trackingCacheQueries.count(scope).awaitAsOne()

    override suspend fun enqueue(events: List<Event>): Unit =
        mutex.withLock {
            events.forEach { event ->
                database.trackingCacheQueries.insertUuid(
                    uuid = event.uuid,
                    scope = scope,
                    timestamp = event.dateCreatedInSecond,
                    nanosecond = event.dateCreatedOfNanoSecond,
                    siteId = event.siteId.toLong(),
                    visitor = event.visitor?.toSerializedString(),
                    isCustomAction = if (event.isCustomAction) 1L else 0L,
                    date = event.date,
                    url = event.url,
                    actionName = Cbor.encodeToByteArray(event.actionName),
                    language = event.language,
                    isNewSession = if (event.isNewSession) 1L else 0L,
                    referer = event.referer,
                    screenResolution = event.screenResolution.toSerializedString(),
                    eventCategory = event.eventCategory,
                    eventAction = event.eventAction,
                    eventName = event.eventName,
                    eventValue = event.eventValue,
                    campaignName = event.campaignName,
                    campaignKeyword = event.campaignKeyword,
                    searchQuery = event.searchQuery,
                    searchCategory = event.searchCategory,
                    searchResultsCount = event.searchResultsCount?.toLong(),
                    dimensions = Cbor.encodeToByteArray(event.dimensions.map { it.toSerializedString() }),
                    customTrackingParameters = Cbor.encodeToByteArray(event.customTrackingParameters),
                    contentName = event.contentName,
                    contentPiece = event.contentPiece,
                    contentTarget = event.contentTarget,
                    contentInteraction = event.contentInteraction,
                    goalId = event.goalId?.toLong(),
                    revenue = event.revenue,
                    orderId = event.orderId,
                    orderItems = Cbor.encodeToByteArray(event.orderItems.map { it.toSerializedString() }),
                    orderRevenue = event.orderRevenue,
                    orderSubTotal = event.orderSubTotal,
                    orderTax = event.orderTax,
                    orderShippingCost = event.orderShippingCost,
                    orderDiscount = event.orderDiscount,
                    isPing = if (event.isPing) 1L else 0L,
                )
            }
        }

    override suspend fun first(limit: Long): List<Event> =
        mutex.withLock {
            database.trackingCacheQueries
                .selectWithLimit(scope, limit)
                .awaitAsList()
                .map { item ->
                    eventFromTrackingCache(item)
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
                    scope = scope,
                )
        }
    }

    override suspend fun removeAll() {
        mutex.withLock {
            database.trackingCacheQueries
                .deleteAll(
                    scope = scope,
                )
        }
    }
}
