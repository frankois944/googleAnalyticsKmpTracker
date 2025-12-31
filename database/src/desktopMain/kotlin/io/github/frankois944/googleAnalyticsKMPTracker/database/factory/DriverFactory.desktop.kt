@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.database.factory

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.frankois944.googleAnalyticsKMPTracker.schema.CacheDatabase
import java.util.Properties

public actual class DriverFactory {
    public actual suspend fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY, Properties(), CacheDatabase.Schema.synchronous())
    /*        JdbcSqliteDriver(
                "jdbc:sqlite:${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/$dbName-googleAnalytics-kmp-tracker-$dbVersion.db",
                Properties(),
                CacheDatabase.Schema.synchronous(),
            )*/
}
