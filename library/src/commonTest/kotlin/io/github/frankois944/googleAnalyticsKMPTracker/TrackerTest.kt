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

const val MEASUREMENT_ID: String = "G-3L12ZLV24G"
const val API_SECRET: String = "O_2cm_slTiyb3XcqXPpTiA"

class TrackerTest : PlatformBaseTest() {
    private lateinit var config: GATrackerConfig

    @BeforeTest
    fun setup() {
        config =
            GATrackerConfig(
                measurementId = MEASUREMENT_ID,
                apiSecret = API_SECRET,
                context = context,
            )
        config.logLevel = LogLevel.Verbose
    }

    @Test
    fun testUserId() {
        GATracker.start(config)
        assertNull(GATracker.userId)
        GATracker.userId = Uuid.random().toHexString()
        assertNotNull(GATracker.userId)
    }

    @Test
    fun testIsOptedOut() {
        config.isOptedOut = false
        GATracker.start(config)
        assertNotNull(GATracker.isOptedOut)
    }

    @Test
    fun testTrackView() =
        runTest {
            GATracker.start(config)
            launch(Dispatchers.Unconfined) {
                GATracker.trackView("Test View")
                GATracker.trackView("Test View1")
                GATracker.trackView("Test View2")
                GATracker.trackView("Test View3")
                delay(5.seconds)
            }
        }
}
