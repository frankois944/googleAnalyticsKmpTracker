@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.database.factory

import app.cash.sqldelight.db.SqlDriver
import io.github.frankois944.googleAnalyticsKMPTracker.schema.CacheDatabase

public expect class DriverFactory() {
    public suspend fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver
}

public suspend fun createDatabase(
    driverFactory: DriverFactory,
    dbName: String,
): CacheDatabase {
    val version = 6
    val driver = driverFactory.createDriver(dbName, version)
    val database = CacheDatabase(driver)
    return database
}
