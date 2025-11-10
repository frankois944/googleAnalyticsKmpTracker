@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.frankois944.matomoKMPTracker.CacheDatabase
import io.github.frankois944.matomoKMPTracker.context.ContextObject.context

internal actual class DriverFactory {
    actual suspend fun createDriver(dbName: String): SqlDriver {
        val context = context?.get()
        requireNotNull(context) {
            "Context can't be null"
        }
        return AndroidSqliteDriver(
            CacheDatabase.Schema.synchronous(),
            context,
            "$dbName-matomo-kmp-tracker.db",
        )
    }
}
