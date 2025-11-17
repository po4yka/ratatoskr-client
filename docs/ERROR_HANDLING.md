# Error Handling Strategy

**Last Updated**: 2025-11-17
**Status**: Implemented

---

## Overview

Bite-Size Reader uses a comprehensive, user-friendly error handling system with automatic retry mechanisms and offline support.

## Architecture

### Error Types (`AppError`)

All errors in the application are represented using the `AppError` sealed class:

```kotlin
sealed class AppError {
    data class NetworkError(message: String, isTimeout: Boolean = false)
    data class ServerError(message: String, statusCode: Int? = null)
    data class UnauthorizedError(message: String)
    data class NotFoundError(message: String, resource: String? = null)
    data class ValidationError(message: String, field: String? = null)
    data class RateLimitError(message: String, retryAfterSeconds: Int? = null)
    data class DatabaseError(message: String)
    data class ParsingError(message: String)
    data class OfflineError(message: String)
    data class UnknownError(message: String)
}
```

### Key Features

1. **User-Friendly Messages**: Every error includes a clear, actionable message
2. **Retry Strategies**: Errors know how they should be retried
3. **HTTP Mapping**: Automatic mapping from HTTP status codes
4. **Exception Conversion**: Intelligent exception-to-error conversion

---

## Usage

### In Use Cases / Repositories

```kotlin
suspend fun getSummaries(): Result<List<Summary>> {
    return try {
        val summaries = api.getSummaries()
        Result.success(summaries)
    } catch (e: Exception) {
        Result.failure(e.toAppError())
    }
}
```

### In ViewModels

```kotlin
viewModelScope.launch {
    summariesFlow
        .catch { error ->
            val appError = error.toAppError()
            _state.value = _state.value.copy(
                error = appError.getUserMessage(),
                appError = appError,
                canRetry = appError.isRetryable()
            )
        }
        .collect { summaries ->
            _state.value = _state.value.copy(
                summaries = summaries,
                error = null,
                appError = null
            )
        }
}
```

### In UI (Compose)

```kotlin
state.appError?.let { error ->
    ErrorBanner(
        title = error.getErrorTitle(),
        message = error.getUserMessage(),
        canRetry = state.canRetry,
        onRetry = { viewModel.retry() },
        onDismiss = { viewModel.clearError() }
    )
}
```

---

## Retry Strategies

### Available Strategies

| Strategy | Use Case | Example |
|----------|----------|---------|
| **Immediate** | Network blips | Retry 5 times immediately |
| **ExponentialBackoff** | Server errors | Retry with 1s, 2s, 4s, 8s delays |
| **FixedDelay** | Rate limits | Retry after specified delay |
| **WhenOnline** | Offline operations | Queue until connection restored |
| **NoRetry** | User errors | Don't retry, require user action |

### Auto-Retry Configuration

```kotlin
fun AppError.getRetryStrategy(): RetryStrategy = when (this) {
    is NetworkError ->
        if (isTimeout) ExponentialBackoff(maxAttempts = 3)
        else Immediate(maxAttempts = 5)

    is ServerError ->
        ExponentialBackoff(maxAttempts = 3, initialDelayMs = 2000)

    is RateLimitError ->
        FixedDelay(maxAttempts = 3, delayMs = (retryAfterSeconds ?: 60) * 1000L)

    is OfflineError -> WhenOnline

    else -> NoRetry
}
```

### Manual Retry

```kotlin
// In ViewModel
fun retry() {
    if (_state.value.canRetry) {
        _state.value = _state.value.copy(
            retryAttempt = _state.value.retryAttempt + 1
        )
        loadData() // Retry the operation
    }
}
```

---

## HTTP Error Mapping

### Status Code Mapping

| Code | Error Type | User Message |
|------|------------|--------------|
| 400 | ValidationError | "Bad request. Please check your input." |
| 401 | UnauthorizedError | "Your session has expired. Please log in again." |
| 403 | UnauthorizedError | "Access forbidden. You don't have permission." |
| 404 | NotFoundError | "The requested content could not be found." |
| 408 | NetworkError | "Request timeout. Please try again." |
| 429 | RateLimitError | "Too many requests. Please slow down." |
| 500 | ServerError | "The server is temporarily unavailable." |
| 502 | ServerError | "Bad gateway. Server is having trouble." |
| 503 | ServerError | "Service unavailable. Down for maintenance." |
| 504 | ServerError | "Gateway timeout. Server took too long." |

### Usage with Ktor

```kotlin
try {
    val response = client.get("/api/summaries")
    Result.success(response)
} catch (e: ClientRequestException) {
    Result.failure(e.response.status.toAppError())
} catch (e: ServerResponseException) {
    Result.failure(e.response.status.toAppError())
}
```

---

## Error Messages

### Design Principles

1. **Be specific**: Tell user exactly what went wrong
2. **Be actionable**: Suggest what user can do
3. **Be friendly**: Avoid technical jargon
4. **Be concise**: Keep to 1-2 sentences

### Examples

**Bad**:
```
"Error code 500"
"Exception in thread 'main' java.net.SocketTimeoutException"
```

**Good**:
```
"No internet connection. Please check your network and try again."
"The server is temporarily unavailable. Please try again in a few moments."
"Your session has expired. Please log in again to continue."
```

---

## State Management

### Error State in UI State

```kotlin
data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,          // Legacy string error
    val appError: AppError? = null,      // Enhanced error with retry
    val canRetry: Boolean = false,       // Is current error retryable?
    val retryAttempt: Int = 0,           // Current retry attempt number
)
```

### Lifecycle

1. **Error Occurs**: Exception caught, converted to AppError
2. **State Updated**: Error set in state with retry capability
3. **UI Shows Error**: Banner/snackbar with message and retry button
4. **User Retries**: Increment attempt, retry operation
5. **Success**: Clear error state
6. **Max Attempts**: Show final error, disable retry

---

## Best Practices

### DO

✅ Convert all exceptions to AppError
✅ Provide user-friendly messages
✅ Enable retry for network/server errors
✅ Log errors with context
✅ Clear errors on success
✅ Show error state in UI
✅ Provide dismiss/retry actions

### DON'T

❌ Show raw exception messages to users
❌ Silently swallow errors
❌ Retry user input errors
❌ Retry indefinitely without limit
❌ Block UI during retry
❌ Show technical stack traces

---

## Testing

### Test Error Conversion

```kotlin
@Test
fun `network exception converts to NetworkError`() {
    val exception = Exception("No network connection")
    val appError = exception.toAppError()

    assertTrue(appError is AppError.NetworkError)
    assertTrue(appError.isRetryable())
}
```

### Test Retry Logic

```kotlin
@Test
fun `retry increments attempt count`() = runTest {
    val viewModel = SummaryListViewModel(...)

    // Trigger error
    viewModel.loadSummaries()
    advanceUntilIdle()

    // Retry
    viewModel.retry()

    assertEquals(1, viewModel.state.value.retryAttempt)
}
```

### Test Error UI

```kotlin
@Test
fun `error banner shows retry button for retryable errors`() {
    composeTestRule.setContent {
        ErrorBanner(
            error = AppError.NetworkError(),
            canRetry = true,
            onRetry = { },
            onDismiss = { }
        )
    }

    composeTestRule.onNodeWithText("Retry").assertExists()
}
```

---

## Future Enhancements

### Planned Improvements

1. **Offline Queue**: Automatically queue failed requests when offline
2. **Error Analytics**: Track error rates and types
3. **Smart Retry**: ML-based retry strategy selection
4. **Error Recovery**: Automatic recovery strategies for common errors
5. **Circuit Breaker**: Stop retrying after too many failures
6. **Fallback Data**: Show cached data when real-time fetch fails

---

## Files

| File | Purpose |
|------|---------|
| `util/error/AppError.kt` | Error type definitions |
| `util/error/HttpErrorMapper.kt` | HTTP status code mapping |
| `util/error/RetryUtil.kt` | Retry strategy execution |
| `presentation/state/*State.kt` | Error state in UI states |
| `presentation/viewmodel/*ViewModel.kt` | Error handling in ViewModels |

---

## References

- [iOS HIG - Error Handling](https://developer.apple.com/design/human-interface-guidelines/error-handling)
- [Material Design - Errors](https://material.io/design/communication/errors.html)
- [HTTP Status Codes](https://httpstatuses.com/)
- [Retry Pattern](https://docs.microsoft.com/en-us/azure/architecture/patterns/retry)

---

**Created**: 2025-11-17
**Maintained By**: Development Team
