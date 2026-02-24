@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentState
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ConsentTest : PlatformBaseTest() {
    @BeforeTest
    fun setup() {
        config =
            GATrackerConfig(
                measurementId = MEASUREMENT_ID,
                apiSecret = API_SECRET,
                context = context,
            )
        config.logLevel = LogLevel.Verbose
        config.userId = Uuid.random().toHexDashString()
    }

    @Test
    fun testConsentSelection() {
        GATracker.start(config)
        ConsentType.entries.forEach { type ->
            assertEquals(ConsentState.Granted, GATracker.consents[type])
        }
        GATracker.updateConsent {
            set(ConsentType.AnalyticsStorage, ConsentState.Denied)
            set(ConsentType.AdPersonalization, ConsentState.Denied)
        }
        assertEquals(ConsentState.Denied, GATracker.consents[ConsentType.AnalyticsStorage])
        assertEquals(ConsentState.Denied, GATracker.consents[ConsentType.AdPersonalization])
        assertEquals(ConsentState.Granted, GATracker.consents[ConsentType.AdUserData])
        assertEquals(ConsentState.Granted, GATracker.consents[ConsentType.AdStorage])
    }

    @Test
    fun testSendConsent() =
        runTest {
            GATracker.start(config)
            launch(Dispatchers.Unconfined) {
                GATracker.updateConsent {
                    set(ConsentType.AnalyticsStorage, ConsentState.Denied)
                    set(ConsentType.AdUserData, ConsentState.Denied)
                }
                delay(30.seconds)
            }
        }
}
