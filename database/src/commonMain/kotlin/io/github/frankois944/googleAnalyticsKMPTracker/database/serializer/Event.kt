@file:OptIn(ExperimentalSerializationApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.database.serializer

import io.github.frankois944.matomoKMPTracker.core.Event
import io.github.frankois944.matomoKMPTracker.schema.TrackingCache
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray

internal fun eventFromTrackingCache(item: TrackingCache): Event =
    Event(
        dateCreatedInSecond = item.timestamp,
        dateCreatedOfNanoSecond = item.nanosecond,
        uuid = item.uuid,
        siteId = item.siteId.toInt(),
        visitor = item.visitor?.let { visitorFromSerializedString(it) },
        isCustomAction = item.isCustomAction != 0L,
        date = item.date,
        url = item.url,
        actionName = Cbor.decodeFromByteArray(item.actionName),
        language = item.language,
        isNewSession = item.isNewSession != 0L,
        referer = item.referer,
        screenResolution = sizeFromSerializedString(item.screenResolution),
        eventCategory = item.eventCategory,
        eventAction = item.eventAction,
        eventName = item.eventName,
        eventValue = item.eventValue,
        campaignName = item.campaignName,
        campaignKeyword = item.campaignKeyword,
        searchQuery = item.searchQuery,
        searchCategory = item.searchCategory,
        searchResultsCount = item.searchResultsCount?.toInt(),
        dimensions =
            Cbor.decodeFromByteArray<List<String>>(item.dimensions).map {
                customDimensionFromSerializedString(it)
            },
        customTrackingParameters = Cbor.decodeFromByteArray(item.customTrackingParameters),
        contentName = item.contentName,
        contentPiece = item.contentPiece,
        contentTarget = item.contentTarget,
        contentInteraction = item.contentInteraction,
        goalId = item.goalId?.toInt(),
        revenue = item.revenue,
        orderId = item.orderId,
        orderItems =
            Cbor.decodeFromByteArray<List<String>>(item.orderItems).map {
                orderItemFromSerializedString(it)
            },
        orderRevenue = item.orderRevenue,
        orderSubTotal = item.orderSubTotal,
        orderTax = item.orderTax,
        orderShippingCost = item.orderShippingCost,
        orderDiscount = item.orderDiscount,
        isPing = item.isPing != 0L,
    )
