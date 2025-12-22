package io.github.frankois944.googleAnalyticsKMPTracker.utils

import kotlin.random.Random

internal object UuidGenerator {
    private fun Byte.toHex(): String =
        this
            .toInt()
            .and(0xFF)
            .toString(16)
            .padStart(2, '0')

    fun nextUuid(): String =
        Random
            .nextBytes(8)
            .joinToString("") { it.toHex() }
}
