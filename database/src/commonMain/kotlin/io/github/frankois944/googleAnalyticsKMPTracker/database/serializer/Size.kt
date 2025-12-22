package io.github.frankois944.googleAnalyticsKMPTracker.database.serializer

import io.github.frankois944.googleAnalyticsKMPTracker.core.Size

// Kotlin
internal fun Size.toSerializedString(): String = "$width|$height"

// Kotlin
internal fun sizeFromSerializedString(input: String): Size {
    val parts = input.split("|")
    require(parts.size == 2) { "Invalid input for Size" }
    return Size(
        width = parts[0].toLong(),
        height = parts[1].toLong(),
    )
}
