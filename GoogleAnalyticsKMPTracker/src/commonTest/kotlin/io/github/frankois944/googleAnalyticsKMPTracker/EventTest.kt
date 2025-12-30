@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.OrderItem
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
import kotlin.uuid.Uuid

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
                it.setIsHeartBeat(false)
                it.logger = DefaultGATrackerLogger(minLevel = LogLevel.Verbose)
                it.dispatchBatch()
                it.queue!!.removeAll()
                it.setUserId("Francois.Dabonot")
                assertEquals(
                    emptyList(),
                    it.queue!!.first(10),
                    "Must remain 0 event on start",
                )
            }

    suspend fun waitAllEventSent(tracker: Tracker) {
        val maxSeconds = 20
        var currentSecond = 0
        delay(1.seconds)
        while (tracker.queue!!.first(10).isNotEmpty()) {
            delay(1.seconds)
            val currentEvent = tracker.queue!!.first(10)
            if (currentSecond > maxSeconds && currentEvent.isNotEmpty()) {
                assertEquals(
                    emptyList(),
                    currentEvent,
                    "The queue is not empty after $maxSeconds seconds",
                )
            }
            currentSecond++
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
    fun testPageView() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Unconfined) {
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
                }
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testGoal() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackGoal(goalId = 1, revenue = 42.0)
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testSearch() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackSearch(query = "Test Unit Search 1")
                delay(1.seconds)
                tracker.trackSearch(query = "Test Unit Search 2", category = "Search Unit Test")
                delay(1.seconds)
                tracker.trackSearch(query = "Test Unit Search 3", category = "Search Unit Test", resultCount = 10)
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testCampaign() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackCampaign(name = "Test-Unit-kmp", keyword = "kmp")
                tracker.trackView(listOf("url_test_unit"))
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testInteraction() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackContentInteraction(
                    name = "Test Unit interact1",
                    interaction = "SendSimpleAction",
                    piece = "extra",
                    target = "Backend",
                )
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testImpression() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackContentImpression(
                    name = "Test Unit Impression",
                    piece = "extra print",
                    target = "Backend print",
                )
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testOrder() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                val items =
                    listOf(
                        OrderItem(
                            sku = "SKU-001",
                            name = "Running Shoes",
                            category = "Shoes",
                            price = 89.99,
                            quantity = 1,
                        ),
                        OrderItem(
                            sku = "SKU-002",
                            name = "Socks",
                            category = "Accessories",
                            price = 9.99,
                            quantity = 2,
                        ),
                    )
                tracker.trackOrder(
                    id = "ORDER-${Uuid.random().toHexString()}",
                    items = items,
                    revenue = 109.97, // if not set, you can also provide orderRevenue via optional params
                    subTotal = 99.97,
                    tax = 5.00,
                    shippingCost = 5.00,
                    discount = 0.0,
                )
                waitAllEventSent(tracker)
            }
        }

    @Test
    fun testEvent() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker = getTracker()
                tracker.trackEventWithCategory(
                    category = "event cat1",
                    action = "event action1",
                    name = "event name1",
                    value = 1.0,
                )
                waitAllEventSent(tracker)
            }
        }
}
