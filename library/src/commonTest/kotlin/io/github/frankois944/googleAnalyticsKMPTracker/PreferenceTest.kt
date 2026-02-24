@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PreferenceTest : PlatformBaseTest() {
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
}
