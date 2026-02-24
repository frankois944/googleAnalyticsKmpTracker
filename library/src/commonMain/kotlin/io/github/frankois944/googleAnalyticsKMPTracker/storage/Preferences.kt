package io.github.frankois944.googleAnalyticsKMPTracker.storage

import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentSelection
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentState
import io.github.frankois944.googleAnalyticsKMPTracker.model.ConsentType

internal object Preferences {
    const val USER_ID_KEY = "userId"
    const val IS_OPTED_KEY = "isOptedOut"

    var userId: String?
        get() = PersistingStorage.get(USER_ID_KEY)
        set(value) {
            if (value != null) {
                PersistingStorage.set(USER_ID_KEY, value)
            } else {
                PersistingStorage.remove(USER_ID_KEY)
            }
        }

    var isOptedOut: Boolean
        get() = PersistingStorage.get(IS_OPTED_KEY)?.toBoolean() ?: false
        set(value) {
            PersistingStorage.set(IS_OPTED_KEY, value.toString())
        }

    var consent: ConsentSelection
        get() =
            ConsentSelection.build {
                ConsentType.entries.forEach { entry ->
                    PersistingStorage.get(entry.key)?.let { value ->
                        set(entry, ConsentState.fromString(value))
                    } ?: run {
                        set(entry, ConsentState.Granted)
                    }
                }
            }
        set(value) {
            value.asMap().forEach {
                PersistingStorage.set(it.key, it.value)
            }
        }
}
