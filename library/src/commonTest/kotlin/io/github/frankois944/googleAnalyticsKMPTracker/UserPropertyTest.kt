@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserPropertyTest : PlatformBaseTest() {
    @BeforeTest
    fun setup() {
        config =
            GATrackerConfig(
                measurementId = MEASUREMENT_ID,
                apiSecret = API_SECRET,
                context = context,
            )
        config.logLevel = LogLevel.Verbose
        config.userId = "123456789"
    }

    @Test
    fun testUserProperty() =
        runTest {
            launch(Dispatchers.Unconfined) {
                GATracker.start(config)
                GATracker.trackView("testUserProperty - Test View[${Random.nextInt()}]")
                delay(1.seconds)
                GATracker.setUserProperty(
                    "testSingleKey0",
                    "SingleValue0 ${Random.nextInt()}",
                )
                GATracker.setUserProperty(
                    "testSingleKey1",
                    "SingleValue1 ${Random.nextInt()}",
                )
                GATracker.setUserProperty(
                    "testSingleKey2",
                    "SingleValue2 ${Random.nextInt()}",
                )
                delay(1.seconds)
                GATracker.trackView("testUserProperty - Test View[${Random.nextInt()}]")
                delay(2.seconds)
            }
        }
}
