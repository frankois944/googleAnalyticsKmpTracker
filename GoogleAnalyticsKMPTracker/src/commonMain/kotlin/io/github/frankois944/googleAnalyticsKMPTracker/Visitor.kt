@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi

internal suspend fun Visitor.Companion.current(userPreferences: UserPreferences): Visitor {
    var clientID = userPreferences.clientId()
    if (clientID.isNullOrEmpty()) {
        clientID =
            newClientId().also {
                userPreferences.setClientId(it)
            }
    }
    val userId = userPreferences.userId()
    return Visitor(clientId = clientID, userId = userId)
}

// "12345.145989"
private fun Visitor.Companion.newClientId(): String =
    "${Random.nextInt(1, Int.MAX_VALUE)}.${Random.nextInt(1, Int.MAX_VALUE)}"
