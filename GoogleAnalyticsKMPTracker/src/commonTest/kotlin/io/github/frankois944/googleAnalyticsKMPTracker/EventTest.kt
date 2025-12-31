@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

expect fun isAndroid(): Boolean

@OptIn(ExperimentalUuidApi::class)
class EventTest {
    private val mainThreadSurrogate = StandardTestDispatcher()

    suspend fun getTracker(): Tracker =
        Tracker
            .create(
                apiSecret = apiSecret,
                measurementId = measurementId,
                url = "https://www.google-analytics.com/mp/collect"
                // For request validation only
                //url = "https://www.google-analytics.com/debug/mp/collect"
            ).also {
                it.logger = DefaultGATrackerLogger(minLevel = LogLevel.Verbose)
                it.dispatchBatch()
                it.queue!!.removeAll()
                it.setUserId("my_user_id")
                assertEquals(
                    emptyList(),
                    it.queue!!.first(1),
                    "Must remain 0 event on start",
                )
            }

    suspend fun waitAllEventSent(tracker: Tracker) {
        delay(1.seconds)
        while (tracker.queue!!.first(1).isNotEmpty()) {
            delay(500.milliseconds)
        }
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testPageView() = runTest(
        timeout = 30.seconds
    ) {
        if (isAndroid()) {
            return@runTest
        }
        val tracker = getTracker()
        val nbVisit = 1
        for (i in 1..nbVisit) {
            println("Session send $i")
            tracker.trackView(listOf("index11"))
            delay(50.milliseconds)
            tracker.trackView(listOf("index1", "index21"))
            delay(50.milliseconds)
            tracker.trackView(listOf("index1", "index2", "index31"))
            delay(50.milliseconds)
            tracker.trackView(listOf("index1", "index2", "index3", "index41"))
            delay(50.milliseconds)
            tracker.trackView(listOf("index1", "index2", "index3", "index4", "index51"))
            delay(50.milliseconds)
            tracker.trackView("MyScreenName")
            delay(50.milliseconds)
        }
        waitAllEventSent(tracker)
    }

    @Test
    fun testSearch() = runTest(timeout = 30.seconds) {
        if (isAndroid()) {
            return@runTest
        }
        val tracker = getTracker()
        launch(Dispatchers.Unconfined) {
            tracker.trackSearch("Test Unit Search 1")
            delay(1.seconds)
            tracker.trackSearch("Test Unit Search 2")
            delay(1.seconds)
            tracker.trackSearch("Test Unit Search 3")
            waitAllEventSent(tracker)
        }
    }

    @Test
    fun testEvent() = runTest(timeout = 30.seconds) {
        if (isAndroid()) {
            return@runTest
        }
        val tracker = getTracker()
        launch(Dispatchers.Unconfined) {
            tracker.trackEvent(
                name = "event_name1",
                parameters = buildMap {
                    put("category", "Button")
                    put("value", 42)
                }
            )
            waitAllEventSent(tracker)
        }
    }
}
