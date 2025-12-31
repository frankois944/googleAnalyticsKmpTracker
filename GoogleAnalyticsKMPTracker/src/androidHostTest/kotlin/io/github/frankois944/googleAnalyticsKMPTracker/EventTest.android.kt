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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

actual fun isAndroid(): Boolean = true

@RunWith(RobolectricTestRunner::class)
class EventTestAndroid {
    private val mainThreadSurrogate = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    suspend fun waitAllEventSent(tracker: Tracker) {
        delay(1.seconds)
        while (tracker.queue!!.first(10).isNotEmpty()) {
            delay(1.seconds)
        }
    }

    @kotlin.test.Test
    fun testPageView() = runTest(timeout = 30.seconds) {
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
                            apiSecret = apiSecret,
                            measurementId = measurementId,
                            url = "https://www.google-analytics.com/mp/collect",
                            context = ApplicationProvider.getApplicationContext(),
                            // For request validation only
                            //url = "https://www.google-analytics.com/debug/mp/collect"
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
