@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.core.CustomDimension
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.OrderItem
import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.core.queue.Queue
import io.github.frankois944.googleAnalyticsKMPTracker.core.queue.enqueue
import io.github.frankois944.googleAnalyticsKMPTracker.database.factory.DriverFactory
import io.github.frankois944.googleAnalyticsKMPTracker.database.factory.createDatabase
import io.github.frankois944.googleAnalyticsKMPTracker.database.queue.DatabaseQueue
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.Dispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.HttpClientDispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import io.github.frankois944.googleAnalyticsKMPTracker.utils.ConcurrentMutableList
import io.github.frankois944.googleAnalyticsKMPTracker.utils.startTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

public class Tracker private constructor(
    internal val url: String,
    internal val apiSecret: String,
    internal val measurementId: String,
    internal val customDispatcher: Dispatcher? = null,
    internal val customQueue: Queue? = null,
    context: Any?,
) {
    internal var queue: Queue? = null
    internal lateinit var userPreferences: UserPreferences
    internal lateinit var dispatcher: Dispatcher
    internal var dimensions: ConcurrentMutableList<CustomDimension> = ConcurrentMutableList()
    internal var nextEventStartsANewSession = true
    internal var campaignName: String? = null
    internal var campaignKeyword: String? = null
    private val numberOfEventsDispatchedAtOnce = 5L
    internal val coroutine = CoroutineScope(Dispatchers.Default)
    internal val dispatchInterval: Duration = 5.seconds
    private var isDispatching: Boolean = false
    private val mutex: Mutex = Mutex()
    // private val heartbeat: HeartBeat
    internal var sessionId: Long = Random.nextLong(from = 1, until = Long.MAX_VALUE)
    internal lateinit var visitor: Visitor

    /**
     * This logger is used to perform logging of all sorts of Matomo related information.
     * Per default it is a `DefaultLogger` with a `minLevel` of `LogLevel.warning`.
     * You can set your own Logger with a custom `minLevel` or a complete custom logging mechanism.
     */
    public var logger: GATrackerLogger = DefaultGATrackerLogger(minLevel = LogLevel.Warning)

    init {
        require(!(Device.model == "Android" && context == null)) {
            "An Android context must be set"
        }
        storeContext(context)
       // heartbeat = HeartBeat(this)
    }

    internal suspend fun build(): Tracker {
        withContext(Dispatchers.Default) {
            val scope = measurementId
            // Dispatcher
            dispatcher =
                customDispatcher ?: HttpClientDispatcher(
                    baseURL = url,
                    onPrintLog = { message ->
                        logger.log(message = message, LogLevel.Debug)
                    },
                    apiSecret = apiSecret,
                )
            // Database
            val database =
                createDatabase(
                    driverFactory = DriverFactory(),
                    dbName = scope.hashCode().toString(),
                )
            this@Tracker.queue = customQueue ?: DatabaseQueue(database, scope)
            this@Tracker.userPreferences = UserPreferences(database, scope)
            this@Tracker.visitor = Visitor.current(userPreferences)
            // Startup
            startNewSession()
            startDispatchEvents()
           /* if (userPreferences.isHeartbeatEnabled()) {
                heartbeat.start()
            }*/
        }
        return this
    }

    internal fun startDispatchEvents() {
        coroutine.launch(Dispatchers.Default) {
            logger.log("Start Dispatchers timer", LogLevel.Debug)
            startTimer(dispatchInterval) {
                mutex.withLock {
                    logger.log("Start checking for new event", LogLevel.Info)
                    if (isDispatching) {
                        logger.log("Already dispatching events", LogLevel.Verbose)
                        return@startTimer
                    }
                    if (queue?.eventCount() == 0L) {
                        logger.log("No events to dispatch", LogLevel.Verbose)
                        return@startTimer
                    }
                    isDispatching = true
                    logger.log("Start dispatching events", LogLevel.Info)
                    dispatchBatch()
                    logger.log("Events dispatched", LogLevel.Info)
                    isDispatching = false
                }
            }
        }
    }

    internal suspend fun dispatchBatch() {
        logger.log("Start Dispatch events", LogLevel.Debug)
        val items = queue?.first(numberOfEventsDispatchedAtOnce)
        if (items.isNullOrEmpty()) {
            logger.log("No events to dispatch", LogLevel.Verbose)
        } else {
            items.forEach { item ->
                logger.log("Sending event ${items.joinToString { "${it.uuid}," }}", LogLevel.Verbose)
                try {
                    dispatcher.sendSingleEvent(item)
                    logger.log("remove events ${items.joinToString { "${it.uuid}," }}", LogLevel.Verbose)
                    queue?.remove(listOf(item))
                } catch (e: IllegalArgumentException) {
                    logger.log("remove events ${items.joinToString { "${it.uuid}," }}", LogLevel.Verbose)
                    queue?.remove(listOf(item))
                    logger.log("Invalid request data, remove from cache: $e", LogLevel.Error)
                } catch (e: Exception) {
                    logger.log("Error while dispatching events: $e", LogLevel.Error)
                }
            }
        }
    }

    public companion object {
        /**
         * @param apiSecret The API Secret from the Google Analytics UI.
         * @param measurementId GA Property ID.
         * @param url The url of the Google Analytics API endpoint, use `https://region1.google-analytics.com/mp/collect` for europe
         * @param context (MANDATORY for Android target) A valid Android Context for content retrieval
         * @param customDispatcher
         * @param customQueue
         */
        @Throws(IllegalArgumentException::class, CancellationException::class)
        public suspend fun create(
            apiSecret: String,
            measurementId: String,
            url: String = "https://www.google-analytics.com/mp/collect",
            context: Any? = null,
            customDispatcher: Dispatcher? = null,
            customQueue: Queue? = null,
        ): Tracker =
            Tracker(
                url = url,
                apiSecret = apiSecret,
                context = context,
                measurementId = measurementId,
                customDispatcher = customDispatcher,
                customQueue = customQueue,
            ).build()
    }

    internal fun queue(
        event: Event,
        nextEventStartsANewSession: Boolean,
    ) {
        coroutine.launch(Dispatchers.Default) {
            if (isOptedOut()) return@launch
            event.isNewSession = nextEventStartsANewSession
            logger.log("Queued event: ${event.uuid}", LogLevel.Verbose)
            this@Tracker.queue?.enqueue(event)
        }
    }

    /**
     * Defines if the user opted out of tracking. When set to true, every event
     * will be discarded immediately. This property is persisted between app launches.
     */
    public suspend fun isOptedOut(): Boolean = userPreferences.optOut()

    /**
     * Defines if the user opted out of tracking. When set to true, every event
     * will be discarded immediately. This property is persisted between app launches.
     */
    public suspend fun setOptOut(value: Boolean): Unit = userPreferences.setOptOut(value)

    /**
     * Get the heartbeat tracking state for the user.
     */
    public suspend fun isHeartBeatEnabled(): Boolean = userPreferences.isHeartbeatEnabled()

    /**
     * Updates the heartbeat tracking state for the user.
     * When the heartbeat is enabled or disabled, this preference is persisted between app launches.
     *
     * @param value A boolean value indicating whether to enable (true) or disable (false) the heartbeat tracking.
     */
    public suspend fun setIsHeartBeat(value: Boolean): Unit =
        userPreferences.setEnableHeartbeat(value).let { isEnabled ->
            /*if (isEnabled) {
                heartbeat.start()
            } else {
                heartbeat.stop()
            }*/
        }

    /**
     * Will be used to associate all future events with a given visitorId / cid. This property
     * is persisted between app launches.
     */
    public suspend fun userId(): String? = userPreferences.userId()

    /**
     * User ID is any non-empty unique string identifying the user (such as an email address or a username)
     */
    public suspend fun setUserId(value: String?) {
        logger.log("Setting the userId to $value", LogLevel.Debug)
        userPreferences.setUserId(value)
        visitor = Visitor.current(userPreferences)
    }

    /**
     * Tracks a custom Event
     *
     * @param event The event that should be tracked.
     */
    public fun track(event: Event) {
        queue(event, nextEventStartsANewSession)
        nextEventStartsANewSession = false
        if (event.campaignName == campaignName &&
            event.campaignKeyword == campaignKeyword
        ) {
            campaignName = null
            campaignKeyword = null
        }
    }

    /**
     * Adds the name and keyword for the current campaign.
     * This is usually very helpfull if you use deeplinks into your app.
     *
     *  More information on campaigns: [https:*matomo.org/docs/tracking-campaigns/](https:*matomo.org/docs/tracking-campaigns/)
     *
     * @param name The name of the campaign.
     * @param keyword The keyword of the campaign.
     */
    public fun trackCampaign(
        name: String,
        keyword: String? = null,
    ) {
        campaignName = name
        campaignKeyword = keyword
    }

    /**
     * Track a content impression
     *
     * @param name The name of the content. For instance 'Ad Foo Bar'
     * @param piece The actual content piece. For instance the path to an image, video, audio, any text
     * @param target The target of the content. For instance the URL of a landing page
     */
    public fun trackContentImpression(
        name: String,
        piece: String? = null,
        target: String? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                contentName = name,
                contentPiece = piece,
                contentTarget = target,
                isCustomAction = false,
            ),
        )
    }

    /**
     * Tracks an interaction with a specific content element.
     *
     * @param name The name of the content being interacted with.
     * @param interaction The type of interaction that occurred.
     * @param piece Additional information or a specific part of the content involved in the interaction, nullable.
     * @param target The intended target of the interaction, nullable.
     */
    public fun trackContentInteraction(
        name: String,
        interaction: String,
        piece: String? = null,
        target: String? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                contentInteraction = interaction,
                contentName = name,
                contentPiece = piece,
                contentTarget = target,
                isCustomAction = false,
            ),
        )
    }

// <editor-fold desc="Track actions">

    /**
     * Tracks a screenview.
     *
     * This method can be used to track hierarchical screen names, e.g. screen/settings/register. Use this to create a hierarchical and logical grouping of screen views in the Matomo web interface.
     *
     * @param view An array of hierarchical screen names.
     * @param url The optional url of the page that was viewed.
     * @param dimensions An optional array of dimensions, that will be set only in the scope of this view.
     */
    public fun trackView(
        view: List<String>,
        url: String? = null,
        dimensions: List<CustomDimension> = emptyList(),
    ) {
        track(
            Event.create(
                tracker = this,
                action = view,
                url = url,
                dimensions = dimensions,
                isCustomAction = false,
            ),
        )
    }

    /**
     * Tracks an event as described here: https://matomo.org/docs/event-tracking/
     *
     * @param category The Category of the Event
     * @param action The Action of the Event
     * @param name The optional name of the Event
     * @param value The optional value of the Event
     * @param dimensions An optional array of dimensions, that will be set only in the scope of this event.
     * @param url The optional url of the page that was viewed.
     */
    public fun trackEventWithCategory(
        category: String,
        action: String,
        name: String? = null,
        value: Double? = null,
        dimensions: List<CustomDimension> = emptyList(),
        url: String? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                action = listOf(action),
                url = url,
                eventCategory = category,
                eventAction = action,
                eventName = name,
                eventValue = value,
                dimensions = dimensions,
                isCustomAction = true,
            ),
        )
    }

    /**
     * Tracks a goal as described here: https://matomo.org/docs/tracking-goals-web-analytics/
     *
     * @param goalId The defined ID of the Goal
     * @param revenue The monetary value that was generated by the Goal
     */
    public fun trackGoal(
        goalId: Int,
        revenue: Double,
    ) {
        track(
            Event.create(
                tracker = this,
                goalId = goalId,
                revenue = revenue,
                isCustomAction = true,
            ),
        )
    }

    /**
     * Tracks an order as described here: https://matomo.org/faq/reports/advanced-manually-tracking-ecommerce-actions-in-matomo/#tracking-orders-to-matomo-required
     *
     * @param id The unique ID of the order
     * @param items The array of items to be ordered
     * @param revenue The grand total for the order (includes tax, shipping and subtracted discount)
     * @param subTotal The sub total of the order (excludes shipping)
     * @param tax The tax amount of the order
     * @param shippingCost The shipping cost of the order
     * @param discount The discount offered
     */
    public fun trackOrder(
        id: String,
        items: List<OrderItem>,
        revenue: Double,
        subTotal: Double? = null,
        tax: Double? = null,
        shippingCost: Double? = null,
        discount: Double? = null,
    ) {
        track(
            Event.create(
                tracker = this@Tracker,
                orderId = id,
                orderItems = items,
                orderRevenue = revenue,
                orderSubTotal = subTotal,
                orderTax = tax,
                orderShippingCost = shippingCost,
                orderDiscount = discount,
                isCustomAction = true,
            ),
        )
    }

    /**
     * Tracks a search result page as described here: https://matomo.org/docs/site-search/
     *
     * @param query The string the user was searching for
     * @param category An optional category which the user was searching in
     * @param resultCount The number of results that were displayed for that search
     * @param dimensions An optional array of dimensions, that will be set only in the scope of this event.
     * @param url The optional url of the page that was viewed.
     */
    public fun trackSearch(
        query: String,
        category: String? = null,
        resultCount: Int? = null,
        dimensions: List<CustomDimension> = emptyList(),
        url: String? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                action = emptyList(),
                url = url,
                searchQuery = query,
                searchCategory = category,
                searchResultsCount = resultCount,
                dimensions = dimensions,
                isCustomAction = true,
            ),
        )
    }
// </editor-fold>

// <editor-fold desc="Dimension">

    /**
     * Set a permanent custom dimension by value and index.
     *
     * @param value The value for the new Custom Dimension
     * @param forIndex The index of the new Custom Dimension
     */
    public fun setDimension(
        value: String,
        forIndex: Int,
    ) {
        coroutine.launch {
            removeDimension(forIndex)
            dimensions.add(CustomDimension(forIndex, value))
        }
    }

    /**
     * Removes a previously set custom dimension.
     *
     * @param removeDimension The index of the dimension.
     */
    public fun removeDimension(atIndex: Int) {
        coroutine.launch {
            dimensions.removeAll {
                it.index == atIndex
            }
        }
    }
// </editor-fold>

    /**
     * Starts a new Session
     *
     * Use this function to manually start a new Session. A new Session will be automatically
     * created only on init of the tracker.
     */
    public fun startNewSession() {
        logger.log("start New Session", LogLevel.Info)
        sessionId = Random.nextLong(from = 1, until = Long.MAX_VALUE)
        nextEventStartsANewSession = true
    }

    /**
     * Resets all session, visitor and campaign information.
     *
     * After calling this method this instance behaves like the app has been freshly installed.
     */
    public fun reset() {
        coroutine.launch {
            logger.log("Reset Session firebaseAppId: $measurementId", LogLevel.Debug)
            userPreferences.reset()
            dimensions.removeAll { true }
            campaignName = null
            campaignKeyword = null
            startNewSession()
        }
    }
}
