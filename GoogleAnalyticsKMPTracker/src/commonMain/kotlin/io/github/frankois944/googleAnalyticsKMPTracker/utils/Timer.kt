package io.github.frankois944.googleAnalyticsKMPTracker.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal fun CoroutineScope.startTimer(
    interval: Duration = 5.seconds,
    onTick: suspend () -> Unit,
): Job =
    this.launch(Dispatchers.Unconfined) {
        try {
            while (true) {
                onTick()
                delay(interval)
            }
        } catch (e: CancellationException) {
            // Coroutine was cancelled
        }
    }
