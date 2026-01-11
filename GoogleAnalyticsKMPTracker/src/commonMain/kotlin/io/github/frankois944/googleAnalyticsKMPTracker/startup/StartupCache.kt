package io.github.frankois944.googleAnalyticsKMPTracker.startup

import io.github.frankois944.googleAnalyticsKMPTracker.core.Event
import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.core.queue.Queue
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import io.github.frankois944.googleAnalyticsKMPTracker.user.current
import kotlin.jvm.JvmInline

@JvmInline
internal value class UserId(val value: String?)
@JvmInline
internal value class IsOptOut(val value: Boolean)
@JvmInline
internal value class AdUserDataEnabled(val value: Boolean?)
@JvmInline
internal value class AdPersonalizationEnabled(val value: Boolean?)

internal class StartupCache(
    val events: MutableList<Event> = mutableListOf(),
    var userId: UserId? = null,
    var adUserDataEnabled: AdUserDataEnabled? = null,
    var adPersonalizationEnabled: AdPersonalizationEnabled? = null,
    var isOptOut: IsOptOut? = null
) {
    fun addEvent(event: Event) {
        events.add(event)
    }

    suspend fun flush(queue: Queue, userPreferences: UserPreferences) {
        userId?.let { userId ->
            userPreferences.setUserId(userId.value)
        }
        adUserDataEnabled?.let { adUserDataEnabled ->
            userPreferences.setAdUserData(adUserDataEnabled.value)
        }
        adPersonalizationEnabled?.let { adPersonalizationEnabled ->
            userPreferences.setAdPersonalization(adPersonalizationEnabled.value)
        }
        isOptOut?.let { isOptOut ->
            userPreferences.setOptOut(isOptOut.value)
        }
        events.forEach { event ->
            if (event.visitor == null) {
                event.visitor = Visitor.current(userPreferences)
            }
        }
        queue.enqueue(events)
    }
}