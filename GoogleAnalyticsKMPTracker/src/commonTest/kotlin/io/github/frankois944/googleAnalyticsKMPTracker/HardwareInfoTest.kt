@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.utils.UuidGenerator
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
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
    fun testUUID() {
        assertEquals(UuidGenerator.nextUuid().length, 16)
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
