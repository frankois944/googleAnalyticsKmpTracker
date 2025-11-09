@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package io.github.frankois944.matomoKMPTracker

import io.ktor.http.encodeURLParameter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
public class Event internal constructor(
    internal val dateCreatedInSecond: Long = Clock.System.now().epochSeconds,
    internal val dateCreatedOfNanoSecond: Int = Clock.System.now().nanosecondsOfSecond,
    internal val uuid: String = Uuid.random().toHexString(),
    internal val rand: Long = 0,
    public val siteId: Int,
    public var visitor: Visitor? = null,
    public val isCustomAction: Boolean,
    public val date: Long =
        Clock.System
            .now()
            .epochSeconds,
    public val url: String?,
    public val actionName: List<String>,
    public val language: String? = Device.create().language,
    public var isNewSession: Boolean = false,
    public val referer: String?,
    public val screenResolution: Size = Device.create().screenSize,
    public val eventCategory: String?,
    public val eventAction: String?,
    public val eventName: String?,
    public val eventValue: Float?,
    public val campaignName: String?,
    public val campaignKeyword: String?,
    public val searchQuery: String?,
    public val searchCategory: String?,
    public val searchResultsCount: Int?,
    public val dimensions: List<CustomDimension>,
    public val customTrackingParameters: Map<String, String>,
    public val contentName: String?,
    public val contentPiece: String?,
    public val contentTarget: String?,
    public val contentInteraction: String?,
    public val goalId: Int?,
    public val revenue: Float?,
    public val orderId: String?,
    public val orderItems: List<OrderItem>,
    public val orderRevenue: Float?,
    public val orderSubTotal: Float?,
    public val orderTax: Float?,
    public val orderShippingCost: Float?,
    public val orderDiscount: Float?,
    public val isPing: Boolean = false,
) {
    public constructor(
        tracker: Tracker,
        action: List<String> = emptyList(),
        url: String? = null,
        referer: String? = null,
        eventCategory: String? = null,
        eventAction: String? = null,
        eventName: String? = null,
        eventValue: Float? = null,
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
        revenue: Float? = null,
        orderId: String? = null,
        orderItems: List<OrderItem> = emptyList(),
        orderRevenue: Float? = null,
        orderSubTotal: Float? = null,
        orderTax: Float? = null,
        orderShippingCost: Float? = null,
        orderDiscount: Float? = null,
        isCustomAction: Boolean,
        isPing: Boolean = false,
    ) : this(
        siteId = tracker.siteId,
        isCustomAction = isCustomAction,
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
    )

    internal val queryItems: Map<String, Any?>
        get() {
            val items =
                buildMap<String, Any?> {
                    set("idsite", siteId.toString())
                    set("apiv", "1")
                    set("rec", "1")
                    set("_id", visitor?.id)
                    set("uid", visitor?.userId)
                    set("url", url)
                    val localTime = Instant.fromEpochSeconds(date).toLocalDateTime(TimeZone.currentSystemDefault())
                    set("h", localTime.hour.toString())
                    set("m", localTime.minute.toString())
                    set("s", localTime.second.toString())
                    set("cdt", date)
                    if (isPing) {
                        set("ping", "1")
                    } else {
                        set("ca", if (isCustomAction) "1" else null)
                        set("action_name", actionName.joinToString("/"))
                        set("lang", language)
                        set("urlref", referer)
                        set("new_visit", if (isNewSession) 1 else 0)
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

    private fun orderItemParameterValue(): String {
        val items = mutableListOf<String>()
        orderItems.forEach {
            items.add("[\"${it.sku}\",\"${it.name}\",\"${it.category}\",${it.price},${it.quantity}]")
        }
        return "[${items.joinToString(",")}]".encodeURLParameter(true)
    }
}
