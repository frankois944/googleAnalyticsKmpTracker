package io.github.frankois944.googleAnalyticsKMPTracker.logger

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.mutableLoggerConfigInit
import co.touchlab.kermit.platformLogWriter

public enum class LogLevel(
    internal val level: Int,
) {
    Verbose(10),
    Debug(20),
    Info(30),
    Warning(40),
    Error(50),
    ;

    internal fun toKermitSeverity(): Severity =
        when (this) {
            Verbose -> Severity.Verbose
            Debug -> Severity.Debug
            Info -> Severity.Info
            Warning -> Severity.Warn
            Error -> Severity.Error
        }
}

internal val loggerConfig =
    mutableLoggerConfigInit(
        logWriters = listOf(platformLogWriter(NoTagFormatter)).toTypedArray(),
        minSeverity = Severity.Debug,
    )

internal val LOG =
    Logger(
        loggerConfig,
        "GATracker",
    )
