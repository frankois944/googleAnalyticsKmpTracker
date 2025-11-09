@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.matomoKMPTracker.database

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver
import io.github.frankois944.matomoKMPTracker.CacheDatabase

internal actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val driver = createDefaultWebWorkerDriver()
        CacheDatabase.Schema.awaitCreate(driver)
        return driver
    }
}
