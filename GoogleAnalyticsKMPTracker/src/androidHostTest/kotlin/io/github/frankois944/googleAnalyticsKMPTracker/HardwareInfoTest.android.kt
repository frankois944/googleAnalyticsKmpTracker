@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import androidx.test.core.app.ApplicationProvider
import io.github.frankois944.googleAnalyticsKMPTracker.context.storeContext
import io.github.frankois944.googleAnalyticsKMPTracker.user.Device
import io.github.frankois944.googleAnalyticsKMPTracker.user.UserAgentProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains

@RunWith(RobolectricTestRunner::class)
class HardwareInfoAndroidTest {
    private val mainThreadSurrogate = StandardTestDispatcher()

    private val siteId = 6

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        storeContext(ApplicationProvider.getApplicationContext())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHardwareInfo() {
        println(
            UserAgentProvider.getUserAgent().also {
                assert(it.isNotEmpty())
            },
        )
    }

    @Test
    fun testLanguage() {
        assertContains(Device.language!!, "-")
        println(Device.language)
    }
}
