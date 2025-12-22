package io.github.frankois944.googleAnalyticsKmpTracker.sample

import androidx.compose.ui.window.ComposeUIViewController
import io.github.frankois944.googleAnalyticsKmpTracker.Tracker
import kotlinx.coroutines.runBlocking
import platform.UIKit.UIViewController

fun MainViewController() : UIViewController {
    return ComposeUIViewController { App() }
}