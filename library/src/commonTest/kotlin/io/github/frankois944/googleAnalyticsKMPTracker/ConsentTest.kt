package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.logger.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentState
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentType
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsentTest : PlatformBaseTest() {
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
}
