@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import io.github.frankois944.matomoKMPTracker.context.ContextObject
import io.github.frankois944.matomoKMPTracker.schema.CacheDatabase

public actual class DriverFactory {
    public actual suspend fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver {
        val context = ContextObject.context?.get()
        requireNotNull(context) {
            "Context can't be null"
        }
        return AndroidSqliteDriver(
            CacheDatabase.Schema.synchronous(),
            context,
            "$dbName-matomo-kmp-tracker-$dbVersion.db",
        )
    }
}
