@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.CustomDimension
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.OrderItem
import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun Event.Companion.create(
    tracker: Tracker,
    action: List<String> = emptyList(),
    visitor: Visitor = tracker.visitor,
    url: String? = null,
    referer: String? = null,
    eventCategory: String? = null,
    eventAction: String? = null,
    eventName: String? = null,
    eventValue: Double? = null,
    customTrackingParameters: Map<String, String> = emptyMap(),
    searchQuery: String? = null,
    searchCategory: String? = null,
    searchResultsCount: Int? = null,
    dimensions: List<CustomDimension> = emptyList(),
    contentName: String? = null,
    contentInteraction: String? = null,
    contentPiece: String? = null,
    contentTarget: String? = null,
    goalId: Int? = null,
    revenue: Double? = null,
    orderId: String? = null,
    orderItems: List<OrderItem> = emptyList(),
    orderRevenue: Double? = null,
    orderSubTotal: Double? = null,
    orderTax: Double? = null,
    orderShippingCost: Double? = null,
    orderDiscount: Double? = null,
    isCustomAction: Boolean,
    isPing: Boolean = false,
    isNewSession: Boolean = false,
): Event =
    Event(
        dateCreatedInSecond = Clock.System.now().epochSeconds,
        dateCreatedOfNanoSecond =
            Clock.System
                .now()
                .nanosecondsOfSecond
                .toLong(),
        isCustomAction = isCustomAction,
        uuid = Uuid.random().toHexString(),
        url = url,
        actionName = action,
        referer = referer,
        eventCategory = eventCategory,
        eventAction = eventAction,
        eventName = eventName,
        eventValue = eventValue,
        campaignName = tracker.campaignName,
        campaignKeyword = tracker.campaignKeyword,
        searchQuery = searchQuery,
        searchCategory = searchCategory,
        searchResultsCount = searchResultsCount,
        dimensions = tracker.dimensions.getAll() + dimensions,
        customTrackingParameters = customTrackingParameters,
        contentName = contentName,
        contentPiece = contentPiece,
        contentTarget = contentTarget,
        contentInteraction = contentInteraction,
        goalId = goalId,
        revenue = revenue,
        orderId = orderId,
        orderItems = orderItems,
        orderRevenue = orderRevenue,
        orderSubTotal = orderSubTotal,
        orderTax = orderTax,
        orderShippingCost = orderShippingCost,
        orderDiscount = orderDiscount,
        isPing = isPing,
        date = Clock.System.now().toEpochMilliseconds(),
        visitor = visitor,
        language = Device.language,
        screenResolution = Device.screenSize,
        measurementId = tracker.measurementId,
        sessionId = tracker.sessionId
    )

internal val Event.getGABody: JsonObject
    get() {
        // TODO: build a valid request from event from Event Class
        // https://developers.google.com/analytics/devguides/collection/protocol/ga4/reference?client_type=firebase
        // the original implementation come from matomo
        // https://developer.matomo.org/api-reference/tracking-api
        return buildJsonObject {
            put("client_id", visitor.clientId)
           /* if (!visitor.userId.isNullOrEmpty()) {
                put("user_id", visitor.userId)
            }
            Device.currentUserAgent?.let { userAgent ->
                put("user_agent", userAgent)
            } ?: run {
                put("device", getGADeviceObject(language, screenResolution))
            }*/

            put("events", buildJsonObject {
                put("name", eventName)
                put("timestamp_micros", date * 1000)
                put("params", buildJsonObject {
                    // You must include the engagement_time_msec and session_id parameters in order for user activity
                    // to display in reports like Realtime.
                    put("session_id", sessionId)
                    put("engagement_time_msec", 2000)
                    when (eventName) {
                        "page_view" -> {
                            put("page_title", actionName.last())
                            put("page_location", actionName.joinToString("/"))
                        }
                        "search" -> {
                            put("search_term", searchQuery)
                            searchCategory?.let {  put("search_category", searchCategory) }
                            searchResultsCount?.let {  put("search_results_count", searchResultsCount) }
                        }
                        "select_content" -> {
                            put("content_type", contentInteraction)
                            put("content_id", contentName)
                            contentPiece?.let { put("content_piece", contentPiece) }
                            contentTarget?.let { put("content_target", contentTarget) }
                        }
                        else -> {
                            eventValue?.let {  put("event_value", eventValue) }
                            eventAction?.let { put("event_action", eventAction) }
                            eventCategory?.let { put("event_category", eventCategory) }
                        }
                    }
                })
            })
            // TODO: remove when debug is over
          //   put("validation_behavior", "ENFORCE_RECOMMENDATIONS")
        }
    }

// TODO: To be removed when migration done
internal val Event.queryItems: Map<String, Any?>
    get() {
        // TODO: replace with proper implementation of GA
        // https://developers.google.com/analytics/devguides/collection/protocol/ga4/reference?client_type=firebase
        // the original implementation come from matomo
        // https://developer.matomo.org/api-reference/tracking-api
        val currentInstant = Instant.fromEpochMilliseconds(date)
        val items =
            buildMap<String, Any?> {
                set("apiv", "1")
                set("rec", "1")
                set("_id", visitor.clientId)
                set("uid", visitor.userId)
                set("cdt", currentInstant.toString())
                val localTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
                set("h", localTime.hour)
                set("m", localTime.minute)
                set("s", localTime.second)
                set("send_image", 0)
                if (isPing) {
                    set("ping", "1")
                } else {
                    set("url", url)
                    if (orderItems.isEmpty()) {
                        set("ca", if (isCustomAction) "1" else null)
                    }
                    set("action_name", actionName.joinToString("/"))
                    set("lang", language)
                    set("urlref", referer)
                    set("res", "${screenResolution.width}x${screenResolution.height}")
                    set("e_c", eventCategory)
                    set("e_a", eventAction)
                    set("e_n", eventName)
                    set("e_v", eventValue)
                    set("_rcn", campaignName)
                    set("_rck", campaignKeyword)
                    set("search", searchQuery)
                    set("search_cat", searchCategory)
                    set("search_count", searchResultsCount)
                    set("c_n", contentName)
                    set("c_p", contentPiece)
                    set("c_t", contentTarget)
                    set("c_i", contentInteraction)
                    set("idgoal", goalId)
                    revenue?.let { revenue ->
                        set("revenue", revenue)
                    } ?: orderRevenue?.let { orderRevenue ->
                        set("revenue", orderRevenue)
                    }
                    set("ec_id", orderId)
                    set("ec_st", orderSubTotal)
                    set("ec_tx", orderTax)
                    set("ec_sh", orderShippingCost)
                    set("ec_dt", orderDiscount)
                }
            }
        val dimensionItems = dimensions.map { "dimension${it.index}" to it.value }
        val customItems = customTrackingParameters.map { it.key to it.value }
        val ecommerceOrderItemsAndFlag =
            if (orderItems.isNotEmpty()) {
                listOf(
                    "ec_items" to orderItemParameterValue(),
                    "idgoal" to "0",
                )
            } else {
                emptyList()
            }
        return items + dimensionItems + ecommerceOrderItemsAndFlag + customItems
    }

private fun Event.orderItemParameterValue(): String {
    val items = mutableListOf<List<String>>()
    orderItems.forEach {
        val newItem =
            buildList {
                add(it.sku)
                add(it.name)
                add(it.category)
                add(it.price.toString())
                add(it.quantity.toString())
            }
        items.add(newItem)
    }
    return Json.encodeToString(items)
}
