package io.github.frankois944.googleAnalyticsKMPTracker.sample

import androidx.compose.ui.window.ComposeUIViewController
import io.github.frankois944.googleAnalyticsKMPTracker.Tracker
import kotlinx.coroutines.runBlocking
import platform.UIKit.UIViewController

fun MainViewController() : UIViewController {
    return ComposeUIViewController { App() }
}