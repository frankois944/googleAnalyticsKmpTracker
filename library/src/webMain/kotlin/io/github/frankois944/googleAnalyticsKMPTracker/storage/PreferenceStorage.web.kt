package io.github.frankois944.googleAnalyticsKMPTracker.storage

import kotlinx.browser.window

internal actual object PersistingStorage {
    actual fun set(
        key: String,
        value: String?,
    ) {
        if (value == null) {
            remove(key)
        } else {
            window
                .localStorage
                .setItem(key, value)
        }
    }

    actual fun get(
        key: String,
        defaultValue: String?,
    ): String? =
        window
            .localStorage
            .getItem(key) ?: defaultValue

    actual fun remove(key: String) {
        window
            .localStorage
            .removeItem(key)
    }

    actual fun allKeys(): List<String>? {
        val size = window.localStorage.length
        val result = mutableListOf<String>()
        for (i in 0 until size) {
            result.add(window.localStorage.key(i) ?: continue)
        }
        return result
    }
}
