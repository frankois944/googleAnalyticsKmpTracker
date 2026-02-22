package io.github.frankois944.googleAnalyticsKMPTracker

import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
actual open class PlatformBaseTest : BaseTest() {
    actual val context: Any?
        get() = ApplicationProvider.getApplicationContext()
}
