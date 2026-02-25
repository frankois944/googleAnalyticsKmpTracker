package io.github.frankois944.googleAnalyticsKMPTracker.queue.database.factory

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import io.github.frankois944.googleAnalyticsKmpTracker.database.schema.CacheDatabase
import okio.FileSystem
import java.util.Properties

internal actual object DriverFactory {
    actual fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver =
        JdbcSqliteDriver(
            "jdbc:sqlite:${FileSystem.SYSTEM_TEMPORARY_DIRECTORY}/$dbName-googleAnalytics-kmp-tracker-$dbVersion.db",
            Properties(),
            CacheDatabase.Schema,
        )
}
