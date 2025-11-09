@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.matomoKMPTracker

import android.os.StrictMode
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

    private val siteId = 6

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @kotlin.test.Test
    fun testPageView() =
        runTest {
            launch(Dispatchers.Default) {
                val tracker =
                    Tracker.create(
                        url = "https://matomo.spmforkmp.eu/matomo.php",
                        siteId = siteId,
                        context = ApplicationProvider.getApplicationContext(),
                    )
                tracker.queue?.removeAll()
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
                delay(5.seconds)
            }
        }
}
