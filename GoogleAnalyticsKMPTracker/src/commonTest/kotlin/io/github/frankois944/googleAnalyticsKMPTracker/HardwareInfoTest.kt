@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.user.Device
import io.github.frankois944.googleAnalyticsKMPTracker.user.UserAgentProvider
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.uuid.ExperimentalUuidApi

class HardwareInfoTest {
    @Test
    fun testHardwareInfo() {
        if (isAndroid()) {
            return
        }
        println(UserAgentProvider.getUserAgent())
    }

    @Test
    fun testLanguage() {
        if (isAndroid()) {
            return
        }
        assertContains(Device.language!!, "-")
        println(Device.language)
    }
}
