@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.queue.database.factory

import app.cash.sqldelight.db.SqlDriver
import io.github.frankois944.googleAnalyticsKmpTracker.database.schema.CacheDatabase

internal expect object DriverFactory {
    fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver
}

internal fun createDatabase(
    driverFactory: DriverFactory,
    dbName: String,
): CacheDatabase {
    val version = 8
    val driver = driverFactory.createDriver(dbName, version)
    val database = CacheDatabase(driver)
    return database
}
