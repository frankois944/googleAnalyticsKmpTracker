package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.utils.startTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

internal class HeartBeat(
    private val tracker: Tracker,
) {
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) {
            tracker.logger.log("Heartbeat already started", LogLevel.Verbose)
            return
        }
        tracker.logger.log("Starting heartbeat", LogLevel.Info)
        job =
            tracker.coroutine.launch(Dispatchers.Unconfined) {
                startTimer(10.seconds) {
                    tracker.logger.log("Sending heartbeat event", LogLevel.Info)
                    try {
                        tracker.dispatcher.sendSingleEvent(
                            event =
                                Event.create(
                                    tracker = tracker,
                                    isCustomAction = false,
                                    isPing = true,
                                    visitor = Visitor.current(tracker.userPreferences!!),
                                ),
                        )
                    } catch (ex: Exception) {
                        tracker.logger.log("Sending heartbeat failed, $ex", LogLevel.Warning)
                    }
                }
            }
    }

    fun stop() {
        if (job == null) {
            tracker.logger.log("Heartbeat already stopped", LogLevel.Verbose)
        } else {
            tracker.logger.log("Stopping heartbeat", LogLevel.Info)
            job?.cancel()
            job = null
        }
    }
}
