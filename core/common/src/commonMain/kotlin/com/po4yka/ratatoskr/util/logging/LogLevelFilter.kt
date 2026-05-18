package com.po4yka.ratatoskr.util.logging

/**
 * Log severity ladder matching slf4j / android.util.Log. Order is the
 * canonical low-to-high severity:
 * [Trace] < [Debug] < [Info] < [Warn] < [Error] < [Off].
 *
 * [Off] is a threshold-only level — used to fully disable a logger.
 * Callers should never pass [Off] as a message level; [LogLevelFilter]
 * defends against that case by always suppressing it.
 */
enum class LogLevel {
    Trace,
    Debug,
    Info,
    Warn,
    Error,
    Off,
}

/**
 * Pure level-threshold predicate. Used by the upcoming `slf4j-android`
 * (or kotlin-logging) wiring that replaces the unmaintained
 * `logback-android` artifact. Keeping the rule in `commonMain` lets the
 * Android, iOS (Darwin appender), and desktop appenders share one
 * decision atom instead of each re-implementing the ladder.
 *
 * Semantics:
 *  - Inclusive: at threshold == message, emit. Matches slf4j and
 *    android.util.Log conventions.
 *  - [LogLevel.Off] as threshold → never emit, including [LogLevel.Error].
 *  - [LogLevel.Off] as message → never emit (defensive; Off is not a
 *    valid event level).
 *
 * Pure, side-effect-free, deterministic.
 */
object LogLevelFilter {
    fun shouldEmit(
        messageLevel: LogLevel,
        threshold: LogLevel,
    ): Boolean {
        if (messageLevel == LogLevel.Off) return false
        if (threshold == LogLevel.Off) return false
        return messageLevel.ordinal >= threshold.ordinal
    }
}
