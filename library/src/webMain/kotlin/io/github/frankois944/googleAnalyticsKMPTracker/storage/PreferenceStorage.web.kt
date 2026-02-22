package io.github.frankois944.googleAnalyticsKMPTracker.storage

import kotlinx.browser.window

internal actual object PreferenceStorage {
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
}
