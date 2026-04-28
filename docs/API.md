# Backend API Reference

**Last Updated**: 2026-04-28
**Base URL**: configured in `local.properties` (default `https://api.ratatoskr.po4yka.com`)
**API version**: v1
**Content-Type**: `application/json`
**Authentication**: JWT Bearer tokens in the `Authorization` header

The canonical OpenAPI spec lives in the backend repository at
[`po4yka/ratatoskr/docs/openapi/`](https://github.com/po4yka/ratatoskr/tree/main/docs/openapi).
This document captures the endpoints the mobile client actually
consumes plus the Kotlin transport-layer wiring that calls them.

For end-to-end authentication setup (BotFather, deep-link scheme,
WebView actuals), see [`docs/AUTHENTICATION.md`](AUTHENTICATION.md).

---

## Endpoints

### Authentication

#### POST `/v1/auth/telegram-login`

Authenticate user with Telegram and receive JWT tokens.

**Request body**:

```json
{
  "id": 123456789,
  "hash": "abc123...",
  "auth_date": 1705234567,
  "username": "johndoe",
  "first_name": "John",
  "last_name": "Doe",
  "photo_url": "https://...",
  "client_id": "ratatoskr-android-v1.0"
}
```

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIs...",
    "token_type": "Bearer",
    "expires_in": 3600,
    "user": {
      "id": 123456789,
      "username": "johndoe",
      "first_name": "John",
      "last_name": "Doe"
    }
  },
  "meta": {
    "timestamp": "2025-01-14T12:00:00Z",
    "version": "1.0"
  }
}
```

**Implementation**:

```kotlin
// feature/auth/.../data/remote/dto/AuthRequestDto.kt
@Serializable
data class TelegramLoginRequest(
    @SerialName("id") val telegramUserId: Long,
    @SerialName("hash") val authHash: String,
    @SerialName("auth_date") val authDate: Long,
    val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("client_id") val clientId: String,
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    val user: UserDto,
)

interface AuthApi {
    suspend fun loginWithTelegram(request: TelegramLoginRequest): ApiResponse<AuthResponse>
}

class AuthApiImpl(private val client: HttpClient) : AuthApi {
    override suspend fun loginWithTelegram(
        request: TelegramLoginRequest,
    ): ApiResponse<AuthResponse> =
        client.post("/v1/auth/telegram-login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
```

#### POST `/v1/auth/refresh`

Refresh expired access token using the refresh token. Wired into the
Ktor `Auth` plugin in
[`core/data/.../data/remote/ApiClient.kt`](../core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/ApiClient.kt)
— the client invokes this automatically on a 401 response.

**Request body**:

```json
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIs...",
    "token_type": "Bearer",
    "expires_in": 3600
  },
  "meta": {
    "timestamp": "2025-01-14T12:00:00Z"
  }
}
```

**Failure handling**: on `success=false` or HTTP error, `SecureStorage.clearTokens()`
is called to force re-authentication. See
[`docs/AUTHENTICATION.md#token-refresh-mechanism`](AUTHENTICATION.md#token-refresh-mechanism).

#### GET `/v1/auth/me`

Get current authenticated user information.

**Headers**: `Authorization: Bearer <access_token>`

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "id": 123456789,
    "username": "johndoe",
    "first_name": "John",
    "is_owner": true
  }
}
```

---

### Summaries

#### GET `/v1/summaries`

List summaries with pagination and filters.

**Headers**: `Authorization: Bearer <access_token>`

**Query parameters**:

- `limit` (int, default: 20)
- `offset` (int, default: 0)
- `is_read` (bool, optional)
- `lang` (string, optional): `en`, `ru`
- `from_date` (ISO 8601, optional)
- `to_date` (ISO 8601, optional)
- `sort_by` (string, optional): `created_at`, `reading_time`
- `sort_order` (string, optional): `asc`, `desc` (default: `desc`)

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "summaries": [
      {
        "id": 1,
        "request_id": 42,
        "title": "Understanding Kotlin Multiplatform",
        "domain": "example.com",
        "url": "https://example.com/article",
        "tldr": "Kotlin Multiplatform allows sharing code...",
        "summary_250": "Brief summary in 250 chars...",
        "reading_time_min": 5,
        "topic_tags": ["#kotlin", "#mobile", "#tech"],
        "is_read": false,
        "lang": "en",
        "created_at": "2025-01-14T12:00:00Z"
      }
    ],
    "pagination": {
      "total": 150,
      "limit": 20,
      "offset": 0,
      "has_more": true
    }
  }
}
```

**Implementation**:

```kotlin
// feature/summary/.../data/remote/dto/SummaryDto.kt
@Serializable
data class SummaryCompactDto(
    val id: Int,
    @SerialName("request_id") val requestId: Int,
    val title: String,
    val domain: String? = null,
    val url: String,
    val tldr: String,
    @SerialName("summary_250") val summary250: String,
    @SerialName("reading_time_min") val readingTimeMin: Int,
    @SerialName("topic_tags") val topicTags: List<String>,
    @SerialName("is_read") val isRead: Boolean,
    val lang: String,
    @SerialName("created_at") val createdAt: String,
)

@Serializable
data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerialName("has_more") val hasMore: Boolean,
)

@Serializable
data class SummaryListResponse(
    val summaries: List<SummaryCompactDto>,
    val pagination: PaginationInfo,
)

interface SummariesApi {
    suspend fun getSummaries(
        limit: Int = 20,
        offset: Int = 0,
        isRead: Boolean? = null,
        lang: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null,
    ): ApiResponse<SummaryListResponse>
}
```

#### GET `/v1/summaries/{id}`

Get full summary detail.

**Headers**: `Authorization: Bearer <access_token>`

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "id": 1,
    "request_id": 42,
    "title": "Understanding Kotlin Multiplatform",
    "url": "https://example.com/article",
    "summary_250": "Brief summary...",
    "summary_1000": "Extended summary in 1000 chars...",
    "tldr": "Kotlin Multiplatform allows sharing code...",
    "key_ideas": [
      "Share business logic across platforms",
      "Native UI for each platform",
      "Reduce code duplication"
    ],
    "topic_tags": ["#kotlin", "#mobile"],
    "entities": {
      "people": ["John Doe"],
      "organizations": ["JetBrains"],
      "locations": ["Prague"]
    },
    "estimated_reading_time_min": 5,
    "key_stats": [
      {
        "label": "Code sharing",
        "value": 70.0,
        "unit": "%",
        "source_excerpt": "Share up to 70% of code"
      }
    ],
    "answered_questions": ["What is KMP?", "How does it work?"],
    "readability": {
      "method": "Flesch-Kincaid",
      "score": 12.4,
      "level": "College"
    },
    "seo_keywords": ["kotlin", "multiplatform", "mobile"],
    "is_read": false,
    "lang": "en",
    "created_at": "2025-01-14T12:00:00Z"
  }
}
```

**Implementation**: see `SummaryDetailDto` in
`feature/summary/.../data/remote/dto/`. The response is mapped to the
domain `Summary` model before reaching the ViewModel.

#### PATCH `/v1/summaries/{id}`

Update summary metadata (mark read/unread).

**Request body**:

```json
{ "is_read": true }
```

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "id": 1,
    "is_read": true,
    "updated_at": "2025-01-14T12:00:00Z"
  }
}
```

---

### Requests (URL submission)

#### POST `/v1/requests`

Submit a new URL for summarization.

**Request body**:

```json
{
  "type": "url",
  "input_url": "https://example.com/article",
  "lang_preference": "auto"
}
```

**Response** (201 Created):

```json
{
  "success": true,
  "data": {
    "request_id": 42,
    "status": "pending",
    "stage": "content_extraction",
    "progress": 0,
    "created_at": "2025-01-14T12:00:00Z"
  }
}
```

**Implementation**:

```kotlin
@Serializable
data class SubmitURLRequest(
    val type: String = "url",
    @SerialName("input_url") val inputUrl: String,
    @SerialName("lang_preference") val langPreference: String = "auto",
)

@Serializable
data class RequestResponse(
    @SerialName("request_id") val requestId: Int,
    val status: String,
    val stage: String?,
    val progress: Int,
    @SerialName("created_at") val createdAt: String,
)
```

#### GET `/v1/requests/{id}/status`

Poll request processing status.

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "request_id": 42,
    "status": "processing",
    "stage": "llm_summarization",
    "progress": 50,
    "estimated_seconds_remaining": 15,
    "error_message": null,
    "can_retry": false,
    "summary_id": null
  }
}
```

| Field | Values |
|---|---|
| `status` | `pending`, `processing`, `completed`, `error` |
| `stage` | `content_extraction`, `llm_summarization`, `validation`, `done` |
| `progress` | 0–100 (percentage) |

The client polls this endpoint every ~2 seconds until `status` is
`completed` or `error`.

#### POST `/v1/requests/{id}/retry`

Retry a failed request.

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "request_id": 42,
    "status": "pending"
  }
}
```

---

### Search

#### GET `/v1/search`

Full-text search across summaries (server-side, full corpus).

**Query parameters**:

- `q` (string, required)
- `limit` (int, default: 20)
- `offset` (int, default: 0)

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "results": [
      {
        "id": 1,
        "title": "Understanding Kotlin Multiplatform",
        "url": "https://example.com/article",
        "snippet": "...Kotlin Multiplatform allows...",
        "relevance_score": 0.95,
        "topic_tags": ["#kotlin", "#mobile"]
      }
    ],
    "total": 5,
    "query": "kotlin multiplatform"
  }
}
```

The mobile client merges these results with local SQLite FTS5 hits and
deduplicates before display.

---

### Sync

#### GET `/v1/sync/delta`

Get incremental updates since last sync. Used by `feature/sync/.../SyncRepositoryImpl`.

**Query parameters**:

- `since` (ISO 8601, required): timestamp of the last successful sync.

**Response** (200 OK):

```json
{
  "success": true,
  "data": {
    "summaries": [
      {
        "id": 1,
        "action": "update",
        "data": { "...full summary object..." }
      }
    ],
    "deleted_ids": [42, 43],
    "sync_timestamp": "2025-01-14T12:00:00Z"
  }
}
```

`action` is either `update` or `delete`. The `data` field is null for
delete actions; the `id` is also reflected in `deleted_ids`.

---

## API Client Setup with Ktor

Configured once in
[`core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/ApiClient.kt`](../core/data/src/commonMain/kotlin/com/po4yka/ratatoskr/data/remote/ApiClient.kt):

```kotlin
class ApiClient(
    private val baseUrl: String,
    private val tokenProvider: TokenProvider,
    private val engine: HttpClientEngine,
) {
    val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Auth) {
            bearer {
                loadTokens {
                    val tokens = tokenProvider.getTokens()
                    BearerTokens(tokens.accessToken, tokens.refreshToken)
                }
                refreshTokens {
                    val newTokens = tokenProvider.refreshToken()
                    BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                }
            }
        }

        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }
    }
}
```

The platform engine is supplied via DI:

| Platform | Engine |
|---|---|
| Android | OkHttp |
| iOS | Darwin |
| Desktop | OkHttp |

---

## Generic API Response Wrapper

Every endpoint returns the same envelope:

```kotlin
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val meta: MetaInfo,
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
    @SerialName("correlation_id") val correlationId: String? = null,
)

@Serializable
data class MetaInfo(
    val timestamp: String,
    val version: String? = null,
)
```

## Error handling

### Common error codes

| Code | HTTP | Description |
|---|---|---|
| `invalid_token` | 401 | JWT token expired or invalid |
| `unauthorized` | 401 | User not authenticated |
| `forbidden` | 403 | User not authorized for this resource |
| `not_found` | 404 | Resource not found |
| `validation_error` | 422 | Request validation failed |
| `rate_limit_exceeded` | 429 | Too many requests |
| `server_error` | 500 | Internal server error |

### Mapping HTTP failures to domain errors

The Ktor `HttpResponseValidator` raises typed exceptions on
non-2xx responses. The transport layer maps those to the
`AppError` sealed class defined in `core/common/.../util/error/`. See
[`docs/ERROR_HANDLING.md`](ERROR_HANDLING.md) for the user-facing
mapping (retry policy, error banners, etc.).

```kotlin
suspend fun <T> safeApiCall(
    apiCall: suspend () -> HttpResponse,
): Result<T> = try {
    val response = apiCall()
    val body: ApiResponse<T> = response.body()
    if (body.success && body.data != null) {
        Result.success(body.data)
    } else {
        Result.failure(ApiException(body.error?.message ?: "Unknown error"))
    }
} catch (e: ClientRequestException) {
    when (e.response.status.value) {
        401 -> Result.failure(AppError.Unauthorized("Please login again"))
        404 -> Result.failure(AppError.NotFound("Resource not found"))
        422 -> {
            val errorBody: ApiResponse<Nothing> = e.response.body()
            Result.failure(AppError.ValidationError(errorBody.error?.details ?: emptyMap()))
        }
        else -> Result.failure(AppError.ServerError("client_error", e.message ?: "Client error"))
    }
} catch (e: ServerResponseException) {
    Result.failure(AppError.ServerError("server_error", "HTTP ${e.response.status.value}"))
} catch (e: Exception) {
    Result.failure(AppError.Unknown(e.message ?: "Unknown error"))
}
```

---

## Authentication flow (use-case level)

```kotlin
// feature/auth/.../domain/usecase/LoginWithTelegramUseCase.kt
class LoginWithTelegramUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(authData: TelegramAuthData): Result<User> {
        val loginResult = authRepository.loginWithTelegram(authData.toRequest())
        return loginResult.mapCatching { authResponse ->
            authRepository.storeTokens(
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken,
                expiresIn = authResponse.expiresIn,
            )
            authResponse.user.toDomain()
        }
    }
}
```

The full Telegram-side flow (BotFather setup, Compose
`TelegramAuthScreen`, platform `WebView` actuals) lives in
[`docs/AUTHENTICATION.md`](AUTHENTICATION.md).
