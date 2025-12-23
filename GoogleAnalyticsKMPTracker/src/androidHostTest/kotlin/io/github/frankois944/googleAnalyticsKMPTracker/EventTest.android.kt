@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

actual fun isAndroid(): Boolean = true

@RunWith(RobolectricTestRunner::class)
class EventTestAndroid {
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

    @kotlin.test.Test
    fun testPageView() =
        runTest {
            launch(Dispatchers.IO) {
                /*val queuedEvents = mutableListOf<Event>()

                val queue: Queue =
                    object : Queue {
                        override suspend fun eventCount(): Long = 0

                        override suspend fun enqueue(events: List<Event>) {
                            queuedEvents.addAll(events)
                        }

                        override suspend fun first(limit: Long): List<Event> = queuedEvents.subList(0, limit.toInt())

                        override suspend fun remove(events: List<Event>) {
                            // no-op
                        }

                        override suspend fun removeAll() {
                            // no-op
                        }
                    }*/
                val tracker =
                    Tracker
                        .create(
                            url = "https://matomo.spmforkmp.eu/matomo.php",
                            siteId = siteId,
                            context = ApplicationProvider.getApplicationContext(),
                            //   customQueue = queue,
                        ).also {
                            it.logger = DefaultGATrackerLogger(minLevel = LogLevel.Verbose)
                        }
                val nbVisit = 3
                for (i in 1..nbVisit) {
                    tracker.startNewSession()
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
                    delay(1.seconds)
                }
                waitAllEventSent(tracker)
                /*queuedEvents.forEach {
                    println("---")
                    println("DATE = ${it.date}")
                    println("isNewSession = ${it.isNewSession}")
                    println("isPing = ${it.isPing}")
                }
                println("---")*/
            }
        }
}
