# Backend Integration Guide

Complete guide for integrating the Bite-Size Reader mobile client with the FastAPI backend.

## Table of Contents

1. [Backend Overview](#backend-overview)
2. [API Configuration](#api-configuration)
3. [Environment Setup](#environment-setup)
4. [Rate Limiting](#rate-limiting)
5. [Network Configuration](#network-configuration)
6. [Error Handling](#error-handling)
7. [Testing](#testing)

---

## Backend Overview

The mobile client connects to the [bite-size-reader](https://github.com/po4yka/bite-size-reader) FastAPI backend service.

### Backend Features

- **Article Summarization**: Submit URLs for AI-powered summarization
- **YouTube Support**: Summarize YouTube videos with transcript extraction
- **Search**: Full-text search across all summaries
- **Sync**: Full and incremental database synchronization
- **Multi-client Support**: Supports multiple mobile apps via client ID validation

### Backend Requirements

**Minimum Backend Version**: v1.0.0

**Required Backend Environment Variables**:
```bash
# JWT Authentication
JWT_SECRET_KEY=<32+ character secret>

# Mobile API Configuration
ALLOWED_ORIGINS=http://localhost:3000,https://your-app.com
ALLOWED_CLIENT_IDS=android-app-v1.0,ios-app-v1.0  # Optional

# User Authorization
ALLOWED_USER_IDS=123456789,987654321

# Telegram (for authentication)
BOT_TOKEN=<your-bot-token>
```

---

## API Configuration

### Base URLs

Configure the base URL based on environment:

**Production**:
```kotlin
const val PRODUCTION_BASE_URL = "https://api.bite-size-reader.com"
```

**Staging**:
```kotlin
const val STAGING_BASE_URL = "https://staging-api.bite-size-reader.com"
```

**Local Development**:
```kotlin
// Android emulator accessing host machine
const val LOCAL_ANDROID_EMULATOR_URL = "http://10.0.2.2:8000"

// iOS simulator accessing host machine
const val LOCAL_IOS_SIMULATOR_URL = "http://localhost:8000"

// Physical device on same network
const val LOCAL_NETWORK_URL = "http://192.168.1.100:8000"
```

**Implementation**:

```kotlin
// shared/src/commonMain/kotlin/config/ApiConfig.kt
object ApiConfig {
    enum class Environment {
        PRODUCTION,
        STAGING,
        LOCAL
    }

    var currentEnvironment: Environment = Environment.LOCAL

    val baseUrl: String
        get() = when (currentEnvironment) {
            Environment.PRODUCTION -> "https://api.bite-size-reader.com"
            Environment.STAGING -> "https://staging-api.bite-size-reader.com"
            Environment.LOCAL -> getPlatformLocalUrl()
        }

    val apiVersion = "v1"
    val fullBaseUrl: String get() = "$baseUrl/$apiVersion"
}

// Platform-specific implementations
// androidMain:
expect fun getPlatformLocalUrl(): String
actual fun getPlatformLocalUrl(): String = "http://10.0.2.2:8000"

// iosMain:
actual fun getPlatformLocalUrl(): String = "http://localhost:8000"
```

### CORS Configuration

The backend must whitelist your app's origin. Ensure these headers are allowed:

**Required Headers**:
- `Authorization` - JWT bearer token
- `Content-Type` - application/json
- `X-Correlation-ID` - Request tracing (optional)
- `X-Client-Version` - App version for analytics (optional)

**Backend CORS Configuration** (`app/api/main.py`):
```python
app.add_middleware(
    CORSMiddleware,
    allow_origins=["https://your-app.com"],  # Add your origins
    allow_credentials=True,
    allow_methods=["GET", "POST", "PATCH", "DELETE"],
    allow_headers=["Authorization", "Content-Type", "X-Correlation-ID"],
)
```

### API Versioning

All endpoints are prefixed with `/v1`:

```
https://api.bite-size-reader.com/v1/summaries
https://api.bite-size-reader.com/v1/auth/telegram-login
```

**Client Implementation**:
```kotlin
val httpClient = HttpClient {
    defaultRequest {
        url("${ApiConfig.fullBaseUrl}/")  // Includes /v1
    }
}
```

---

## Environment Setup

### Required Backend Services

For local development, you need the backend service running:

**Using Docker Compose** (Recommended):

```yaml
# backend/docker-compose.yml
services:
  api:
    build: .
    ports:
      - "8000:8000"
    env_file: .env
    volumes:
      - ./data:/data
    restart: unless-stopped
```

Start backend:
```bash
cd ../bite-size-reader
docker-compose up -d
```

**Manual Setup**:

```bash
cd ../bite-size-reader

# Create virtual environment
python -m venv .venv
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env with your credentials

# Run API server
uvicorn app.api.main:app --reload --host 0.0.0.0 --port 8000
```

### Client Configuration

**local.properties**:
```properties
# Backend API Configuration
api.base.url=http://10.0.2.2:8000
api.timeout.seconds=30
api.retry.max.attempts=3

# Authentication
telegram.bot.token=YOUR_BOT_TOKEN_HERE
client.id=android-app-v1.0

# Debug
api.logging.enabled=true
api.debug.payloads=false
```

**BuildConfig Generation** (Android):

```kotlin
// composeApp/build.gradle.kts
android {
    defaultConfig {
        val properties = Properties()
        file("../local.properties").inputStream().use { properties.load(it) }

        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${properties.getProperty("api.base.url", "http://10.0.2.2:8000")}\""
        )

        buildConfigField(
            "String",
            "CLIENT_ID",
            "\"${properties.getProperty("client.id", "android-app-v1.0")}\""
        )
    }
}
```

---

## Rate Limiting

The backend implements per-endpoint rate limits to prevent abuse.

### Rate Limits by Endpoint

| Endpoint Pattern | Limit | Window |
|-----------------|-------|--------|
| `/v1/summaries` | 200 requests | 60 seconds |
| `/v1/requests` | 10 requests | 60 seconds |
| `/v1/search` | 50 requests | 60 seconds |
| `/v1/sync/*` | 30 requests | 60 seconds |
| `/v1/auth/*` | 20 requests | 60 seconds |
| Default | 100 requests | 60 seconds |

### Rate Limit Headers

The backend returns rate limit information in response headers:

```
X-RateLimit-Limit: 200
X-RateLimit-Remaining: 195
X-RateLimit-Reset: 1700000000
```

### Client-Side Rate Limit Handling

**Implementation**:

```kotlin
// data/remote/RateLimitHandler.kt
class RateLimitHandler {
    private val limitByEndpoint = mutableMapOf<String, RateLimitInfo>()

    data class RateLimitInfo(
        val limit: Int,
        val remaining: Int,
        val resetTimestamp: Long
    )

    fun updateFromHeaders(endpoint: String, headers: Headers) {
        val limit = headers["X-RateLimit-Limit"]?.toIntOrNull() ?: return
        val remaining = headers["X-RateLimit-Remaining"]?.toIntOrNull() ?: return
        val reset = headers["X-RateLimit-Reset"]?.toLongOrNull() ?: return

        limitByEndpoint[endpoint] = RateLimitInfo(limit, remaining, reset)
    }

    fun shouldThrottle(endpoint: String): Boolean {
        val info = limitByEndpoint[endpoint] ?: return false

        return if (info.remaining <= 5) {
            // Less than 5 requests remaining - throttle
            val now = Clock.System.now().epochSeconds
            now < info.resetTimestamp
        } else {
            false
        }
    }

    fun getRetryAfterSeconds(endpoint: String): Long? {
        val info = limitByEndpoint[endpoint] ?: return null
        val now = Clock.System.now().epochSeconds
        return (info.resetTimestamp - now).coerceAtLeast(0)
    }
}
```

**Usage in Repository**:

```kotlin
class SummaryRepositoryImpl(
    private val api: SummariesApi,
    private val rateLimitHandler: RateLimitHandler
) : SummaryRepository {

    override suspend fun getSummaries(limit: Int, offset: Int): Result<List<Summary>> {
        val endpoint = "/v1/summaries"

        // Check if we should throttle
        if (rateLimitHandler.shouldThrottle(endpoint)) {
            val retryAfter = rateLimitHandler.getRetryAfterSeconds(endpoint)
            return Result.failure(
                RateLimitException("Rate limit exceeded. Retry after $retryAfter seconds")
            )
        }

        return try {
            val response = api.getSummaries(limit, offset)

            // Update rate limit info from response headers
            rateLimitHandler.updateFromHeaders(endpoint, response.headers)

            Result.success(response.data.summaries)
        } catch (e: ClientRequestException) {
            if (e.response.status.value == 429) {
                // Handle 429 Too Many Requests
                val retryAfter = e.response.headers["Retry-After"]?.toLongOrNull() ?: 60
                delay(retryAfter * 1000)
                getSummaries(limit, offset) // Retry once
            } else {
                Result.failure(e)
            }
        }
    }
}
```

### Exponential Backoff

For retry logic on rate limit errors:

```kotlin
suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    maxDelayMs: Long = 10000,
    factor: Double = 2.0,
    block: suspend () -> T
): Result<T> {
    var currentDelay = initialDelayMs
    repeat(maxRetries) { attempt ->
        try {
            return Result.success(block())
        } catch (e: Exception) {
            if (attempt == maxRetries - 1) {
                return Result.failure(e)
            }

            if (e is RateLimitException ||
                (e is ClientRequestException && e.response.status.value == 429)) {
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMs)
            } else {
                return Result.failure(e)
            }
        }
    }
    return Result.failure(Exception("Max retries exceeded"))
}
```

---

## Network Configuration

### Timeouts

Configure appropriate timeouts for different operation types:

```kotlin
// data/remote/ApiClient.kt
val httpClient = HttpClient(engine) {
    install(HttpTimeout) {
        // Request timeout (entire request/response cycle)
        requestTimeoutMillis = 30_000  // 30 seconds

        // Connect timeout (initial connection)
        connectTimeoutMillis = 10_000  // 10 seconds

        // Socket timeout (between TCP packets)
        socketTimeoutMillis = 30_000   // 30 seconds
    }
}
```

**Recommended Timeouts by Operation**:

| Operation | Recommended Timeout |
|-----------|-------------------|
| Authentication | 10 seconds |
| Get summaries | 15 seconds |
| Submit URL | 30 seconds |
| Poll status | 10 seconds |
| Search | 20 seconds |
| Full sync | 120 seconds |
| Delta sync | 30 seconds |

**Implementation**:

```kotlin
sealed class ApiOperation(val timeoutMs: Long) {
    object Auth : ApiOperation(10_000)
    object GetSummaries : ApiOperation(15_000)
    object SubmitURL : ApiOperation(30_000)
    object PollStatus : ApiOperation(10_000)
    object Search : ApiOperation(20_000)
    object FullSync : ApiOperation(120_000)
    object DeltaSync : ApiOperation(30_000)
}

suspend fun <T> executeWithTimeout(
    operation: ApiOperation,
    block: suspend () -> T
): Result<T> {
    return withTimeout(operation.timeoutMs) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### Connection Pooling

Optimize network performance with connection pooling:

```kotlin
// Android (OkHttp engine)
val httpClient = HttpClient(OkHttp) {
    engine {
        config {
            connectionPool(ConnectionPool(
                maxIdleConnections = 5,
                keepAliveDuration = 5,
                timeUnit = TimeUnit.MINUTES
            ))

            retryOnConnectionFailure(true)
        }
    }
}

// iOS (Darwin engine)
val httpClient = HttpClient(Darwin) {
    engine {
        configureRequest {
            setAllowsCellularAccess(true)
            setAllowsExpensiveNetworkAccess(true)
        }
    }
}
```

### Network Reachability

Check network status before making requests:

```kotlin
// Platform-specific implementations
expect class NetworkMonitor {
    fun isNetworkAvailable(): Boolean
    fun observeNetworkStatus(): Flow<NetworkStatus>
}

enum class NetworkStatus {
    CONNECTED,
    DISCONNECTED,
    CONNECTING
}

// Usage in repository
override suspend fun getSummaries(): Result<List<Summary>> {
    if (!networkMonitor.isNetworkAvailable()) {
        // Return cached data only
        return Result.success(localDb.getCachedSummaries())
    }

    // Proceed with network request
    // ...
}
```

---

## Error Handling

### Error Code Reference

The backend returns standardized error codes in the response:

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable message",
    "details": { "field": "error detail" },
    "correlation_id": "req-abc123"
  }
}
```

### Standard Error Codes

| Code | HTTP Status | Description | Client Action |
|------|------------|-------------|---------------|
| `invalid_token` | 401 | JWT token expired or malformed | Refresh token or re-authenticate |
| `unauthorized` | 401 | User not authenticated | Show login screen |
| `forbidden` | 403 | User not in allowlist | Show error message |
| `not_found` | 404 | Resource not found | Show not found UI |
| `validation_error` | 422 | Request validation failed | Show field-specific errors |
| `rate_limit_exceeded` | 429 | Too many requests | Wait and retry with backoff |
| `server_error` | 500 | Internal server error | Retry or show error |
| `service_unavailable` | 503 | Backend temporarily down | Retry later |

### Error Handling Implementation

```kotlin
// domain/model/ApiError.kt
sealed class ApiError(val message: String) : Exception(message) {
    data class NetworkError(
        override val message: String = "Network connection failed"
    ) : ApiError(message)

    data class Unauthorized(
        override val message: String = "Please login again"
    ) : ApiError(message)

    data class Forbidden(
        override val message: String = "Access denied"
    ) : ApiError(message)

    data class NotFound(
        override val message: String = "Resource not found"
    ) : ApiError(message)

    data class ValidationError(
        val fields: Map<String, String>,
        override val message: String = "Validation failed"
    ) : ApiError(message)

    data class RateLimitExceeded(
        val retryAfterSeconds: Long,
        override val message: String = "Too many requests"
    ) : ApiError(message)

    data class ServerError(
        val correlationId: String?,
        override val message: String = "Server error occurred"
    ) : ApiError(message)

    data class Unknown(
        override val message: String = "Unknown error"
    ) : ApiError(message)
}

// data/remote/ApiErrorHandler.kt
object ApiErrorHandler {
    suspend fun <T> handleResponse(
        httpResponse: HttpResponse
    ): Result<T> {
        return try {
            when (httpResponse.status.value) {
                in 200..299 -> {
                    val body: ApiResponse<T> = httpResponse.body()
                    if (body.success && body.data != null) {
                        Result.success(body.data)
                    } else {
                        Result.failure(parseError(body.error))
                    }
                }
                401 -> Result.failure(ApiError.Unauthorized())
                403 -> Result.failure(ApiError.Forbidden())
                404 -> Result.failure(ApiError.NotFound())
                422 -> {
                    val errorBody: ApiResponse<Nothing> = httpResponse.body()
                    Result.failure(
                        ApiError.ValidationError(
                            fields = errorBody.error?.details ?: emptyMap()
                        )
                    )
                }
                429 -> {
                    val retryAfter = httpResponse.headers["Retry-After"]
                        ?.toLongOrNull() ?: 60
                    Result.failure(
                        ApiError.RateLimitExceeded(retryAfter)
                    )
                }
                in 500..599 -> {
                    val errorBody: ApiResponse<Nothing> = httpResponse.body()
                    Result.failure(
                        ApiError.ServerError(
                            correlationId = errorBody.error?.correlationId
                        )
                    )
                }
                else -> Result.failure(ApiError.Unknown())
            }
        } catch (e: Exception) {
            Result.failure(ApiError.NetworkError(e.message ?: "Network error"))
        }
    }

    private fun parseError(error: ErrorDetail?): ApiError {
        return when (error?.code) {
            "invalid_token" -> ApiError.Unauthorized("Token expired")
            "unauthorized" -> ApiError.Unauthorized()
            "forbidden" -> ApiError.Forbidden()
            "not_found" -> ApiError.NotFound()
            "validation_error" -> ApiError.ValidationError(error.details ?: emptyMap())
            "rate_limit_exceeded" -> ApiError.RateLimitExceeded(60)
            else -> ApiError.Unknown(error?.message ?: "Unknown error")
        }
    }
}
```

### Correlation IDs

Use correlation IDs for debugging and support:

```kotlin
// Extract correlation ID from error
when (val result = repository.getSummaries()) {
    is Result.Failure -> {
        val error = result.error
        if (error is ApiError.ServerError && error.correlationId != null) {
            // Log correlation ID for support
            Logger.e("Server error", "Correlation ID: ${error.correlationId}")

            // Show to user for support requests
            showError("Server error. Reference: ${error.correlationId}")
        }
    }
}
```

---

## Testing

### Testing Against Local Backend

**1. Start Backend Locally**:

```bash
cd ../bite-size-reader
docker-compose up -d
```

Verify backend is running:
```bash
curl http://localhost:8000/health
# Expected: {"status": "ok"}
```

**2. Configure Mobile Client**:

Update `local.properties`:
```properties
api.base.url=http://10.0.2.2:8000  # Android emulator
# OR
api.base.url=http://localhost:8000  # iOS simulator
```

**3. Seed Test Data** (Optional):

```bash
# In backend directory
python -m app.cli.summary --url "https://example.com/test-article"
```

### Integration Tests

Test API integration with mock server:

```kotlin
// shared/src/commonTest/kotlin/ApiIntegrationTest.kt
class ApiIntegrationTest {
    private lateinit var mockEngine: MockEngine
    private lateinit var apiClient: HttpClient

    @BeforeTest
    fun setup() {
        mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/v1/summaries" -> respond(
                    content = """
                        {
                          "success": true,
                          "data": {
                            "summaries": [],
                            "pagination": {
                              "total": 0,
                              "limit": 20,
                              "offset": 0,
                              "has_more": false
                            }
                          },
                          "meta": {"timestamp": "2025-01-01T00:00:00Z"}
                        }
                    """.trimIndent(),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        apiClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json()
            }
        }
    }

    @Test
    fun testGetSummaries() = runTest {
        val api = SummariesApiImpl(apiClient)
        val result = api.getSummaries()

        assertTrue(result.success)
        assertEquals(0, result.data.summaries.size)
    }
}
```

### Manual Testing Checklist

- [ ] Authentication with Telegram credentials
- [ ] Get summaries list (empty state)
- [ ] Get summaries list (with data)
- [ ] Pagination (load more)
- [ ] Get summary detail
- [ ] Mark summary as read/unread
- [ ] Submit URL for summarization
- [ ] Poll request status
- [ ] Search summaries
- [ ] Full sync
- [ ] Delta sync
- [ ] Token refresh
- [ ] Rate limit handling (submit 11 URLs rapidly)
- [ ] Offline mode (airplane mode)
- [ ] Network error recovery

### Debug Logging

Enable verbose API logging:

```kotlin
val httpClient = HttpClient {
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                println("HTTP: $message")
            }
        }
        level = LogLevel.ALL
    }
}
```

View logs:
```bash
# Android
adb logcat | grep "HTTP:"

# iOS
# View in Xcode console
```

---

## Troubleshooting

### Common Issues

**Issue: Connection refused**

**Cause**: Backend not running or wrong URL

**Solution**:
1. Verify backend is running: `curl http://localhost:8000/health`
2. Check `local.properties` has correct URL
3. For Android emulator, use `10.0.2.2` instead of `localhost`

---

**Issue: 401 Unauthorized**

**Cause**: Invalid or expired token

**Solution**:
1. Clear stored tokens
2. Re-authenticate with Telegram
3. Check `ALLOWED_USER_IDS` in backend `.env`

---

**Issue: 403 Forbidden**

**Cause**: User not in backend allowlist

**Solution**:
1. Add your Telegram user ID to backend `ALLOWED_USER_IDS`
2. Restart backend

---

**Issue: 429 Rate Limit**

**Cause**: Too many requests in short time

**Solution**:
1. Implement exponential backoff
2. Respect `Retry-After` header
3. Add client-side request throttling

---

**Issue: SSL Certificate Error (iOS)**

**Cause**: Self-signed certificate in development

**Solution**:
```swift
// iOS only - for development
class TrustAllCertificatesDelegate: NSObject, URLSessionDelegate {
    func urlSession(
        _ session: URLSession,
        didReceive challenge: URLAuthenticationChallenge,
        completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
    ) {
        completionHandler(.useCredential, URLCredential(trust: challenge.protectionSpace.serverTrust!))
    }
}
```

**Warning**: Never use in production!

---

## Next Steps

- Review [SYNC_STRATEGY.md](./SYNC_STRATEGY.md) for sync implementation
- Review [SECURITY.md](./SECURITY.md) for security best practices
- Review [DEVELOPMENT.md](./DEVELOPMENT.md) for local development setup

---

**Last Updated**: 2025-11-16
