package io.github.frankois944.googleAnalyticsKMPTracker.database.serializer

import io.github.frankois944.matomoKMPTracker.core.CustomDimension

// Kotlin
internal fun CustomDimension.toSerializedString(): String {
    // Escape any pipe characters
    fun escape(value: String) = value.replace("|", "\\|")
    return "$index|${escape(value)}"
}

// Kotlin
internal fun customDimensionFromSerializedString(input: String): CustomDimension {
    // Split only on unescaped pipe
    // For this 2-field format, simple split is enough
    val idx = input.indexOf('|')
    require(idx != -1) { "Invalid string: no delimiter" }
    val indexPart = input.substring(0, idx)
    val valuePart = input.substring(idx + 1).replace("\\|", "|")
    return CustomDimension(index = indexPart.toInt(), value = valuePart)
}
