@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseTest {
    private val mainThreadSurrogate = StandardTestDispatcher()

    lateinit var config: GATrackerConfig

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
