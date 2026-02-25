@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TrackerTest : PlatformBaseTest() {
    @BeforeTest
    fun setup() {
        config =
            GATrackerConfig(
                measurementId = MEASUREMENT_ID,
                apiSecret = API_SECRET,
                context = context,
            )
        config.logLevel = LogLevel.Verbose
        config.userId = "41414141414"
    }

    @Test
    fun testTrackView() =
        runTest {
            launch(Dispatchers.Unconfined) {
                GATracker.start(config)
                GATracker.trackView("Test View")
                GATracker.trackView("Test View1")
                GATracker.trackView("Test View2")
                GATracker.trackView("Test View3")
                delay(2.seconds)
            }
        }
}
