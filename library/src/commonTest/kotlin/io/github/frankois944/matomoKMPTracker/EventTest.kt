@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.matomoKMPTracker

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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

expect fun isAndroid(): Boolean

@OptIn(ExperimentalUuidApi::class)
class EventTest {
    private val mainThreadSurrogate = StandardTestDispatcher()
    private val siteId = 6

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
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                val nbVisit = 3
                for (i in 1..nbVisit) {
                    tracker.startNewSession()
                    delay(50.milliseconds)
                    println("Session send $i")
                    tracker.trackView(listOf("index1"))
                    delay(50.milliseconds)
                    tracker.trackView(listOf("index2"))
                    delay(50.milliseconds)
                    tracker.trackView(listOf("index3"))
                    delay(50.milliseconds)
                    tracker.trackView(listOf("index4"))
                    delay(50.milliseconds)
                    tracker.trackView(listOf("index5"))
                    delay(50.milliseconds)
                    tracker.trackView(listOf("index6"))
                    delay(50.milliseconds)
                    tracker.dispatchBatch()
                }
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testGoal() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackGoal(goalId = 1, revenue = 42.0f)
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testSearch() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackSearch(query = "Test Unit")
                tracker.trackSearch(query = "Test Unit2", category = "Search Unit Test")
                tracker.trackSearch(query = "Test Unit2", category = "Search Unit Test", resultCount = 10)
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testCampaign() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackCampaign(name = "Test-Unit-kmp", keyword = "kmp")
                tracker.trackView(listOf("url_test_unit"))
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testInteraction() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackContentInteraction(
                    name = "Test Unut interact1",
                    interaction = "SendSimpleAction",
                    piece = "extra",
                    target = "Backend",
                )
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testImpression() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackContentImpression(
                    name = "Test Unit Impression",
                    piece = "extra print",
                    target = "Backend print",
                )
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testOrder() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackOrder(
                    id = Uuid.random().toHexString(),
                    items =
                        listOf(
                            OrderItem(
                                sku = "SKU 1",
                                name = "product_1",
                                category = "cat_1",
                                price = 100.0f,
                                quantity = 1,
                            ),
                            OrderItem(
                                sku = "SKU 2",
                                name = "product_2",
                                category = "cat_2",
                                price = 10.0f,
                                quantity = 10,
                            ),
                        ),
                    revenue = 42.0f,
                    subTotal = 21.0f,
                    tax = 4.0f,
                    shippingCost = 10.0f,
                    discount = 12.0f,
                )
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }

    @Test
    fun testEvent() =
        runTest {
            if (isAndroid()) {
                return@runTest
            }
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                    )
                tracker.trackEventWithCategory(
                    category = "event cat1",
                    action = "event action1",
                    name = "event name1",
                    value = 1.0f,
                )
                delay(1.seconds)
                tracker.dispatchBatch()
                delay(2.seconds)
            }
        }
}
