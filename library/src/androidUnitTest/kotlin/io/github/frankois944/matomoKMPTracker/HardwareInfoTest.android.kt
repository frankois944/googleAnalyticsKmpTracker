@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.matomoKMPTracker

import androidx.test.core.app.ApplicationProvider
import io.github.frankois944.matomoKMPTracker.context.ContextObject
import io.github.frankois944.matomoKMPTracker.context.storeContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        assertContains(Device.create().language!!, "-")
        println(Device.create().language)
    }
}
