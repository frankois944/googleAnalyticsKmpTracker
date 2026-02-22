@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

open class BaseTest {
    @BeforeTest
    fun setUp() =
        runTest {
            Dispatchers.setMain(StandardTestDispatcher())
        }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
