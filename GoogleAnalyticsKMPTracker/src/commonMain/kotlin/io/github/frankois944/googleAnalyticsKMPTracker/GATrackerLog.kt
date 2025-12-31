package io.github.frankois944.googleAnalyticsKMPTracker

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
        message: String,
        level: LogLevel,
    )
}

public class DisabledLogger : GATrackerLogger {
    override fun log(
        message: String,
        level: LogLevel,
    ) {
        // no-op
    }
}

// / This Logger logs every message to the console with a `println` statement.
public class DefaultGATrackerLogger(
    private var minLevel: LogLevel,
) : GATrackerLogger {
    override fun log(
        message: String,
        level: LogLevel,
    ) {
        if (level.level >= minLevel.level) {
            println("[$level]$message")
        }
    }
}
