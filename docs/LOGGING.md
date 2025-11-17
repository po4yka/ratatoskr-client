# Logging with kotlin-logging

This project uses [kotlin-logging](https://github.com/oshai/kotlin-logging) for structured logging across all platforms.

## Overview

kotlin-logging is a Kotlin Multiplatform logging library that provides a lightweight wrapper around SLF4J for JVM/Android and uses platform-specific loggers for other platforms.

## Setup

The logging dependencies are already configured in the project:

- **Common**: `kotlin-logging` and `slf4j-api`
- **Android**: `logback-android` as the SLF4J implementation
- **iOS**: Uses NSLog backend (built into kotlin-logging)

## Usage

### Creating a Logger

Create a logger instance at the top level of your file:

```kotlin
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}
```

### Logging Messages

kotlin-logging supports multiple log levels with lambda-based logging for better performance:

```kotlin
// Debug level - detailed information for debugging
logger.debug { "User ID: $userId attempting login" }

// Info level - general informational messages
logger.info { "Successfully logged in user: $userId" }

// Warn level - warning messages
logger.warn { "Login failed: ${error.message}" }

// Error level - error messages with optional exception
logger.error(exception) { "Exception during login for user: $userId" }
```

### Benefits of Lambda-based Logging

The lambda syntax (`{ }`) ensures that string interpolation only happens if the log level is enabled, improving performance:

```kotlin
// This is efficient - string interpolation only happens if DEBUG is enabled
logger.debug { "Processing ${expensiveOperation()}" }

// Avoid this - expensiveOperation() runs even if DEBUG is disabled
logger.debug("Processing ${expensiveOperation()}")
```

## Configuration

### Android Configuration

The Android logging is configured via `composeApp/src/androidMain/assets/logback.xml`:

```xml
<configuration>
    <appender name="LOGCAT" class="ch.qos.logback.classic.android.LogcatAppender">
        <tagEncoder>
            <pattern>%logger{12}</pattern>
        </tagEncoder>
        <encoder>
            <pattern>%msg%n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="LOGCAT" />
    </root>
</configuration>
```

You can adjust log levels in this file:
- `DEBUG` - Most verbose, includes all logs
- `INFO` - General information and above
- `WARN` - Warnings and errors only
- `ERROR` - Errors only

### iOS Configuration

iOS uses NSLog by default through kotlin-logging. No additional configuration is needed.

## Examples

See the following files for usage examples:

- `shared/src/commonMain/kotlin/com/po4yka/bitesizereader/data/remote/ApiClient.kt` - HTTP client logging
- `shared/src/commonMain/kotlin/com/po4yka/bitesizereader/data/repository/AuthRepositoryImpl.kt` - Repository logging

## Best Practices

1. **Use appropriate log levels**:
   - `debug` - Detailed diagnostic information
   - `info` - General informational events
   - `warn` - Potentially harmful situations
   - `error` - Error events with exceptions

2. **Use lambda syntax** for all log messages to avoid unnecessary string operations

3. **Don't log sensitive data** like passwords, tokens, or personal information

4. **Include context** in log messages (user IDs, operation names, etc.)

5. **Log exceptions** with the error level:
   ```kotlin
   catch (e: Exception) {
       logger.error(e) { "Failed to process request" }
   }
   ```

## Migration from Kermit

If you're migrating code from Kermit, here are the equivalents:

| Kermit | kotlin-logging |
|--------|---------------|
| `Logger.v { }` | `logger.trace { }` |
| `Logger.d { }` | `logger.debug { }` |
| `Logger.i { }` | `logger.info { }` |
| `Logger.w { }` | `logger.warn { }` |
| `Logger.e { }` | `logger.error { }` |

## Resources

- [kotlin-logging GitHub](https://github.com/oshai/kotlin-logging)
- [SLF4J Documentation](https://www.slf4j.org/)
- [Logback Android](https://github.com/tony19/logback-android)
