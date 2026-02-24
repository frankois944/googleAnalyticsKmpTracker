package io.github.frankois944.googleAnalyticsKMPTracker.storage

internal expect object PersistingStorage {
    fun set(
        key: String,
        value: String?,
    )

    fun get(
        key: String,
        defaultValue: String? = null,
    ): String?

    fun allKeys(): List<String>?

    fun remove(key: String)
}
