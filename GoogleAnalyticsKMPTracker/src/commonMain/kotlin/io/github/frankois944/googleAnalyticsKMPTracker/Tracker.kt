@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.UserProperty
import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.core.queue.Queue
import io.github.frankois944.googleAnalyticsKMPTracker.core.queue.enqueue
import io.github.frankois944.googleAnalyticsKMPTracker.database.factory.DriverFactory
import io.github.frankois944.googleAnalyticsKMPTracker.database.factory.createDatabase
import io.github.frankois944.googleAnalyticsKMPTracker.database.queue.DatabaseQueue
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.Dispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.dispatcher.http.HttpClientDispatcher
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import io.github.frankois944.googleAnalyticsKMPTracker.utils.ConcurrentMutableList
import io.github.frankois944.googleAnalyticsKMPTracker.utils.startTimer
import io.github.frankois944.googleAnalyticsKMPTracker.utils.toJsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalTime::class)
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
    internal var userProperties: ConcurrentMutableList<UserProperty> = ConcurrentMutableList()
    private val numberOfEventsDispatchedAtOnce = 10L
    internal val coroutine = CoroutineScope(Dispatchers.Default)
    internal val dispatchInterval: Duration = 5.seconds
    private var isDispatching: Boolean = false
    private val mutex: Mutex = Mutex()
    private var lastEventDate = Clock.System.now()

    /**
     * sessionId is the timestamps of the beginning of the session
     */
    internal var sessionId: Long = Clock.System.now().epochSeconds
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
            //     items.forEach { item ->
            logger.log("Sending event ${items.joinToString { it.uuid }}", LogLevel.Verbose)
            try {
                // bulk or not build ??
                dispatcher.sendBulkEvent(items)
                logger.log("remove events ${items.joinToString { it.uuid }}", LogLevel.Verbose)
                queue?.remove(items)
            } catch (e: Exception) {
                logger.log("Error while dispatching events: $e", LogLevel.Error)
            }
        }
        //}
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
    ) {
        coroutine.launch(Dispatchers.Default) {
            if (isOptedOut()) return@launch
            logger.log("Queued event: ${event.uuid}", LogLevel.Verbose)
            event.lastEventTimeStampInMs = lastEventDate.toEpochMilliseconds()
            this@Tracker.queue?.enqueue(event)
            lastEventDate = Clock.System.now()
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
    internal fun track(event: Event) {
        queue(event)
    }

    /**
     * Send an event with the specified name and parameters.
     *
     * @param name The name of the event to be tracked. It identifies the type of action or behavior being logged.
     * @param params A map containing key-value pairs of additional information or attributes related to the event.
     * Accepted value types include String, Int, Double, Boolean, Long, and Float.
     * Other types will be converted to a string representation.
     */
    public fun sendEvent(name: String, params: Map<String, Any>) {
        track(
            Event.create(
                tracker = this,
                eventName = name,
                params = params.toJsonObject().map { it.key to it.value.jsonPrimitive }.toMap(),
            ),
        )
    }

    /**
     * Tracks an interaction with a specific content item.
     *
     * This method creates a "select_content" event, which represents the interaction with content.
     * The event includes the type and ID of the content interacted with, if provided.
     *
     * @param contentType The type of the content being interacted with (e.g., video, article). Can be null.
     * @param contentId The unique identifier of the content being interacted with. Can be null.
     */
    public fun trackContentInteraction(
        contentType: String? = null,
        contentId: String? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                eventName = "select_content",
                params = buildMap {
                    contentId?.let { put("content_id", JsonPrimitive(contentType)) }
                    contentType?.let { put("content_type", JsonPrimitive(contentId)) }
                },
            ),
        )
    }

// <editor-fold desc="Track actions">

    /**
     * Tracks a page view event for the specified screen.
     *
     * @param viewName The name of the screen being viewed. This is used as the page title in the tracking data.
     */
    public fun trackView(
        viewName: String,
    ) {
        trackView(listOf(viewName))
    }

    /**
     * Tracks a page view event for the specified screen.
     *
     * @param view A list of hierarchical screen names. The last element is used as the page title,
     * and all elements are joined with '/' to form the page location.
     */
    public fun trackView(
        view: List<String>,
    ) {
        track(
            Event.create(
                tracker = this,
                eventName = "page_view",
                params = buildMap {
                    put("page_title", JsonPrimitive(view.last()))
                    put("page_location", JsonPrimitive(view.joinToString("/")))
                },
            ),
        )
    }

    /**
     * Tracks a custom event with optional parameters. This method allows the creation
     * and dispatch of an event with the specified name and attributes.
     *
     * @param name The name of the event to be tracked.
     * @param parameters A map of key-value pairs representing additional parameters
     * for the event. Accepted value types include String, Int, Double, Boolean, Long,
     * and Float. If the value does not match any of these types, it will be
     * converted to a string. Can be null.
     */
    public fun trackEvent(
        name: String,
        parameters: Map<String, Any>? = null,
    ) {
        track(
            Event.create(
                tracker = this,
                eventName = name,
                params = buildMap {
                    parameters?.let {
                        for ((key, value) in parameters) {
                            when (value) {
                                is String -> put(key, JsonPrimitive(value))
                                is Int -> put(key, JsonPrimitive(value))
                                is Double -> put(key, JsonPrimitive(value))
                                is Boolean -> put(key, JsonPrimitive(value))
                                is Long -> put(key, JsonPrimitive(value))
                                is Float -> put(key, JsonPrimitive(value))
                                else -> put(key, JsonPrimitive(value.toString()))
                            }
                        }
                    }
                }
            ),
        )
    }

    /**
     * Tracks a search event with the specified search term.
     *
     * @param searchTerm The search term entered by the user.
     * This term will be recorded as part of the search tracking event.
     */
    public fun trackSearch(
        searchTerm: String,
    ) {
        track(
            Event.create(
                tracker = this,
                eventName = "search",
                params = buildMap {
                    put("search_term", JsonPrimitive(searchTerm))
                },
            ),
        )
    }
// </editor-fold>

// <editor-fold desc="Dimension">

    /**
     * Set a permanent custom dimension by value and index.
     *
     * @param value The value for the new Custom Dimension
     * @param key The key of the new Custom Dimension
     */
    public fun setUserProperty(
        key: String,
        value: Any,
    ) {
        coroutine.launch {
            removeUserProperty(key)
            userProperties.add(UserProperty(key, value))
        }
    }

    /**
     * Removes a previously set custom dimension.
     *
     * @param key The name of the propertu.
     */
    public fun removeUserProperty(key: String) {
        coroutine.launch {
            userProperties.removeAll {
                it.name == key
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
        sessionId = Clock.System.now().epochSeconds
        lastEventDate = Clock.System.now()
    }

    /**
     * Resets all session, visitor and campaign information.
     *
     * After calling this method this instance behaves like the app has been freshly installed.
     */
    public fun reset() {
        coroutine.launch {
            logger.log("Reset Session measurementId: $measurementId", LogLevel.Debug)
            userPreferences.reset()
            userProperties.removeAll { true }
            startNewSession()
        }
    }
}
