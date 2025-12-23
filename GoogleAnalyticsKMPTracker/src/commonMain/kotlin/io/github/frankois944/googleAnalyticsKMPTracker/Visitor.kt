@file:OptIn(ExperimentalUuidApi::class)

package io.github.frankois944.googleAnalyticsKMPTracker

import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor
import io.github.frankois944.googleAnalyticsKMPTracker.preferences.UserPreferences
import io.github.frankois944.googleAnalyticsKMPTracker.utils.UuidGenerator
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal suspend fun Visitor.Companion.current(userPreferences: UserPreferences): Visitor {
    var appInstanceId = userPreferences.appInstanceId()
    if (appInstanceId.isNullOrEmpty()) {
        appInstanceId =
            newVisitorID().also {
                userPreferences.setAppInstanceId(it)
            }
    }
    val userId = userPreferences.userId()
    return Visitor(appInstanceId = appInstanceId, userId = userId)
}

private fun Visitor.Companion.newVisitorID(): String = UuidGenerator.nextUuid()
