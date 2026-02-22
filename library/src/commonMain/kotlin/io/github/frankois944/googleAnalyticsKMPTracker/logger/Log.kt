package io.github.frankois944.googleAnalyticsKMPTracker.logger

public enum class LogLevel(
    internal val level: Int,
) {
    Verbose(10),
    Debug(20),
    Info(30),
    Warning(40),
    Error(50),
}

public interface GATrackerLogger {
    public fun log(
        level: LogLevel,
        message: () -> String,
    )
}

// / This Logger logs every message to the console with a `println` statement.
public class Logger(
    internal var minLevel: LogLevel,
) : GATrackerLogger {
    override fun log(
        level: LogLevel,
        message: () -> String,
    ) {
        if (level.level >= minLevel.level) {
            println("[GATracker][$level]${message()}")
        }
    }
}

internal val LOG = Logger(LogLevel.Debug)
