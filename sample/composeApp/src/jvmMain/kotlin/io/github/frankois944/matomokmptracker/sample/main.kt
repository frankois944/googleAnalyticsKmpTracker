package io.github.frankois944.googleAnalyticsKMPTracker.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.frankois944.googleAnalyticsKMPTracker.DefaultMatomoTrackerLogger
import io.github.frankois944.googleAnalyticsKMPTracker.LogLevel
import io.github.frankois944.googleAnalyticsKMPTracker.Tracker
import kotlinx.coroutines.runBlocking

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MatomoKmpTrackerSample",
    ) {
        App()
    }
}