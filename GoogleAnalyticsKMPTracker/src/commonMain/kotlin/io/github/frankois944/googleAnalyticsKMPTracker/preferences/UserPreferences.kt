@file:OptIn(ExperimentalTime::class)

package io.github.frankois944.googleAnalyticsKMPTracker.preferences

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import io.github.frankois944.googleAnalyticsKMPTracker.schema.CacheDatabase
import kotlin.time.ExperimentalTime

internal class UserPreferences(
    val database: CacheDatabase,
    val scope: String,
) {
    // <editor-fold desc="Opt Out">

    suspend fun optOut(): Boolean =
        database.persistingPreferenceQueries
            .selectPreference("optOut", scope)
            .awaitAsOneOrNull()
            ?.value_
            ?.toBooleanStrictOrNull() ?: false

    suspend fun setOptOut(value: Boolean) {
        database.persistingPreferenceQueries
            .insertPreference("optOut", value.toString(), scope)
    }
    // </editor-fold>

    // <editor-fold desc="Client Id">

    suspend fun clientId(): String? =
        database.persistingPreferenceQueries
            .selectPreference("setClientId", scope)
            .awaitAsOneOrNull()
            ?.value_

    suspend fun setClientId(value: String?) {
        database.persistingPreferenceQueries
            .insertPreference("setClientId", value, scope)
    }
// </editor-fold>

    // <editor-fold desc="Visitor User Id">
    suspend fun userId(): String? =
        database.persistingPreferenceQueries
            .selectPreference("userId", scope)
            .awaitAsOneOrNull()
            ?.value_

    suspend fun setUserId(value: String?) {
        database.persistingPreferenceQueries
            .insertPreference("userId", value, scope)
    }
// </editor-fold>

    // <editor-fold desc="use heartbeat">
    suspend fun isHeartbeatEnabled(): Boolean =
        database.persistingPreferenceQueries
            .selectPreference("use_heartbeat", scope)
            .awaitAsOneOrNull()
            ?.value_
            ?.toBoolean()
            ?: true

    suspend fun setEnableHeartbeat(value: Boolean): Boolean {
        database.persistingPreferenceQueries
            .insertPreference("use_heartbeat", value.toString(), scope)
        return value
    }
// </editor-fold>

    // <editor-fold desc="Reset all preferences">
    suspend fun reset() {
        database.persistingPreferenceQueries.deleteAllPreferencesWithScope(scope)
    }
// </editor-fold>
}
