@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import io.github.frankois944.matomoKMPTracker.CacheDatabase

internal expect class DriverFactory() {
    suspend fun createDriver(): SqlDriver
}

internal suspend fun createDatabase(driverFactory: DriverFactory): CacheDatabase {
    val driver = driverFactory.createDriver()
    val database = CacheDatabase(driver)
    return database
}
