@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker.core

import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

public class Event(
    public val dateCreatedInSecond: Long,
    public val dateCreatedOfNanoSecond: Long,
    public val uuid: String,
    public var visitor: Visitor,
    public val isCustomAction: Boolean,
    public val date: Long,
    public val url: String?,
    public val actionName: List<String>,
    public val language: String?,
    public val referer: String?,
    public val screenResolution: Size,
    public val eventCategory: String?,
    public val eventAction: String?,
    public val eventName: String?,
    public val eventValue: Double?,
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
    public val revenue: Double?,
    public val orderId: String?,
    public val orderItems: List<OrderItem>,
    public val orderRevenue: Double?,
    public val orderSubTotal: Double?,
    public val orderTax: Double?,
    public val orderShippingCost: Double?,
    public val orderDiscount: Double?,
    public val isPing: Boolean,
    public val measurementId: String,
    public val sessionId: Long
    ) {
    override fun toString(): String =
        """Event(
            |  uuid='$uuid',
            |  dateCreatedInSecond=$dateCreatedInSecond,
            |  dateCreatedOfNanoSecond=$dateCreatedOfNanoSecond,
            |  date=$date,
            |  actionName=$actionName,
            |  visitor=$visitor,
            |  isCustomAction=$isCustomAction,
            |  language=$language,
            |  referer=$referer,
            |  screenResolution=$screenResolution,
            |  eventCategory=$eventCategory,
            |  eventAction=$eventAction,
            |  eventName=$eventName,
            |  eventValue=$eventValue,
            |  campaignName=$campaignName,
            |  campaignKeyword=$campaignKeyword,
            |  searchQuery=$searchQuery,
            |  searchCategory=$searchCategory,
            |  searchResultsCount=$searchResultsCount,
            |  dimensions=$dimensions,
            |  customTrackingParameters=$customTrackingParameters,
            |  contentName=$contentName,
            |  contentPiece=$contentPiece,
            |  contentTarget=$contentTarget,
            |  contentInteraction=$contentInteraction,
            |  goalId=$goalId,
            |  revenue=$revenue,
            |  orderId=$orderId,
            |  orderItems=$orderItems,
            |  orderRevenue=$orderRevenue,
            |  orderSubTotal=$orderSubTotal,
            |  orderTax=$orderTax,
            |  orderShippingCost=$orderShippingCost,
            |  orderDiscount=$orderDiscount,
            |  isPing=$isPing
            |  firebaseAppId=$measurementId,
            |  sessionId=$sessionId
            |)
        """.trimMargin()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        return dateCreatedInSecond == other.dateCreatedInSecond &&
                dateCreatedOfNanoSecond == other.dateCreatedOfNanoSecond &&
                uuid == other.uuid &&
                visitor == other.visitor &&
                isCustomAction == other.isCustomAction &&
                date == other.date &&
                actionName == other.actionName &&
                language == other.language &&
                referer == other.referer &&
                screenResolution == other.screenResolution &&
                eventCategory == other.eventCategory &&
                eventAction == other.eventAction &&
                eventName == other.eventName &&
                eventValue == other.eventValue &&
                campaignName == other.campaignName &&
                campaignKeyword == other.campaignKeyword &&
                searchQuery == other.searchQuery &&
                searchCategory == other.searchCategory &&
                searchResultsCount == other.searchResultsCount &&
                dimensions == other.dimensions &&
                customTrackingParameters == other.customTrackingParameters &&
                contentName == other.contentName &&
                contentPiece == other.contentPiece &&
                contentTarget == other.contentTarget &&
                contentInteraction == other.contentInteraction &&
                goalId == other.goalId &&
                revenue == other.revenue &&
                orderId == other.orderId &&
                orderItems == other.orderItems &&
                orderRevenue == other.orderRevenue &&
                orderSubTotal == other.orderSubTotal &&
                orderTax == other.orderTax &&
                orderShippingCost == other.orderShippingCost &&
                orderDiscount == other.orderDiscount &&
                isPing == other.isPing &&
                measurementId == other.measurementId &&
                sessionId == other.sessionId
    }

    override fun hashCode(): Int {
        var result = dateCreatedInSecond.hashCode()
        result = 31 * result + dateCreatedOfNanoSecond.hashCode()
        result = 31 * result + isCustomAction.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + (eventValue?.hashCode() ?: 0)
        result = 31 * result + (searchResultsCount ?: 0)
        result = 31 * result + (goalId ?: 0)
        result = 31 * result + (revenue?.hashCode() ?: 0)
        result = 31 * result + (orderRevenue?.hashCode() ?: 0)
        result = 31 * result + (orderSubTotal?.hashCode() ?: 0)
        result = 31 * result + (orderTax?.hashCode() ?: 0)
        result = 31 * result + (orderShippingCost?.hashCode() ?: 0)
        result = 31 * result + (orderDiscount?.hashCode() ?: 0)
        result = 31 * result + isPing.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + (visitor?.hashCode() ?: 0)
        result = 31 * result + actionName.hashCode()
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + (referer?.hashCode() ?: 0)
        result = 31 * result + screenResolution.hashCode()
        result = 31 * result + (eventCategory?.hashCode() ?: 0)
        result = 31 * result + (eventAction?.hashCode() ?: 0)
        result = 31 * result + (eventName?.hashCode() ?: 0)
        result = 31 * result + (campaignName?.hashCode() ?: 0)
        result = 31 * result + (campaignKeyword?.hashCode() ?: 0)
        result = 31 * result + (searchQuery?.hashCode() ?: 0)
        result = 31 * result + (searchCategory?.hashCode() ?: 0)
        result = 31 * result + dimensions.hashCode()
        result = 31 * result + customTrackingParameters.hashCode()
        result = 31 * result + (contentName?.hashCode() ?: 0)
        result = 31 * result + (contentPiece?.hashCode() ?: 0)
        result = 31 * result + (contentTarget?.hashCode() ?: 0)
        result = 31 * result + (contentInteraction?.hashCode() ?: 0)
        result = 31 * result + (orderId?.hashCode() ?: 0)
        result = 31 * result + orderItems.hashCode()
        result = 31 * result + measurementId.hashCode()
        result = 31 * result + sessionId.hashCode()
        return result
    }

    public companion object
}
