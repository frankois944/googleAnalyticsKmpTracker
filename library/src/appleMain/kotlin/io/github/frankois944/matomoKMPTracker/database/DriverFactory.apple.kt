@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import io.github.frankois944.matomoKMPTracker.CacheDatabase

internal actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver =
        NativeSqliteDriver(
            CacheDatabase.Schema.synchronous(),
            "matomo-kmp-tracker.db",
        )
}
