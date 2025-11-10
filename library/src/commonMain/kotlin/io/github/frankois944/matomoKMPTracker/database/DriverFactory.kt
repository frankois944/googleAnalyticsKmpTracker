@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.db.SqlDriver
import io.github.frankois944.matomoKMPTracker.CacheDatabase

internal expect class DriverFactory() {
    suspend fun createDriver(dbName: String): SqlDriver
}

internal suspend fun createDatabase(
    driverFactory: DriverFactory,
    dbName: String,
): CacheDatabase {
    val driver = driverFactory.createDriver(dbName)
    val database = CacheDatabase(driver)
    return database
}
