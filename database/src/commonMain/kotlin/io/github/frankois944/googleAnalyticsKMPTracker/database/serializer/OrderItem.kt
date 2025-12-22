package io.github.frankois944.googleAnalyticsKMPTracker.database.serializer

import io.github.frankois944.matomoKMPTracker.core.OrderItem

internal fun OrderItem.toSerializedString(): String {
    // To avoid delimiter collision, escape any '|' with '\|'
    fun escape(value: String) = value.replace("|", "\\|")
    return listOf(
        escape(sku),
        escape(name),
        escape(category),
        price.toString(),
        quantity.toString(),
    ).joinToString("|")
}

// Kotlin
internal fun orderItemFromSerializedString(input: String): OrderItem {
    // Splitting on '|' not preceded by '\'
    // For compactness, here it's just split and unescape
    fun unescape(value: String) = value.replace("\\|", "|")
    val parts = input.split("(?<!\\\\)\\|".toRegex())
    require(parts.size == 5) { "Invalid input for OrderItem" }
    return OrderItem(
        sku = unescape(parts[0]),
        name = unescape(parts[1]),
        category = unescape(parts[2]),
        price = parts[3].toDouble(),
        quantity = parts[4].toInt(),
    )
}
