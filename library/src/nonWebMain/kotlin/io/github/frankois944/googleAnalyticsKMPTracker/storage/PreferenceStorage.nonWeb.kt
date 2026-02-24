package io.github.frankois944.googleAnalyticsKMPTracker.storage

internal actual object PersistingStorage {
    private val data: MutableMap<String, String?> = mutableMapOf()

    actual fun set(
        key: String,
        value: String?,
    ) {
        data[key] = value
    }

    actual fun get(
        key: String,
        defaultValue: String?,
    ): String? = data[key] ?: defaultValue

    actual fun remove(key: String) {
        data.remove(key)
    }

    actual fun allKeys(): List<String>? = data.keys.toList()
}
