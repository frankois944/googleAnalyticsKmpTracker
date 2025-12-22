@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package io.github.frankois944.googleAnalyticsKMPTracker.database.factory

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import app.cash.sqldelight.driver.worker.expected.Worker
import io.github.frankois944.matomoKMPTracker.schema.CacheDatabase

public actual class DriverFactory actual constructor() {
    public actual suspend fun createDriver(
        dbName: String,
        dbVersion: Int,
    ): SqlDriver =
        WebWorkerDriver(
            Worker(
                js("""new URL("@cashapp/sqldelight-sqljs-worker/sqljs.worker.js", import.meta.url)"""),
            ),
        ).also {
            CacheDatabase.Schema.awaitCreate(it)
        }
}
