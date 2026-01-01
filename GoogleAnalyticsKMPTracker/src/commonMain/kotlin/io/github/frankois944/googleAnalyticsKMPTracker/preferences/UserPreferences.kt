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

    // <editor-fold desc="adUserData">
    suspend fun adUserData(): Boolean? =
        database.persistingPreferenceQueries
            .selectPreference("adUserData", scope)
            .awaitAsOneOrNull()
            ?.value_
            ?.toBooleanStrictOrNull()

    suspend fun setAdUserData(value: Boolean?) {
        database.persistingPreferenceQueries
            .insertPreference("adUserData", value.toString(), scope)
    }
    // </editor-fold>

    // <editor-fold desc="ad_personalization">
    suspend fun adPersonalization(): Boolean? =
        database.persistingPreferenceQueries
            .selectPreference("adPersonalization", scope)
            .awaitAsOneOrNull()
            ?.value_
            ?.toBooleanStrictOrNull()

    suspend fun setAdPersonalization(value: Boolean?) {
        database.persistingPreferenceQueries
            .insertPreference("adPersonalization", value.toString(), scope)
    }
    // </editor-fold>

    // <editor-fold desc="Reset all preferences">
    suspend fun reset() {
        database.persistingPreferenceQueries.deleteAllPreferencesWithScope(scope)
    }
// </editor-fold>
}
