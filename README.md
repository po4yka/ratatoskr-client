# Bite-Size Reader Mobile Client

Native iOS and Android mobile application for [Bite-Size Reader](https://github.com/po4yka/bite-size-reader) - a service that summarizes web articles and YouTube videos using LLM.

## Overview

This is a **Kotlin Multiplatform Mobile (KMP)** app that provides native user experiences on both iOS and Android while sharing ~70-80% of business logic code. The app allows users to:

- Browse and read saved article/video summaries
- Submit new URLs for AI-powered summarization
- Search summaries by topic, content, or tags
- Work offline with automatic sync
- Track reading progress and organize content

### Architecture Philosophy

**KMP + Native UI Approach:**
- **Shared Code (70-80%)**: Business logic, networking, database, state management (Kotlin)
- **Native UI (20-30%)**: Platform-specific UI with SwiftUI (iOS) and Jetpack Compose (Android)
- **Offline-First**: Local SQLite database with background sync to backend API
- **Clean Architecture**: Domain-driven design with clear separation of concerns

## Tech Stack

### Shared Kotlin Multiplatform (commonMain)

| Category | Technology | Purpose |
|----------|-----------|---------|
| **Navigation** | [Decompose](https://github.com/arkivanov/Decompose) | Lifecycle-aware navigation and state preservation |
| **Networking** | [Ktor Client 3.0](https://ktor.io/docs/client.html) | HTTP client with async/await support |
| **Data Layer** | [Store 5](https://github.com/MobileNativeFoundation/Store) | Repository pattern with caching and sync |
| **Database** | [SQLDelight 2.0](https://cashapp.github.io/sqldelight/) | Type-safe SQL with coroutines support |
| **Serialization** | [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) | JSON parsing and data classes |
| **DI** | [Koin 3.5+](https://insert-koin.io/) | Dependency injection |
| **Coroutines** | [kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines) | Async/await and Flow streams |
| **Date/Time** | [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) | ISO 8601 parsing and timezone handling |
| **Logging** | [Kermit](https://github.com/touchlab/Kermit) | Multiplatform logging |

### iOS (SwiftUI)

- **SwiftUI** - Modern declarative UI framework
- **Combine** - Reactive bindings to shared ViewModels
- **SKIE** - Better Swift/Kotlin interop (suspend → async/await, Flow → AsyncSequence)
- **Keychain** - Secure JWT token storage
- **Share Extension** - Submit URLs from Safari/other apps
- **WidgetKit** - Home screen widget for recent summaries

### Android (Jetpack Compose)

- **Jetpack Compose** - Modern declarative UI (100% Compose)
- **Material 3** - Material Design components
- **Koin Android** - Activity/Composable injection
- **EncryptedSharedPreferences** - Secure JWT storage
- **WorkManager** - Background sync jobs
- **App Widgets** - Home screen widget

## Project Structure

```
bite-size-reader-client/
├── shared/                          # KMP shared code (~70-80%)
│   ├── src/
│   │   ├── commonMain/kotlin/       # Shared Kotlin code
│   │   │   ├── data/
│   │   │   │   ├── local/          # SQLDelight database
│   │   │   │   ├── remote/         # Ktor API clients
│   │   │   │   ├── repository/     # Store-based repositories
│   │   │   │   └── mappers/        # DTO ↔ Domain mappers
│   │   │   ├── domain/
│   │   │   │   ├── model/          # Domain entities
│   │   │   │   ├── repository/     # Repository interfaces
│   │   │   │   └── usecase/        # Business logic use cases
│   │   │   ├── presentation/
│   │   │   │   ├── navigation/     # Decompose navigation
│   │   │   │   └── viewmodel/      # Shared ViewModels (MVI)
│   │   │   ├── di/                 # Koin modules
│   │   │   └── util/               # Extensions, helpers
│   │   ├── androidMain/kotlin/     # Android-specific code
│   │   ├── iosMain/kotlin/         # iOS-specific code
│   │   └── commonTest/kotlin/      # Shared tests
│   └── build.gradle.kts
├── composeApp/                      # Android app (~15-20%)
│   ├── src/androidMain/kotlin/
│   │   ├── ui/
│   │   │   ├── theme/              # Material 3 theme
│   │   │   ├── screens/            # Composable screens
│   │   │   └── components/         # Reusable components
│   │   ├── MainActivity.kt
│   │   └── App.kt
│   └── build.gradle.kts
├── iosApp/                          # iOS app (~15-20%)
│   ├── iosApp/
│   │   ├── Views/                  # SwiftUI views
│   │   ├── ViewModels/             # Swift wrappers for KMP VMs
│   │   ├── Auth/                   # Telegram auth
│   │   └── App.swift
│   ├── ShareExtension/             # Share sheet extension
│   └── WidgetExtension/            # Home screen widget
├── gradle/
│   └── libs.versions.toml          # Version catalog
├── README.md                        # This file
├── TODO.md                          # Implementation checklist
└── ROADMAP.md                       # Development phases
```

## Backend API Integration

This client connects to the [bite-size-reader](https://github.com/po4yka/bite-size-reader) FastAPI backend.

**Base URL**: Configurable via `local.properties` (default development: `http://localhost:8000`)

**API Version**: v1

**Content-Type**: `application/json`

**Authentication**: JWT Bearer tokens in `Authorization` header

### API Endpoints Reference

#### Authentication API

##### POST `/v1/auth/telegram-login`

Authenticate user with Telegram and receive JWT tokens.

**Request Body**:
```json
{
  "id": 123456789,
  "hash": "abc123...",
  "auth_date": 1705234567,
  "username": "johndoe",
  "first_name": "John",
  "last_name": "Doe",
  "photo_url": "https://...",
  "client_id": "android-app-v1.0"
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
// data/remote/dto/AuthRequestDto.kt
@Serializable
data class TelegramLoginRequest(
    @SerialName("id") val telegramUserId: Long,
    @SerialName("hash") val authHash: String,
    @SerialName("auth_date") val authDate: Long,
    val username: String? = null,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("client_id") val clientId: String
)

@Serializable
data class AuthResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int,
    val user: UserDto
)

// data/remote/AuthApi.kt
interface AuthApi {
    suspend fun loginWithTelegram(request: TelegramLoginRequest): ApiResponse<AuthResponse>
}

class AuthApiImpl(private val client: HttpClient) : AuthApi {
    override suspend fun loginWithTelegram(request: TelegramLoginRequest): ApiResponse<AuthResponse> {
        return client.post("/v1/auth/telegram-login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
```

##### POST `/v1/auth/refresh`

Refresh expired access token using refresh token.

**Request Body**:
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

**Implementation**:
```kotlin
suspend fun refreshToken(refreshToken: String): ApiResponse<TokenRefreshResponse> {
    return client.post("/v1/auth/refresh") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("refresh_token" to refreshToken))
    }.body()
}
```

##### GET `/v1/auth/me`

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

#### Summaries API

##### GET `/v1/summaries`

List summaries with pagination and filters.

**Headers**: `Authorization: Bearer <access_token>`

**Query Parameters**:
- `limit` (int, default: 20): Number of results per page
- `offset` (int, default: 0): Pagination offset
- `is_read` (bool, optional): Filter by read status
- `lang` (string, optional): Filter by language (en, ru)
- `from_date` (string, optional): ISO 8601 date (e.g., "2025-01-01T00:00:00Z")
- `to_date` (string, optional): ISO 8601 date
- `sort_by` (string, optional): Sort field (created_at, reading_time)
- `sort_order` (string, optional): asc or desc (default: desc)

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
// data/remote/dto/SummaryDto.kt
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
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class PaginationInfo(
    val total: Int,
    val limit: Int,
    val offset: Int,
    @SerialName("has_more") val hasMore: Boolean
)

@Serializable
data class SummaryListResponse(
    val summaries: List<SummaryCompactDto>,
    val pagination: PaginationInfo
)

// data/remote/SummariesApi.kt
interface SummariesApi {
    suspend fun getSummaries(
        limit: Int = 20,
        offset: Int = 0,
        isRead: Boolean? = null,
        lang: String? = null,
        fromDate: String? = null,
        toDate: String? = null,
        sortBy: String? = null,
        sortOrder: String? = null
    ): ApiResponse<SummaryListResponse>
}

class SummariesApiImpl(private val client: HttpClient) : SummariesApi {
    override suspend fun getSummaries(
        limit: Int,
        offset: Int,
        isRead: Boolean?,
        lang: String?,
        fromDate: String?,
        toDate: String?,
        sortBy: String?,
        sortOrder: String?
    ): ApiResponse<SummaryListResponse> {
        return client.get("/v1/summaries") {
            parameter("limit", limit)
            parameter("offset", offset)
            isRead?.let { parameter("is_read", it) }
            lang?.let { parameter("lang", it) }
            fromDate?.let { parameter("from_date", it) }
            toDate?.let { parameter("to_date", it) }
            sortBy?.let { parameter("sort_by", it) }
            sortOrder?.let { parameter("sort_order", it) }
        }.body()
    }
}
```

##### GET `/v1/summaries/{id}`

Get full summary details by ID.

**Headers**: `Authorization: Bearer <access_token>`

**Path Parameters**: `id` (int) - Summary ID

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

**Implementation**:
```kotlin
@Serializable
data class SummaryDetailDto(
    val id: Int,
    @SerialName("request_id") val requestId: Int,
    val title: String,
    val url: String,
    @SerialName("summary_250") val summary250: String,
    @SerialName("summary_1000") val summary1000: String,
    val tldr: String,
    @SerialName("key_ideas") val keyIdeas: List<String>,
    @SerialName("topic_tags") val topicTags: List<String>,
    val entities: EntitiesDto,
    @SerialName("estimated_reading_time_min") val readingTimeMin: Int,
    @SerialName("key_stats") val keyStats: List<KeyStatDto>,
    @SerialName("answered_questions") val answeredQuestions: List<String>,
    val readability: ReadabilityDto,
    @SerialName("seo_keywords") val seoKeywords: List<String>,
    @SerialName("is_read") val isRead: Boolean,
    val lang: String,
    @SerialName("created_at") val createdAt: String
)

suspend fun getSummaryById(id: Int): ApiResponse<SummaryDetailDto> {
    return client.get("/v1/summaries/$id").body()
}
```

##### PATCH `/v1/summaries/{id}`

Update summary metadata (mark as read/unread).

**Headers**: `Authorization: Bearer <access_token>`

**Path Parameters**: `id` (int) - Summary ID

**Request Body**:
```json
{
  "is_read": true
}
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

**Implementation**:
```kotlin
suspend fun updateSummary(id: Int, isRead: Boolean): ApiResponse<SummaryUpdateResponse> {
    return client.patch("/v1/summaries/$id") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("is_read" to isRead))
    }.body()
}
```

#### Requests API

##### POST `/v1/requests`

Submit new URL for summarization.

**Headers**: `Authorization: Bearer <access_token>`

**Request Body**:
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
    @SerialName("lang_preference") val langPreference: String = "auto"
)

@Serializable
data class RequestResponse(
    @SerialName("request_id") val requestId: Int,
    val status: String,
    val stage: String?,
    val progress: Int,
    @SerialName("created_at") val createdAt: String
)

suspend fun submitURL(url: String, langPreference: String = "auto"): ApiResponse<RequestResponse> {
    return client.post("/v1/requests") {
        contentType(ContentType.Application.Json)
        setBody(SubmitURLRequest(inputUrl = url, langPreference = langPreference))
    }.body()
}
```

##### GET `/v1/requests/{id}/status`

Poll request processing status.

**Headers**: `Authorization: Bearer <access_token>`

**Path Parameters**: `id` (int) - Request ID

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

**Status Values**: `pending`, `processing`, `completed`, `error`

**Stage Values**: `content_extraction`, `llm_summarization`, `validation`, `done`

**Progress**: 0-100 (percentage)

**Implementation**:
```kotlin
@Serializable
data class RequestStatusDto(
    @SerialName("request_id") val requestId: Int,
    val status: String,
    val stage: String?,
    val progress: Int,
    @SerialName("estimated_seconds_remaining") val estimatedSecondsRemaining: Int?,
    @SerialName("error_message") val errorMessage: String?,
    @SerialName("can_retry") val canRetry: Boolean,
    @SerialName("summary_id") val summaryId: Int?
)

suspend fun getRequestStatus(requestId: Int): ApiResponse<RequestStatusDto> {
    return client.get("/v1/requests/$requestId/status").body()
}
```

##### POST `/v1/requests/{id}/retry`

Retry failed request.

**Headers**: `Authorization: Bearer <access_token>`

**Path Parameters**: `id` (int) - Request ID

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

#### Search API

##### GET `/v1/search`

Full-text search across summaries.

**Headers**: `Authorization: Bearer <access_token>`

**Query Parameters**:
- `q` (string, required): Search query
- `limit` (int, default: 20): Number of results
- `offset` (int, default: 0): Pagination offset

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

**Implementation**:
```kotlin
@Serializable
data class SearchResultDto(
    val id: Int,
    val title: String,
    val url: String,
    val snippet: String,
    @SerialName("relevance_score") val relevanceScore: Double,
    @SerialName("topic_tags") val topicTags: List<String>
)

@Serializable
data class SearchResponse(
    val results: List<SearchResultDto>,
    val total: Int,
    val query: String
)

suspend fun search(query: String, limit: Int = 20, offset: Int = 0): ApiResponse<SearchResponse> {
    return client.get("/v1/search") {
        parameter("q", query)
        parameter("limit", limit)
        parameter("offset", offset)
    }.body()
}
```

#### Sync API

##### GET `/v1/sync/delta`

Get incremental updates since last sync.

**Headers**: `Authorization: Bearer <access_token>`

**Query Parameters**:
- `since` (string, required): ISO 8601 timestamp of last sync

**Response** (200 OK):
```json
{
  "success": true,
  "data": {
    "summaries": [
      {
        "id": 1,
        "action": "update",
        "data": { /* full summary object */ }
      }
    ],
    "deleted_ids": [42, 43],
    "sync_timestamp": "2025-01-14T12:00:00Z"
  }
}
```

**Implementation**:
```kotlin
@Serializable
data class SyncDeltaResponse(
    val summaries: List<SyncChangeDto>,
    @SerialName("deleted_ids") val deletedIds: List<Int>,
    @SerialName("sync_timestamp") val syncTimestamp: String
)

@Serializable
data class SyncChangeDto(
    val id: Int,
    val action: String, // "update" or "delete"
    val data: SummaryDetailDto?
)

suspend fun getDeltaSync(since: String): ApiResponse<SyncDeltaResponse> {
    return client.get("/v1/sync/delta") {
        parameter("since", since)
    }.body()
}
```

### API Client Setup with Ktor

**Complete Ktor HttpClient configuration**:

```kotlin
// data/remote/ApiClient.kt
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(
    private val baseUrl: String,
    private val tokenProvider: TokenProvider,
    private val engine: HttpClientEngine
) {
    val httpClient = HttpClient(engine) {
        // JSON serialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
                prettyPrint = false
            })
        }

        // Authentication with JWT
        install(Auth) {
            bearer {
                loadTokens {
                    val tokens = tokenProvider.getTokens()
                    BearerTokens(
                        accessToken = tokens.accessToken,
                        refreshToken = tokens.refreshToken
                    )
                }

                refreshTokens {
                    val newTokens = tokenProvider.refreshToken()
                    BearerTokens(
                        accessToken = newTokens.accessToken,
                        refreshToken = newTokens.refreshToken
                    )
                }
            }
        }

        // Default request configuration
        defaultRequest {
            url(baseUrl)
            contentType(ContentType.Application.Json)
        }

        // Logging (debug builds only)
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
            filter { request ->
                request.url.host.contains(baseUrl)
            }
        }

        // Timeout configuration
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        // Response validation
        HttpResponseValidator {
            validateResponse { response ->
                when (response.status.value) {
                    in 300..399 -> throw RedirectException(response)
                    in 400..499 -> throw ClientRequestException(response)
                    in 500..599 -> throw ServerResponseException(response)
                }
            }
        }
    }
}

interface TokenProvider {
    suspend fun getTokens(): AuthTokens
    suspend fun refreshToken(): AuthTokens
}

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)
```

### Generic API Response Wrapper

All API responses follow this structure:

```kotlin
// data/remote/dto/ApiResponseDto.kt
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: ErrorDetail? = null,
    val meta: MetaInfo
)

@Serializable
data class ErrorDetail(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null,
    @SerialName("correlation_id") val correlationId: String? = null
)

@Serializable
data class MetaInfo(
    val timestamp: String,
    val version: String? = null
)
```

### Error Handling

**Common Error Codes**:

| Code | HTTP Status | Description |
|------|------------|-------------|
| `invalid_token` | 401 | JWT token expired or invalid |
| `unauthorized` | 401 | User not authenticated |
| `forbidden` | 403 | User not authorized for this resource |
| `not_found` | 404 | Resource not found |
| `validation_error` | 422 | Request validation failed |
| `rate_limit_exceeded` | 429 | Too many requests |
| `server_error` | 500 | Internal server error |

**Implementation**:

```kotlin
// domain/model/ApiError.kt
sealed class ApiError {
    data class NetworkError(val message: String) : ApiError()
    data class ServerError(val code: String, val message: String) : ApiError()
    data class Unauthorized(val message: String) : ApiError()
    data class NotFound(val message: String) : ApiError()
    data class ValidationError(val fields: Map<String, String>) : ApiError()
    data class Unknown(val message: String) : ApiError()
}

// data/remote/ApiErrorHandler.kt
suspend fun <T> safeApiCall(
    apiCall: suspend () -> HttpResponse
): Result<T> {
    return try {
        val response = apiCall()
        val body: ApiResponse<T> = response.body()

        if (body.success && body.data != null) {
            Result.success(body.data)
        } else {
            Result.failure(
                ApiException(body.error?.message ?: "Unknown error")
            )
        }
    } catch (e: RedirectResponseException) {
        Result.failure(ApiError.NetworkError("Redirect: ${e.message}"))
    } catch (e: ClientRequestException) {
        when (e.response.status.value) {
            401 -> Result.failure(ApiError.Unauthorized("Please login again"))
            404 -> Result.failure(ApiError.NotFound("Resource not found"))
            422 -> {
                val errorBody: ApiResponse<Nothing> = e.response.body()
                Result.failure(
                    ApiError.ValidationError(errorBody.error?.details ?: emptyMap())
                )
            }
            else -> Result.failure(ApiError.ServerError(
                code = "client_error",
                message = e.message ?: "Client error"
            ))
        }
    } catch (e: ServerResponseException) {
        Result.failure(ApiError.ServerError(
            code = "server_error",
            message = "Server error: ${e.response.status.value}"
        ))
    } catch (e: Exception) {
        Result.failure(ApiError.Unknown(e.message ?: "Unknown error"))
    }
}
```

### Authentication Flow Implementation

Complete Telegram authentication flow:

```kotlin
// domain/usecase/LoginWithTelegramUseCase.kt
class LoginWithTelegramUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String
    ): Result<User> {
        // 1. Submit Telegram auth data to backend
        val loginResult = authRepository.loginWithTelegram(
            TelegramLoginRequest(
                telegramUserId = telegramUserId,
                authHash = authHash,
                authDate = authDate,
                username = username,
                firstName = firstName,
                lastName = lastName,
                photoUrl = photoUrl,
                clientId = clientId
            )
        )

        // 2. Store tokens securely
        return loginResult.mapCatching { authResponse ->
            authRepository.storeTokens(
                accessToken = authResponse.accessToken,
                refreshToken = authResponse.refreshToken,
                expiresIn = authResponse.expiresIn
            )
            authResponse.user.toDomain()
        }
    }
}
```

See [TODO.md](./TODO.md) for step-by-step implementation tasks.

## Getting Started

### Prerequisites

#### Development Tools

- **Xcode 15+** (for iOS development, macOS only)
- **Android Studio Ladybug+** (2024.2.1 or later)
- **JDK 17+** (for Gradle)
- **CocoaPods** (for iOS dependencies)

#### Backend Service

The mobile client requires the [bite-size-reader](https://github.com/po4yka/bite-size-reader) FastAPI backend service.

**Backend Requirements**:
- **Python 3.13+**
- **Docker** (optional, recommended)
- **Required Environment Variables**:
  - `JWT_SECRET_KEY` - 32+ character secret (generate: `openssl rand -hex 32`)
  - `BOT_TOKEN` - Telegram bot token (for auth verification)
  - `ALLOWED_USER_IDS` - Comma-separated Telegram user IDs
  - `ALLOWED_CLIENT_IDS` - Optional client ID whitelist
  - `ALLOWED_ORIGINS` - CORS allowed origins (for mobile API)
  - `OPENROUTER_API_KEY` - For LLM summarization
  - `FIRECRAWL_API_KEY` - For content extraction

**Quick Backend Setup**:

```bash
# Clone backend repository
cd ..
git clone https://github.com/po4yka/bite-size-reader.git
cd bite-size-reader

# Configure environment
cp .env.example .env
# Edit .env with your API keys

# Start with Docker (recommended)
docker-compose up -d

# Verify backend is running
curl http://localhost:8000/health
# Expected: {"status":"ok"}
```

**Backend API Documentation**: http://localhost:8000/docs

For detailed backend setup, see [docs/DEVELOPMENT.md](./docs/DEVELOPMENT.md#backend-setup).

### Clone Repository

```bash
git clone https://github.com/po4yka/bite-size-reader-client.git
cd bite-size-reader-client
```

### Configuration

Create `local.properties` in project root:

```properties
# Backend API base URL
api.base.url=http://localhost:8000

# Telegram Bot Token (for auth verification)
telegram.bot.token=YOUR_BOT_TOKEN_HERE

# Client ID (identifies this app to backend)
client.id=android-app-v1.0
```

**Note**: Do NOT commit `local.properties` - it's in `.gitignore`.

### Build & Run

#### Android

```bash
# Open in Android Studio
open -a "Android Studio" .

# Or build from command line
./gradlew :composeApp:assembleDebug

# Install on connected device/emulator
./gradlew :composeApp:installDebug
```

#### iOS

```bash
# Install CocoaPods dependencies
cd iosApp
pod install
cd ..

# Open Xcode workspace
open iosApp/iosApp.xcworkspace

# Or build from command line
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
xcodebuild -workspace iosApp/iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator
```

### Running Tests

```bash
# All shared tests
./gradlew :shared:allTests

# Android tests
./gradlew :composeApp:testDebugUnitTest

# iOS tests (requires macOS)
./gradlew :shared:iosSimulatorArm64Test
```

## Development

### Code Style

- **Kotlin**: [Official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html)
- **Swift**: [Swift API Design Guidelines](https://swift.org/documentation/api-design-guidelines/)
- **Formatting**: Use IDE auto-formatting (Cmd+Opt+L / Ctrl+Alt+L)

### Dependency Management

All versions are managed in `gradle/libs.versions.toml`:

```toml
[versions]
kotlin = "2.2.20"
ktor = "3.0.2"
sqldelight = "2.0.2"
decompose = "3.2.0"
store = "5.1.0"
koin = "3.5.6"

[libraries]
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
# ... more dependencies
```

### Adding Dependencies

1. Add version to `[versions]` section in `libs.versions.toml`
2. Add library to `[libraries]` section
3. Reference in `build.gradle.kts`: `implementation(libs.ktor.client.core)`

### Architecture Patterns

#### MVI (Model-View-Intent)

```kotlin
// State
data class SummaryListState(
    val summaries: List<Summary> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Intent/Event
sealed class SummaryListEvent {
    data object LoadSummaries : SummaryListEvent()
    data class MarkAsRead(val id: Int) : SummaryListEvent()
}

// ViewModel
class SummaryListViewModel(
    private val getSummariesUseCase: GetSummariesUseCase
) {
    private val _state = MutableStateFlow(SummaryListState())
    val state: StateFlow<SummaryListState> = _state.asStateFlow()

    fun onEvent(event: SummaryListEvent) {
        when (event) {
            is SummaryListEvent.LoadSummaries -> loadSummaries()
            is SummaryListEvent.MarkAsRead -> markAsRead(event.id)
        }
    }
}
```

#### Repository Pattern (with Store)

```kotlin
class SummaryRepositoryImpl(
    private val store: Store<String, List<Summary>>
) : SummaryRepository {

    override fun getSummaries(): Flow<List<Summary>> =
        store.stream(StoreReadRequest.cached(key = "summaries", refresh = true))
            .map { it.dataOrNull() ?: emptyList() }
}
```

#### Decompose Navigation

```kotlin
interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        class SummaryList(val component: SummaryListComponent) : Child()
        class SummaryDetail(val component: SummaryDetailComponent) : Child()
    }
}
```

## Key Features

### Offline-First Architecture

- **Local SQLite Database**: All summaries cached locally
- **Background Sync**: Automatic delta sync on app launch
- **Optimistic Updates**: Instant UI updates with background sync
- **Conflict Resolution**: Server wins for reads, local changes uploaded

### Telegram Authentication

1. User taps "Login with Telegram"
2. Opens Telegram Login Widget (WebView on iOS, Custom Tab on Android)
3. User authorizes in Telegram app
4. Callback receives auth data
5. App exchanges auth data for JWT tokens
6. Tokens stored securely (Keychain/EncryptedSharedPreferences)

### URL Submission Flow

1. User pastes URL or shares from another app
2. Client validates URL format
3. POST to `/v1/requests` with URL
4. Receive `request_id`
5. Poll `/v1/requests/{id}/status` every 2 seconds
6. Show progress: content_extraction → llm_summarization → validation → done
7. Fetch final summary and display

### Search

- **Local FTS**: SQLite FTS5 for offline search
- **Remote API**: Full-corpus search on backend
- **Merged Results**: Combine local + remote with deduplication
- **Topic Tags**: Filter by hashtags (#technology, #ai, etc.)

## Performance

### Optimizations

- **Lazy Loading**: Pagination (20 items per page)
- **Image Caching**: Coil (Android) / Kingfisher (iOS) for thumbnails
- **Database Indexing**: Indexes on `is_read`, `created_at`, FTS
- **Memory Management**: Weak references, proper lifecycle handling
- **Background Sync**: WorkManager (Android) / Background Tasks (iOS)

### Benchmarks

- **App Launch**: <2 seconds cold start
- **Summary List**: 60 FPS scrolling with 1000+ items
- **Search**: <200ms for local FTS, <500ms for remote
- **Sync**: <5 seconds for 100 summaries delta sync

## Troubleshooting

### Common Issues

**iOS build fails with "Framework not found Shared":**
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

**Android build fails with SQLDelight errors:**
```bash
./gradlew :shared:generateSqlDelightInterface --rerun-tasks
```

**API connection refused:**
- Ensure backend is running: `cd ../bite-size-reader && docker-compose up`
- Check `api.base.url` in `local.properties`
- On Android emulator, use `http://10.0.2.2:8000` instead of `localhost:8000`

**Telegram auth fails:**
- Verify `telegram.bot.token` in `local.properties`
- Check backend logs for HMAC validation errors
- Ensure timestamp is within 15-minute window

### Debug Logging

Enable verbose logging in `local.properties`:

```properties
log.level=DEBUG
```

Or at runtime:

```kotlin
// Set Kermit log level
Logger.setMinSeverity(Severity.Debug)
```

## Contributing

See [TODO.md](./TODO.md) for current implementation tasks and [ROADMAP.md](./ROADMAP.md) for planned features.

### Development Workflow

1. Create feature branch: `git checkout -b feature/summary-filters`
2. Implement changes in `shared/` first (business logic)
3. Add platform-specific UI in `composeApp/` and `iosApp/`
4. Write tests: `./gradlew :shared:allTests`
5. Format code: IDE auto-format
6. Commit with descriptive message
7. Push and create PR

## License

BSD 3-Clause License - see [LICENSE](./LICENSE) file.

Copyright (c) 2025, Nikita Pochaev

## Related Projects

- **Backend Service**: [bite-size-reader](https://github.com/po4yka/bite-size-reader) - FastAPI backend with Telegram bot
- **Decompose**: [arkivanov/Decompose](https://github.com/arkivanov/Decompose) - Navigation library
- **Store**: [MobileNativeFoundation/Store](https://github.com/MobileNativeFoundation/Store) - Repository pattern

## Resources

### Documentation

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Decompose Documentation](https://arkivanov.github.io/Decompose/)
- [Ktor Client Docs](https://ktor.io/docs/client.html)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)
- [Store Documentation](https://mobilenativefoundation.github.io/Store/)
- [Backend API Spec](https://github.com/po4yka/bite-size-reader/blob/main/SPEC.md)

## Changelog

See [CHANGELOG.md](./CHANGELOG.md) for version history.
