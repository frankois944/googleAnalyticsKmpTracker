@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker

import io.github.frankois944.matomoKMPTracker.context.storeContext
import io.github.frankois944.matomoKMPTracker.database.DriverFactory
import io.github.frankois944.matomoKMPTracker.database.createDatabase
import io.github.frankois944.matomoKMPTracker.dispatcher.Dispatcher
import io.github.frankois944.matomoKMPTracker.dispatcher.HttpClientDispatcher
import io.github.frankois944.matomoKMPTracker.preferences.UserPreferences
import io.github.frankois944.matomoKMPTracker.queue.DatabaseQueue
import io.github.frankois944.matomoKMPTracker.queue.Queue
import io.github.frankois944.matomoKMPTracker.queue.enqueue
import io.github.frankois944.matomoKMPTracker.utils.ConcurrentMutableList
import io.github.frankois944.matomoKMPTracker.utils.startTimer
import io.ktor.http.Url
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

public expect abstract class NativeContext

public class Tracker private constructor(
    internal val url: String,
    internal val siteId: Int,
    internal val tokenAuth: String? = null,
    internal val customDispatcher: Dispatcher? = null,
    internal val customUserAgent: String? = null,
    internal val customQueue: Queue? = null,
    internal val customActionHostUrl: String? = null,
    context: NativeContext?,
) {
    internal var queue: Queue? = null
    internal var userPreferences: UserPreferences? = null
    internal lateinit var dispatcher: Dispatcher
    internal var dimensions: ConcurrentMutableList<CustomDimension> = ConcurrentMutableList()
    internal val contentBase: String
    internal var nextEventStartsANewSession = true
    internal var campaignName: String? = null
    internal var campaignKeyword: String? = null
    internal var siteUrl: Url = Url(url)
    internal val device: Device = Device.create()
    private val numberOfEventsDispatchedAtOnce = 20L
    internal val coroutine = CoroutineScope(Dispatchers.Default)
    internal val dispatchInterval: Duration = 5.seconds
    private var isDispatching: Boolean = false
    private val mutex: Mutex = Mutex()
    private val heartbeat: HeartBeat

    /**
     * This logger is used to perform logging of all sorts of Matomo related information.
     * Per default it is a `DefaultLogger` with a `minLevel` of `LogLevel.warning`.
     * You can set your own Logger with a custom `minLevel` or a complete custom logging mechanism.
     */
    public var logger: MatomoTrackerLogger = DefaultMatomoTrackerLogger(minLevel = LogLevel.Warning)

    init {
        require(url.endsWith("matomo.php")) {
            "The url must end with 'matomo.php'"
        }

        require(!(device.model == "Android" && context == null)) {
            "An Android context must be set"
        }
        storeContext(context)
        contentBase =
            if (!customActionHostUrl.isNullOrEmpty()) {
                "http://$customActionHostUrl/"
            } else if (!device.actionUrl.isNullOrEmpty()) {
                "http://${device.actionUrl}/"
            } else {
                "http://unknown/"
            }
        heartbeat = HeartBeat(this)
    }

    internal suspend fun build(): Tracker {
            val prefix = "${siteId}_${siteUrl.host}"
            // Dispatcher
            dispatcher =
                customDispatcher ?: HttpClientDispatcher(
                    baseURL = url,
                    userAgent = customUserAgent,
                    onPrintLog = { message ->
                        logger.log(message = message, LogLevel.Debug)
                    },
                    tokenAuth = tokenAuth,
                )
            // Database
            val database = createDatabase(DriverFactory())
        this.queue = customQueue ?: DatabaseQueue(database, prefix)
        this.userPreferences = UserPreferences(database, prefix)

            // Startup
            startNewSession()
            startDispatchEvents()
            if (userPreferences?.isHeartbeatEnabled() == true) {
                heartbeat.start()
            }
        return this
    }

    internal fun startDispatchEvents() {
        coroutine.launch(Dispatchers.Unconfined) {
            logger.log("Start Dispatchers timer", LogLevel.Debug)
            startTimer(dispatchInterval) {
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
                while (dispatchBatch()) {
                    logger.log("Events dispatched", LogLevel.Info)
                }
                isDispatching = false
            }
        }
    }

    internal suspend fun dispatchBatch(): Boolean {
        logger.log("Start Dispatch events", LogLevel.Debug)
        val items = queue?.first(numberOfEventsDispatchedAtOnce)
        if (items == null || items.isEmpty()) {
            logger.log("No events to dispatch", LogLevel.Verbose)
            return false
        }
        logger.log("Found ${items.size} events", LogLevel.Debug)
        try {
            dispatcher.send(items)
            queue?.remove(items)
            return true
        } catch (e: IllegalArgumentException) {
            queue?.remove(items)
            logger.log("Invalid request data, remove from cache: $e", LogLevel.Error)
        } catch (e: Exception) {
            logger.log("Error while dispatching events: $e", LogLevel.Error)
        }
        return false
    }

    public companion object {
        /**
         * @param url The url of the matomo API endpoint
         * @param siteId The ID of the website we're tracking a visit/action for.
         * @param tokenAuth 32 character authorization key used to authenticate the API request
         * @param customActionHostUrl Used to build the url of an Event, it will then have the format `http://customActionHostUrl/actions`.
         * - On Apple and Android targets, by default, it's the applicationId/packageName.
         * - On the wasm target, by default, it's the current hostname of the browser.
         * - On Desktop, by default, it's null, but it's RECOMMENDED to set a value by yourself.
         * @param context (MANDATORY for Android target) An Android Context for content retrieval
         * @param customUserAgent Set a custom userAgent for every request
         * @param customDispatcher
         * @param customQueue
         */
        @Throws(IllegalArgumentException::class, CancellationException::class)
        public suspend fun create(
            url: String,
            siteId: Int,
            tokenAuth: String? = null,
            customActionHostUrl: String? = null,
            context: NativeContext? = null,
            customUserAgent: String? = null,
            customDispatcher: Dispatcher? = null,
            customQueue: Queue? = null,
        ): Tracker =
            Tracker(
                url = url,
                siteId = siteId,
                context = context,
                tokenAuth = tokenAuth,
                customDispatcher = customDispatcher,
                customUserAgent = customUserAgent,
                customQueue = customQueue,
                customActionHostUrl = customActionHostUrl,
            ).build()
    }

    internal fun queue(
        event: Event,
        nextEventStartsANewSession: Boolean,
        isHeartBeat: Boolean = false,
    ) {
        coroutine.launch(Dispatchers.Default) {
            userPreferences?.let { userPreferences ->
                if (isOptedOut()) return@launch
                event.visitor = Visitor.current(userPreferences)
                if (isHeartBeat) {
                    dispatcher.sendPing(event)
                } else {
                    event.isNewSession = nextEventStartsANewSession
                    this@Tracker.mutex.withLock {
                        logger.log("Queued event: ${event.uuid}", LogLevel.Verbose)
                        this@Tracker.queue?.enqueue(event)
                    }
                }
            }
        }
    }

    /**
     * Defines if the user opted out of tracking. When set to true, every event
     * will be discarded immediately. This property is persisted between app launches.
     */
    public suspend fun isOptedOut(): Boolean = userPreferences?.optOut() ?: false

    /**
     * Defines if the user opted out of tracking. When set to true, every event
     * will be discarded immediately. This property is persisted between app launches.
     */
    public suspend fun setOptOut(value: Boolean): Unit? = userPreferences?.setOptOut(value)

    /**
     * Get the heartbeat tracking state for the user.
     */
    public suspend fun isHeartBeatEnabled(): Boolean = userPreferences?.isHeartbeatEnabled() ?: true

    /**
     * Updates the heartbeat tracking state for the user.
     * When the heartbeat is enabled or disabled, this preference is persisted between app launches.
     *
     * @param value A boolean value indicating whether to enable (true) or disable (false) the heartbeat tracking.
     */
    public suspend fun setIsHeartBeat(value: Boolean): Unit =
        userPreferences?.setEnableHeartbeat(value).let { isEnabled ->
            if (isEnabled == true) {
                heartbeat.start()
            } else {
                heartbeat.stop()
            }
        }

    /**
     * Will be used to associate all future events with a given visitorId / cid. This property
     * is persisted between app launches.
     */
    public suspend fun userId(): String? = userPreferences?.userId()

    /**
     * User ID is any non-empty unique string identifying the user (such as an email address or a username)
     */
    public suspend fun setUserId(value: String?) {
        logger.log("Setting the userId to $value", LogLevel.Debug)
        userPreferences?.setUserId(value)
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
            Event(
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
            Event(
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
            Event(
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
        value: Float? = null,
        dimensions: List<CustomDimension> = emptyList(),
        url: String? = null,
    ) {
        track(
            Event(
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
        revenue: Float,
    ) {
        track(
            Event(
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
        revenue: Float,
        subTotal: Float? = null,
        tax: Float? = null,
        shippingCost: Float? = null,
        discount: Float? = null,
    ) {
        track(
            Event(
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
            Event(
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
        nextEventStartsANewSession = true
    }

    /**
     * Resets all session, visitor and campaign information.
     *
     * This function should only be called from the main thread
     * After calling this method this instance behaves like the app has been freshly installed.
     */
    public fun reset() {
        coroutine.launch {
            logger.log("Reset Session siteId: $siteId", LogLevel.Debug)
            userPreferences?.reset()
            dimensions.removeAll { true }
            campaignName = null
            campaignKeyword = null
            startNewSession()
        }
    }
}
