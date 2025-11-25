@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package io.github.frankois944.matomoKMPTracker

import io.github.frankois944.matomoKMPTracker.core.CustomDimension
import io.github.frankois944.matomoKMPTracker.core.Event
import io.github.frankois944.matomoKMPTracker.core.OrderItem
import io.github.frankois944.matomoKMPTracker.core.Visitor
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal fun Event.Companion.create(
    tracker: Tracker,
    action: List<String> = emptyList(),
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
    visitor: Visitor? = null,
): Event =
    Event(
        dateCreatedInSecond = Clock.System.now().epochSeconds,
        dateCreatedOfNanoSecond =
            Clock.System
                .now()
                .nanosecondsOfSecond
                .toLong(),
        siteId = tracker.siteId,
        isCustomAction = isCustomAction,
        uuid = Uuid.random().toHexString(),
        url = url ?: (tracker.contentBase + action.joinToString("/")),
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
        isNewSession = isNewSession,
        screenResolution = Device.screenSize,
    )

internal val Event.queryItems: Map<String, Any?>
    get() {
        val currentInstant = Instant.fromEpochMilliseconds(date)
        val items =
            buildMap<String, Any?> {
                set("idsite", siteId)
                set("apiv", "1")
                set("rec", "1")
                set("_id", visitor?.id)
                set("uid", visitor?.userId)
                set("cdt", currentInstant.toString())
                val localTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
                set("h", localTime.hour)
                set("m", localTime.minute)
                set("s", localTime.second)
                set("send_image", 0)
                // set("uadata", UserAgentProvider.getClientHint().encodeURLParameter(false))
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
                    if (isNewSession) {
                        set("new_visit", 1)
                    }
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
