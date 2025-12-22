package io.github.frankois944.googleAnalyticsKmpTracker.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MatomoKmpTrackerSample",
    ) {
        App()
    }
}