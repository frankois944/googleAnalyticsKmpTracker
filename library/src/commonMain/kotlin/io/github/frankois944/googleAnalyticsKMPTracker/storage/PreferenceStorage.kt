package io.github.frankois944.googleAnalyticsKMPTracker.storage

internal expect object PreferenceStorage {
    fun set(
        key: String,
        value: String?,
    )

    fun get(
        key: String,
        defaultValue: String? = null,
    ): String?

    fun remove(key: String)
}
