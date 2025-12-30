package io.github.frankois944.googleAnalyticsKMPTracker.database.serializer

import io.github.frankois944.googleAnalyticsKMPTracker.core.Visitor

internal fun Visitor.toSerializedString(): String = clientId + ";" + (userId ?: "")

internal fun visitorFromSerializedString(data: String): Visitor {
    val parts = data.split(';', limit = 2)
    val id = parts.getOrNull(0) ?: throw IllegalArgumentException("Missing id")
    val userId = parts.getOrNull(1).takeUnless { it.isNullOrEmpty() }
    return Visitor(id, userId)
}
