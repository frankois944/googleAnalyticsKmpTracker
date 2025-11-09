package io.github.frankois944.matomoKMPTracker

import io.github.frankois944.matomoKMPTracker.utils.startTimer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
                startTimer(tracker.dispatchInterval) {
                    tracker.logger.log("Sending heartbeat event", LogLevel.Info)
                    tracker.queue(
                        event =
                            Event(
                                tracker = tracker,
                                isCustomAction = false,
                                isPing = true,
                            ),
                        nextEventStartsANewSession = false,
                    )
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
