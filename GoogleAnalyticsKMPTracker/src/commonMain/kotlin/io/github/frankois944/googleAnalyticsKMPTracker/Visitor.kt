@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import io.github.frankois944.googleAnalyticsKMPTracker.utils.UuidGenerator
import kotlin.uuid.ExperimentalUuidApi

internal suspend fun Visitor.Companion.current(userPreferences: UserPreferences): Visitor {
    var id = userPreferences.clientId()
    if (id.isNullOrEmpty()) {
        id =
            newVisitorID().also {
                userPreferences.setClientId(it)
            }
    }
    val userId = userPreferences.userId()
    return Visitor(id = id, userId = userId)
}

private fun Visitor.Companion.newVisitorID(): String = UuidGenerator.nextUuid()
