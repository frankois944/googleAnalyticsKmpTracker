@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.database.factory

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.github.frankois944.googleAnalyticsKMPTracker.schema.CacheDatabase

public actual class DriverFactory {
    public actual suspend fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver =
        NativeSqliteDriver(
            CacheDatabase.Schema.synchronous(),
            "$dbName-matomo-kmp-tracker-$dbVersion.db",
        )
}
