@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.frankois944.matomoKMPTracker.CacheDatabase
import okio.FileSystem
import java.util.Properties

internal actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver =
        JdbcSqliteDriver(
            "jdbc:sqlite:${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/matomo-kmp-tracker.db",
            Properties(),
            CacheDatabase.Schema.synchronous(),
        )
}
