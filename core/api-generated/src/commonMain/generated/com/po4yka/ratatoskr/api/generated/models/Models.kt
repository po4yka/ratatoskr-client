/* 
 * NOTE: This file is auto generated. Do not edit the file manually!
 * 
 * Ratatoskr Mobile API
 * RESTful API for Android/iOS mobile clients
 * Version 1.0.0
 * 
 * Generated Wed, 13 May 2026 10:50:14 +0400
 * OpenAPI KMP Gen (version 1.3.0) by kroegerama
 */
@file:Suppress("ArrayInDataClass", "RedundantVisibilityModifier", "unused", "ConstPropertyName")

package com.po4yka.ratatoskr.api.generated.models

import androidx.compose.runtime.Immutable
import kotlin.Boolean
import kotlin.Double
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.emptyList
import kotlin.collections.emptyMap
import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonElement

/**
 * Response metadata for observability and pagination.
 */
@Serializable
@Immutable
public data class Meta(
  /**
   * Request correlation ID for debugging (always present, may be empty string).
   */
  @SerialName("correlation_id")
  public val correlationId: String,
  /**
   * ISO 8601 response timestamp.
   */
  @SerialName("timestamp")
  public val timestamp: Instant,
  /**
   * App / build version (changes every deploy). Sourced from APP_VERSION env var.
   */
  @SerialName("version")
  public val version: String,
  /**
   * API contract semver. Distinct from `version` — bumps only on breaking contract
   * changes (response shape, request shape, routing surface). Mobile / CLI clients
   * should pin against this value and refuse to upgrade major versions blindly.
   */
  @SerialName("api_version")
  public val apiVersion: String,
  /**
   * Build identifier (if available).
   */
  @SerialName("build")
  public val build: String? = null,
  @SerialName("pagination")
  public val pagination: Pagination? = null,
  /**
   * Debug information (only in development mode).
   */
  @SerialName("debug")
  public val debug: JsonElement? = null,
)

/**
 * Pagination metadata for list responses.
 */
@Serializable
@Immutable
public data class Pagination(
  /**
   * Total number of items matching the query.
   */
  @SerialName("total")
  public val total: Long,
  /**
   * Maximum items per page.
   */
  @SerialName("limit")
  public val limit: Long,
  /**
   * Current offset from start.
   */
  @SerialName("offset")
  public val offset: Long,
  /**
   * Whether more items are available.
   */
  @SerialName("hasMore")
  public val hasMore: Boolean,
  /**
   * Cursor for next page (if using cursor-based pagination).
   */
  @SerialName("nextCursor")
  public val nextCursor: Long? = null,
)

@Serializable
@Immutable
public data class BaseSuccessResponse(
  @SerialName("success")
  public val success: Boolean,
  @SerialName("meta")
  public val meta: Meta,
)

/**
 * Structured error details for programmatic handling.
 */
@Serializable
@Immutable
public data class ErrorObject(
  /**
   * Machine-readable error code for programmatic handling.
   */
  @SerialName("code")
  public val code: Code,
  /**
   * Error category for client-side handling logic.
   */
  @SerialName("errorType")
  public val errorType: ErrorType,
  /**
   * Human-readable error message.
   */
  @SerialName("message")
  public val message: String,
  /**
   * Whether the client should retry the request.
   */
  @SerialName("retryable")
  public val retryable: Boolean,
  /**
   * Additional error context (field errors, resource IDs, etc.).
   */
  @SerialName("details")
  public val details: JsonElement? = null,
  /**
   * Request correlation ID for debugging (always present).
   */
  @SerialName("correlation_id")
  public val correlationId: String,
  /**
   * Seconds to wait before retrying (for rate limit errors).
   */
  @SerialName("retry_after")
  public val retryAfter: Long? = null,
) {
  /**
   * Machine-readable error code for programmatic handling.
   */
  @Serializable
  @Immutable
  public enum class Code {
    @SerialName("VALIDATION_ERROR")
    VALIDATION_ERROR,
    @SerialName("UNAUTHORIZED")
    UNAUTHORIZED,
    @SerialName("FORBIDDEN")
    FORBIDDEN,
    @SerialName("NOT_FOUND")
    NOT_FOUND,
    @SerialName("CONFLICT")
    CONFLICT,
    @SerialName("RATE_LIMIT_EXCEEDED")
    RATE_LIMIT_EXCEEDED,
    @SerialName("SESSION_EXPIRED")
    SESSION_EXPIRED,
    @SerialName("TOKEN_EXPIRED")
    TOKEN_EXPIRED,
    @SerialName("TOKEN_INVALID")
    TOKEN_INVALID,
    @SerialName("TOKEN_REVOKED")
    TOKEN_REVOKED,
    @SerialName("TOKEN_WRONG_TYPE")
    TOKEN_WRONG_TYPE,
    @SerialName("REFRESH_RATE_LIMITED")
    REFRESH_RATE_LIMITED,
    @SerialName("SYNC_SESSION_EXPIRED")
    SYNC_SESSION_EXPIRED,
    @SerialName("SYNC_SESSION_NOT_FOUND")
    SYNC_SESSION_NOT_FOUND,
    @SerialName("SYNC_SESSION_FORBIDDEN")
    SYNC_SESSION_FORBIDDEN,
    @SerialName("SYNC_NO_CHANGES")
    SYNC_NO_CHANGES,
    @SerialName("SYNC_CONFLICT")
    SYNC_CONFLICT,
    @SerialName("SYNC_INVALID_ENTITY")
    SYNC_INVALID_ENTITY,
    @SerialName("SYNC_ENTITY_NOT_FOUND")
    SYNC_ENTITY_NOT_FOUND,
    @SerialName("INTERNAL_ERROR")
    INTERNAL_ERROR,
    @SerialName("DATABASE_ERROR")
    DATABASE_ERROR,
    @SerialName("EXTERNAL_API_ERROR")
    EXTERNAL_API_ERROR,
    @SerialName("PROCESSING_ERROR")
    PROCESSING_ERROR,
    @SerialName("AUTH_SERVICE_UNAVAILABLE")
    AUTH_SERVICE_UNAVAILABLE,
    @SerialName("CONFIGURATION_ERROR")
    CONFIGURATION_ERROR,
    @SerialName("FEATURE_DISABLED")
    FEATURE_DISABLED,
  }

  /**
   * Error category for client-side handling logic.
   */
  @Serializable
  @Immutable
  public enum class ErrorType {
    @SerialName("authentication")
    AUTHENTICATION,
    @SerialName("authorization")
    AUTHORIZATION,
    @SerialName("validation")
    VALIDATION,
    @SerialName("not_found")
    NOT_FOUND,
    @SerialName("conflict")
    CONFLICT,
    @SerialName("rate_limit")
    RATE_LIMIT,
    @SerialName("external_service")
    EXTERNAL_SERVICE,
    @SerialName("sync")
    SYNC,
    @SerialName("internal")
    INTERNAL,
    @SerialName("configuration")
    CONFIGURATION,
  }
}

@Serializable
@Immutable
public data class ErrorResponse(
  @SerialName("success")
  public val success: Boolean,
  @SerialName("error")
  public val error: ErrorObject,
  @SerialName("meta")
  public val meta: Meta,
)

@Serializable
@Immutable
public data class AuthTokens(
  @SerialName("access_token")
  public val accessToken: String,
  @SerialName("refresh_token")
  public val refreshToken: String,
  @SerialName("token_type")
  public val tokenType: TokenType,
  /**
   * Seconds until access token expiry
   */
  @SerialName("expires_in")
  public val expiresIn: Long,
  @SerialName("refresh_expires_in")
  public val refreshExpiresIn: Long? = null,
) {
  @Serializable
  @Immutable
  public enum class TokenType {
    @SerialName("Bearer")
    BEARER,
  }
}

@Serializable
@Immutable
public data class LoginData(
  @SerialName("tokens")
  public val tokens: AuthTokens,
  @SerialName("user")
  public val user: User,
  @SerialName("preferences")
  public val preferences: UserPreferences? = null,
)

@Serializable
@Immutable
public data class LoginResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: LoginData? = null,
)

@Serializable
@Immutable
public data class AuthTokensResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: AuthTokens? = null,
)

/**
 * Request body for secret-key based authentication.
 */
@Serializable
@Immutable
public data class SecretLoginRequest(
  /**
   * User ID (must be whitelisted in ALLOWED_USER_IDS).
   */
  @SerialName("user_id")
  public val userId: Long,
  /**
   * Client application identifier.
   */
  @SerialName("client_id")
  public val clientId: String,
  /**
   * Secret key for authentication (minimum 8 characters).
   */
  @SerialName("secret")
  public val secret: String,
  /**
   * Optional username to associate with the session.
   */
  @SerialName("username")
  public val username: String? = null,
)

/**
 * Request body for nickname/email + password login.
 */
@Serializable
@Immutable
public data class CredentialsLoginRequest(
  /**
   * Nickname or email. Presence of `@` routes to the email branch;
   * otherwise treated as a nickname. Lookup is canonicalized
   * (lowercase + trim) before hitting the credentials table.
   */
  @SerialName("identifier")
  public val identifier: String,
  /**
   * Plaintext password (HMAC-peppered + argon2id-verified server-side).
   */
  @SerialName("password")
  public val password: String,
  /**
   * True → 30-day refresh TTL, web client persists tokens in localStorage.
   * False → short-lived refresh (default 12h), refresh cookie issued as a
   * session cookie (no Max-Age) so it vanishes on browser close.
   */
  @SerialName("remember_me")
  public val rememberMe: Boolean? = null,
  /**
   * Client application identifier (must be in ALLOWED_CLIENT_IDS if that env var is set).
   */
  @SerialName("client_id")
  public val clientId: String,
)

/**
 * Request body for owner-only password change.
 */
@Serializable
@Immutable
public data class ChangePasswordRequest(
  /**
   * Current plaintext password (verified before update).
   */
  @SerialName("current_password")
  public val currentPassword: String,
  /**
   * New plaintext password (validated against credentials_password_min_length).
   */
  @SerialName("new_password")
  public val newPassword: String,
)

@Serializable
@Immutable
public data class SecretKeyCreateRequest(
  @SerialName("user_id")
  public val userId: Long,
  @SerialName("client_id")
  public val clientId: String,
  @SerialName("label")
  public val label: String? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("expires_at")
  public val expiresAt: Instant? = null,
  /**
   * Optional client-provided secret; if omitted server generates
   */
  @SerialName("secret")
  public val secret: String? = null,
  @SerialName("username")
  public val username: String? = null,
)

@Serializable
@Immutable
public data class SecretKeyRotateRequest(
  @SerialName("label")
  public val label: String? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("expires_at")
  public val expiresAt: Instant? = null,
  /**
   * Optional client-provided secret; if omitted server generates
   */
  @SerialName("secret")
  public val secret: String? = null,
)

@Serializable
@Immutable
public data class SecretKeyRevokeRequest(
  @SerialName("reason")
  public val reason: String? = null,
)

@Serializable
@Immutable
public data class ClientSecretInfo(
  @SerialName("id")
  public val id: Long,
  @SerialName("user_id")
  public val userId: Long,
  @SerialName("client_id")
  public val clientId: String,
  @SerialName("status")
  public val status: Status,
  @SerialName("label")
  public val label: String? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("expires_at")
  public val expiresAt: Instant? = null,
  @SerialName("last_used_at")
  public val lastUsedAt: Instant? = null,
  @SerialName("failed_attempts")
  public val failedAttempts: Long,
  @SerialName("locked_until")
  public val lockedUntil: Instant? = null,
  @SerialName("created_at")
  public val createdAt: Instant,
  @SerialName("updated_at")
  public val updatedAt: Instant,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("active")
    ACTIVE,
    @SerialName("revoked")
    REVOKED,
    @SerialName("expired")
    EXPIRED,
    @SerialName("locked")
    LOCKED,
  }
}

@Serializable
@Immutable
public data class SecretKeyCreateResponse(
  /**
   * Plaintext secret (only returned once)
   */
  @SerialName("secret")
  public val secret: String,
  @SerialName("key")
  public val key: ClientSecretInfo,
)

@Serializable
@Immutable
public data class SecretKeyActionResponse(
  @SerialName("key")
  public val key: ClientSecretInfo,
)

@Serializable
@Immutable
public data class SecretKeyListResponse(
  @SerialName("keys")
  public val keys: List<ClientSecretInfo> = emptyList(),
)

@Serializable
@Immutable
public data class SecretKeyCreateResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SecretKeyCreateResponse? = null,
)

@Serializable
@Immutable
public data class SecretKeyActionResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SecretKeyActionResponse? = null,
)

@Serializable
@Immutable
public data class SecretKeyListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SecretKeyListResponse? = null,
)

@Serializable
@Immutable
public data class TelegramLinkStatus(
  @SerialName("linked")
  public val linked: Boolean,
  @SerialName("telegram_user_id")
  public val telegramUserId: Long? = null,
  @SerialName("username")
  public val username: String? = null,
  @SerialName("photo_url")
  public val photoUrl: String? = null,
  @SerialName("first_name")
  public val firstName: String? = null,
  @SerialName("last_name")
  public val lastName: String? = null,
  @SerialName("linked_at")
  public val linkedAt: Instant? = null,
  @SerialName("link_nonce_expires_at")
  public val linkNonceExpiresAt: Instant? = null,
  @SerialName("link_nonce")
  public val linkNonce: String? = null,
)

@Serializable
@Immutable
public data class TelegramLinkBeginResponse(
  @SerialName("nonce")
  public val nonce: String,
  @SerialName("expires_at")
  public val expiresAt: Instant,
)

@Serializable
@Immutable
public data class TelegramLinkCompleteRequest(
  @SerialName("id")
  public val id: Long? = null,
  @SerialName("hash")
  public val hash: String? = null,
  @SerialName("auth_date")
  public val authDate: Long? = null,
  @SerialName("username")
  public val username: String? = null,
  @SerialName("first_name")
  public val firstName: String? = null,
  @SerialName("last_name")
  public val lastName: String? = null,
  @SerialName("photo_url")
  public val photoUrl: String? = null,
  /**
   * Client application ID (e.g., 'android-app-v1.0', 'ios-app-v2.0')
   */
  @SerialName("client_id")
  public val clientId: String? = null,
  @SerialName("nonce")
  public val nonce: String? = null,
)

@Serializable
@Immutable
public data class TelegramLinkStatusEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TelegramLinkStatus? = null,
)

@Serializable
@Immutable
public data class TelegramLinkBeginResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TelegramLinkBeginResponse? = null,
)

@Serializable
@Immutable
public data class User(
  @SerialName("userId")
  public val userId: Long,
  @SerialName("username")
  public val username: String,
  @SerialName("clientId")
  public val clientId: String,
  @SerialName("isOwner")
  public val isOwner: Boolean,
  @SerialName("createdAt")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class UserResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: User? = null,
)

@Serializable
@Immutable
public data class UserPreferences(
  /**
   * User identifier.
   */
  @SerialName("userId")
  public val userId: Long,
  /**
   * Telegram username (if linked).
   */
  @SerialName("telegramUsername")
  public val telegramUsername: String? = null,
  @SerialName("langPreference")
  public val langPreference: LangPreference? = null,
  @SerialName("notificationSettings")
  public val notificationSettings: JsonElement? = null,
  @SerialName("appSettings")
  public val appSettings: JsonElement? = null,
) {
  @Serializable
  @Immutable
  public enum class LangPreference {
    @SerialName("auto")
    AUTO,
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
  }
}

@Serializable
@Immutable
public data class UserPreferencesResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: UserPreferences? = null,
)

@Serializable
@Immutable
public data class UserStats(
  @SerialName("totalSummaries")
  public val totalSummaries: Long,
  @SerialName("unreadCount")
  public val unreadCount: Long,
  @SerialName("readCount")
  public val readCount: Long,
  @SerialName("totalReadingTimeMin")
  public val totalReadingTimeMin: Long,
  @SerialName("averageReadingTimeMin")
  public val averageReadingTimeMin: Double,
  @SerialName("favoriteTopics")
  public val favoriteTopics: List<FavoriteTopicsItem> = emptyList(),
  @SerialName("favoriteDomains")
  public val favoriteDomains: List<FavoriteDomainsItem> = emptyList(),
  @SerialName("languageDistribution")
  public val languageDistribution: Map<String, Long> = emptyMap(),
  @SerialName("joinedAt")
  public val joinedAt: Instant? = null,
  @SerialName("lastSummaryAt")
  public val lastSummaryAt: Instant? = null,
) {
  @Serializable
  @Immutable
  public data class FavoriteTopicsItem(
    @SerialName("topic")
    public val topic: String,
    @SerialName("count")
    public val count: Long,
  )

  @Serializable
  @Immutable
  public data class FavoriteDomainsItem(
    @SerialName("domain")
    public val domain: String,
    @SerialName("count")
    public val count: Long,
  )
}

@Serializable
@Immutable
public data class UserStatsResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: UserStats? = null,
)

/**
 * Request body for registering a device for push notifications.
 */
@Serializable
@Immutable
public data class DeviceRegistrationPayload(
  /**
   * FCM or APNS device token.
   */
  @SerialName("token")
  public val token: String,
  /**
   * Mobile platform (must be 'ios' or 'android').
   */
  @SerialName("platform")
  public val platform: Platform,
  /**
   * Unique device identifier (optional).
   */
  @SerialName("device_id")
  public val deviceId: String? = null,
) {
  /**
   * Mobile platform (must be 'ios' or 'android').
   */
  @Serializable
  @Immutable
  public enum class Platform {
    @SerialName("ios")
    IOS,
    @SerialName("android")
    ANDROID,
  }
}

@Serializable
@Immutable
public data class StreamDoneEvent(
  @SerialName("kind")
  public val kind: Kind,
  @SerialName("payload")
  public val payload: Payload,
  @SerialName("timestamp")
  public val timestamp: Instant,
  @SerialName("correlation_id")
  public val correlationId: String,
) {
  @Serializable
  @Immutable
  public enum class Kind {
    @SerialName("done")
    DONE,
  }

  @Serializable
  @Immutable
  public data class Payload(
    @SerialName("summary_id")
    public val summaryId: String? = null,
    @SerialName("request_id")
    public val requestId: String,
  )
}

@Serializable
@Immutable
public data class StreamErrorEvent(
  @SerialName("kind")
  public val kind: Kind,
  @SerialName("payload")
  public val payload: Payload,
  @SerialName("timestamp")
  public val timestamp: Instant,
  @SerialName("correlation_id")
  public val correlationId: String,
) {
  @Serializable
  @Immutable
  public enum class Kind {
    @SerialName("error")
    ERROR,
  }

  @Serializable
  @Immutable
  public data class Payload(
    @SerialName("code")
    public val code: String,
    @SerialName("message")
    public val message: String,
    @SerialName("correlation_id")
    public val correlationId: String,
  )
}

@Serializable
@Immutable
public data class StreamPhaseEvent(
  @SerialName("kind")
  public val kind: Kind,
  @SerialName("payload")
  public val payload: Payload,
  @SerialName("timestamp")
  public val timestamp: Instant,
  @SerialName("correlation_id")
  public val correlationId: String,
) {
  @Serializable
  @Immutable
  public enum class Kind {
    @SerialName("phase")
    PHASE,
  }

  @Serializable
  @Immutable
  public data class Payload(
    @SerialName("phase")
    public val phase: Phase,
  ) {
    @Serializable
    @Immutable
    public enum class Phase {
      @SerialName("extracting")
      EXTRACTING,
      @SerialName("summarizing")
      SUMMARIZING,
      @SerialName("validating")
      VALIDATING,
      @SerialName("persisting")
      PERSISTING,
      @SerialName("done")
      DONE,
    }
  }
}

@Serializable
@Immutable
public data class StreamSectionEvent(
  @SerialName("kind")
  public val kind: Kind,
  @SerialName("payload")
  public val payload: Payload,
  @SerialName("timestamp")
  public val timestamp: Instant,
  @SerialName("correlation_id")
  public val correlationId: String,
) {
  @Serializable
  @Immutable
  public enum class Kind {
    @SerialName("section")
    SECTION,
  }

  @Serializable
  @Immutable
  public data class Payload(
    /**
     * Section name from the summary contract (e.g. tldr, summary_250, key_ideas, topic_tags).
     */
    @SerialName("section")
    public val section: String,
    /**
     * For string sections, the section text; for list sections, a JSON-encoded array string.
     */
    @SerialName("content")
    public val content: String,
    /**
     * True only for in-flight previews; false once the section is closed.
     */
    @SerialName("partial")
    public val partial: Boolean,
  )
}

@Serializable
@Immutable
public data class SuccessMessageEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("message")
    public val message: String,
  )
}

@Serializable
@Immutable
public data class SummaryPayload(
  @SerialName("summary_250")
  public val summary250: String,
  @SerialName("summary_1000")
  public val summary1000: String,
  @SerialName("tldr")
  public val tldr: String,
  @SerialName("key_ideas")
  public val keyIdeas: List<String> = emptyList(),
  @SerialName("topic_tags")
  public val topicTags: List<String> = emptyList(),
  @SerialName("entities")
  public val entities: Entities,
  @SerialName("estimated_reading_time_min")
  public val estimatedReadingTimeMin: Long,
  @SerialName("key_stats")
  public val keyStats: List<KeyStatsItem>? = null,
  @SerialName("readability")
  public val readability: Readability? = null,
  @SerialName("metadata")
  public val metadata: Metadata? = null,
  @SerialName("extractive_quotes")
  public val extractiveQuotes: List<ExtractiveQuotesItem>? = null,
  @SerialName("questions_answered")
  public val questionsAnswered: List<QuestionsAnsweredItem>? = null,
  @SerialName("topic_taxonomy")
  public val topicTaxonomy: List<TopicTaxonomyItem>? = null,
  @SerialName("hallucination_risk")
  public val hallucinationRisk: String? = null,
  @SerialName("confidence")
  public val confidence: Double? = null,
  @SerialName("insights")
  public val insights: Insights? = null,
) {
  @Serializable
  @Immutable
  public data class Entities(
    @SerialName("people")
    public val people: List<String>? = null,
    @SerialName("organizations")
    public val organizations: List<String>? = null,
    @SerialName("locations")
    public val locations: List<String>? = null,
  )

  @Serializable
  @Immutable
  public data class KeyStatsItem(
    @SerialName("label")
    public val label: String? = null,
    @SerialName("value")
    public val `value`: Double? = null,
    @SerialName("unit")
    public val unit: String? = null,
    @SerialName("source_excerpt")
    public val sourceExcerpt: String? = null,
  )

  @Serializable
  @Immutable
  public data class Readability(
    @SerialName("method")
    public val method: String? = null,
    @SerialName("score")
    public val score: Double? = null,
    @SerialName("level")
    public val level: String? = null,
  )

  @Serializable
  @Immutable
  public data class Metadata(
    @SerialName("title")
    public val title: String? = null,
    @SerialName("canonical_url")
    public val canonicalUrl: String? = null,
    @SerialName("domain")
    public val domain: String? = null,
    @SerialName("author")
    public val author: String? = null,
    @SerialName("published_at")
    public val publishedAt: Instant? = null,
    @SerialName("last_updated")
    public val lastUpdated: Instant? = null,
  )

  @Serializable
  @Immutable
  public data class ExtractiveQuotesItem(
    @SerialName("text")
    public val text: String? = null,
    @SerialName("source_span")
    public val sourceSpan: String? = null,
  )

  @Serializable
  @Immutable
  public data class QuestionsAnsweredItem(
    @SerialName("question")
    public val question: String? = null,
    @SerialName("answer")
    public val answer: String? = null,
  )

  @Serializable
  @Immutable
  public data class TopicTaxonomyItem(
    @SerialName("label")
    public val label: String? = null,
    @SerialName("score")
    public val score: Double? = null,
    @SerialName("path")
    public val path: String? = null,
  )

  @Serializable
  @Immutable
  public data class Insights(
    @SerialName("topic_overview")
    public val topicOverview: String? = null,
    @SerialName("new_facts")
    public val newFacts: List<NewFactsItem>? = null,
    @SerialName("open_questions")
    public val openQuestions: List<String>? = null,
    @SerialName("caution")
    public val caution: String? = null,
  ) {
    @Serializable
    @Immutable
    public data class NewFactsItem(
      @SerialName("fact")
      public val fact: String? = null,
      @SerialName("why_it_matters")
      public val whyItMatters: String? = null,
      @SerialName("confidence")
      public val confidence: Double? = null,
    )
  }
}

@Serializable
@Immutable
public data class Summary(
  @SerialName("id")
  public val id: Long,
  @SerialName("request_id")
  public val requestId: Long,
  @SerialName("lang")
  public val lang: String,
  @SerialName("is_read")
  public val isRead: Boolean,
  @SerialName("version")
  public val version: Long,
  @SerialName("created_at")
  public val createdAt: Instant,
  @SerialName("json_payload")
  public val jsonPayload: SummaryPayload,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("domain")
  public val domain: String? = null,
  @SerialName("url")
  public val url: String? = null,
  @SerialName("tldr")
  public val tldr: String? = null,
  @SerialName("is_favorited")
  public val isFavorited: Boolean? = null,
)

/**
 * Compact summary representation for list views.
 */
@Serializable
@Immutable
public data class SummaryListItem(
  /**
   * Unique summary identifier.
   */
  @SerialName("id")
  public val id: Long,
  /**
   * Associated request ID.
   */
  @SerialName("requestId")
  public val requestId: Long,
  /**
   * Article title.
   */
  @SerialName("title")
  public val title: String,
  /**
   * Source domain (e.g., example.com).
   */
  @SerialName("domain")
  public val domain: String,
  /**
   * Original article URL.
   */
  @SerialName("url")
  public val url: String,
  /**
   * Concise multi-sentence summary.
   */
  @SerialName("tldr")
  public val tldr: String,
  /**
   * Short summary (maximum 250 characters).
   */
  @SerialName("summary250")
  public val summary250: String,
  /**
   * Estimated reading time in minutes.
   */
  @SerialName("readingTimeMin")
  public val readingTimeMin: Long,
  /**
   * Topic hashtags (e.g.,
   */
  @SerialName("topicTags")
  public val topicTags: List<String> = emptyList(),
  /**
   * Whether the user has marked this as read.
   */
  @SerialName("isRead")
  public val isRead: Boolean,
  /**
   * Detected or preferred language.
   */
  @SerialName("lang")
  public val lang: Lang,
  /**
   * ISO 8601 creation timestamp.
   */
  @SerialName("createdAt")
  public val createdAt: Instant,
  /**
   * LLM confidence score (0.0-1.0).
   */
  @SerialName("confidence")
  public val confidence: Double,
  /**
   * Assessed hallucination risk level.
   */
  @SerialName("hallucinationRisk")
  public val hallucinationRisk: HallucinationRisk,
  /**
   * Whether the user has favorited this summary.
   */
  @SerialName("isFavorited")
  public val isFavorited: Boolean? = null,
  /**
   * Featured image URL (if available).
   */
  @SerialName("imageUrl")
  public val imageUrl: String? = null,
) {
  /**
   * Detected or preferred language.
   */
  @Serializable
  @Immutable
  public enum class Lang {
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
    @SerialName("auto")
    AUTO,
  }

  /**
   * Assessed hallucination risk level.
   */
  @Serializable
  @Immutable
  public enum class HallucinationRisk {
    @SerialName("low")
    LOW,
    @SerialName("medium")
    MEDIUM,
    @SerialName("high")
    HIGH,
    @SerialName("unknown")
    UNKNOWN,
  }
}

@Serializable
@Immutable
public data class SummaryStats(
  @SerialName("totalSummaries")
  public val totalSummaries: Long,
  @SerialName("unreadCount")
  public val unreadCount: Long,
)

@Serializable
@Immutable
public data class PaginatedSummariesData(
  @SerialName("summaries")
  public val summaries: List<SummaryListItem> = emptyList(),
  @SerialName("stats")
  public val stats: SummaryStats? = null,
  @SerialName("pagination")
  public val pagination: Pagination,
)

@Serializable
@Immutable
public data class PaginatedSummariesResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: PaginatedSummariesData? = null,
)

@Serializable
@Immutable
public data class SummaryDetailData(
  @SerialName("summary")
  public val summary: Summary,
  @SerialName("request")
  public val request: Request,
  @SerialName("source")
  public val source: Source,
  @SerialName("processing")
  public val processing: Processing,
) {
  @Serializable
  @Immutable
  public data class Summary(
    @SerialName("summary250")
    public val summary250: String,
    @SerialName("summary1000")
    public val summary1000: String,
    @SerialName("tldr")
    public val tldr: String,
    @SerialName("keyIdeas")
    public val keyIdeas: List<String> = emptyList(),
    @SerialName("topicTags")
    public val topicTags: List<String> = emptyList(),
    @SerialName("entities")
    public val entities: Entities,
    @SerialName("estimatedReadingTimeMin")
    public val estimatedReadingTimeMin: Long,
    @SerialName("keyStats")
    public val keyStats: List<KeyStatsItem>? = null,
    @SerialName("answeredQuestions")
    public val answeredQuestions: List<String>? = null,
    @SerialName("readability")
    public val readability: Readability? = null,
    @SerialName("seoKeywords")
    public val seoKeywords: List<String>? = null,
  ) {
    @Serializable
    @Immutable
    public data class Entities(
      @SerialName("people")
      public val people: List<String>? = null,
      @SerialName("organizations")
      public val organizations: List<String>? = null,
      @SerialName("locations")
      public val locations: List<String>? = null,
    )

    @Serializable
    @Immutable
    public data class KeyStatsItem(
      @SerialName("label")
      public val label: String? = null,
      @SerialName("value")
      public val `value`: Double? = null,
      @SerialName("unit")
      public val unit: String? = null,
      @SerialName("sourceExcerpt")
      public val sourceExcerpt: String? = null,
    )

    @Serializable
    @Immutable
    public data class Readability(
      @SerialName("method")
      public val method: String? = null,
      @SerialName("score")
      public val score: Double? = null,
      @SerialName("level")
      public val level: String? = null,
    )
  }

  @Serializable
  @Immutable
  public data class Request(
    @SerialName("id")
    public val id: String,
    @SerialName("type")
    public val type: String,
    @SerialName("status")
    public val status: String,
    @SerialName("url")
    public val url: String? = null,
    @SerialName("normalizedUrl")
    public val normalizedUrl: String? = null,
    @SerialName("dedupeHash")
    public val dedupeHash: String? = null,
    @SerialName("langDetected")
    public val langDetected: String? = null,
    @SerialName("createdAt")
    public val createdAt: Instant,
    @SerialName("updatedAt")
    public val updatedAt: Instant,
  )

  @Serializable
  @Immutable
  public data class Source(
    @SerialName("url")
    public val url: String? = null,
    @SerialName("title")
    public val title: String? = null,
    @SerialName("domain")
    public val domain: String? = null,
    @SerialName("author")
    public val author: String? = null,
    @SerialName("publishedAt")
    public val publishedAt: Instant? = null,
    @SerialName("wordCount")
    public val wordCount: Long? = null,
    @SerialName("contentType")
    public val contentType: String? = null,
  )

  @Serializable
  @Immutable
  public data class Processing(
    @SerialName("modelUsed")
    public val modelUsed: String? = null,
    @SerialName("tokensUsed")
    public val tokensUsed: Long? = null,
    @SerialName("processingTimeMs")
    public val processingTimeMs: Long? = null,
    @SerialName("crawlTimeMs")
    public val crawlTimeMs: Long? = null,
    @SerialName("confidence")
    public val confidence: Double? = null,
    @SerialName("hallucinationRisk")
    public val hallucinationRisk: String? = null,
  )
}

@Serializable
@Immutable
public data class SummaryContent(
  @SerialName("summaryId")
  public val summaryId: Long,
  @SerialName("requestId")
  public val requestId: Long? = null,
  @SerialName("format")
  public val format: Format,
  /**
   * Full article content for offline reading.
   */
  @SerialName("content")
  public val content: String,
  /**
   * MIME type for the returned content (e.g., text/markdown).
   */
  @SerialName("contentType")
  public val contentType: String,
  @SerialName("lang")
  public val lang: String? = null,
  @SerialName("sourceUrl")
  public val sourceUrl: String? = null,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("domain")
  public val domain: String? = null,
  @SerialName("retrievedAt")
  public val retrievedAt: Instant,
  @SerialName("sizeBytes")
  public val sizeBytes: Long? = null,
  @SerialName("checksumSha256")
  public val checksumSha256: String? = null,
) {
  @Serializable
  @Immutable
  public enum class Format {
    @SerialName("markdown")
    MARKDOWN,
    @SerialName("text")
    TEXT,
    @SerialName("html")
    HTML,
  }
}

@Serializable
@Immutable
public data class SummaryContentData(
  @SerialName("content")
  public val content: SummaryContent,
)

@Serializable
@Immutable
public data class SummaryContentResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SummaryContentData? = null,
)

@Serializable
@Immutable
public data class SummaryDetailResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SummaryDetailData? = null,
)

@Serializable
@Immutable
public data class SummaryUpdateResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("id")
    public val id: Long,
    @SerialName("isRead")
    public val isRead: Boolean,
    @SerialName("updatedAt")
    public val updatedAt: Instant,
  )
}

@Serializable
@Immutable
public data class SummaryDeleteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("id")
    public val id: Long,
    @SerialName("deletedAt")
    public val deletedAt: Instant,
  )
}

@Serializable
@Immutable
public data class ToggleFavoriteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("success")
    public val success: Boolean,
    @SerialName("isFavorited")
    public val isFavorited: Boolean,
  )
}

@Serializable
@Immutable
public data class SuccessResponse(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: JsonElement? = null,
)

@Serializable
@Immutable
public data class SignalFeedbackRequest(
  @SerialName("action")
  public val action: Action,
) {
  @Serializable
  @Immutable
  public enum class Action {
    @SerialName("like")
    LIKE,
    @SerialName("dislike")
    DISLIKE,
    @SerialName("skip")
    SKIP,
    @SerialName("hide_source")
    HIDE_SOURCE,
    @SerialName("queue")
    QUEUE,
    @SerialName("boost_topic")
    BOOST_TOPIC,
  }
}

@Serializable
@Immutable
public data class SourceActiveRequest(
  @SerialName("is_active")
  public val isActive: Boolean,
)

@Serializable
@Immutable
public data class TopicPreferenceRequest(
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("weight")
  public val weight: Double? = null,
)

@Serializable
@Immutable
public data class SignalListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("signals")
    public val signals: List<SignalItem> = emptyList(),
  )
}

@Serializable
@Immutable
public data class SignalHealthResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("vector")
    public val vector: Vector? = null,
    @SerialName("sources")
    public val sources: Sources? = null,
  ) {
    @Serializable
    @Immutable
    public data class Vector(
      @SerialName("ready")
      public val ready: Boolean? = null,
      @SerialName("required")
      public val required: Boolean? = null,
      @SerialName("collection")
      public val collection: String? = null,
    )

    @Serializable
    @Immutable
    public data class Sources(
      @SerialName("total")
      public val total: Long? = null,
      @SerialName("active")
      public val active: Long? = null,
      @SerialName("errored")
      public val errored: Long? = null,
    )
  }
}

@Serializable
@Immutable
public data class SignalSourceHealthResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("sources")
    public val sources: List<SignalSourceHealth> = emptyList(),
  )
}

@Serializable
@Immutable
public data class SignalFeedbackResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("updated")
    public val updated: Boolean? = null,
  )
}

@Serializable
@Immutable
public data class SignalSourceActiveResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("updated")
    public val updated: Boolean? = null,
    @SerialName("is_active")
    public val isActive: Boolean? = null,
  )
}

@Serializable
@Immutable
public data class SignalTopicResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("topic")
    public val topic: JsonElement? = null,
  )
}

@Serializable
@Immutable
public data class Collection(
  @SerialName("id")
  public val id: Long,
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("parentId")
  public val parentId: Long? = null,
  @SerialName("position")
  public val position: Long? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
  @SerialName("serverVersion")
  public val serverVersion: Long,
  @SerialName("isShared")
  public val isShared: Boolean,
  @SerialName("shareCount")
  public val shareCount: Long? = null,
  @SerialName("itemCount")
  public val itemCount: Long? = null,
  @SerialName("children")
  public val children: List<Collection>? = null,
  @SerialName("aclSummary")
  public val aclSummary: AclSummary? = null,
) {
  @Serializable
  @Immutable
  public data class AclSummary(
    @SerialName("totalCollaborators")
    public val totalCollaborators: Long? = null,
    @SerialName("roles")
    public val roles: List<String>? = null,
  )
}

@Serializable
@Immutable
public data class CollectionCreateRequest(
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("parent_id")
  public val parentId: Long? = null,
  @SerialName("position")
  public val position: Long? = null,
  @SerialName("collection_type")
  public val collectionType: CollectionType? = null,
  @SerialName("query_conditions")
  public val queryConditions: List<JsonElement>? = null,
  @SerialName("query_match_mode")
  public val queryMatchMode: QueryMatchMode? = null,
) {
  @Serializable
  @Immutable
  public enum class CollectionType {
    @SerialName("manual")
    MANUAL,
    @SerialName("smart")
    SMART,
  }

  @Serializable
  @Immutable
  public enum class QueryMatchMode {
    @SerialName("all")
    ALL,
    @SerialName("any")
    ANY,
  }
}

@Serializable
@Immutable
public data class CollectionUpdateRequest(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("parent_id")
  public val parentId: Long? = null,
  @SerialName("position")
  public val position: Long? = null,
  @SerialName("query_conditions")
  public val queryConditions: List<JsonElement>? = null,
  @SerialName("query_match_mode")
  public val queryMatchMode: String? = null,
)

@Serializable
@Immutable
public data class CollectionItemCreateRequest(
  @SerialName("summary_id")
  public val summaryId: Long,
)

@Serializable
@Immutable
public data class CollectionItem(
  @SerialName("collectionId")
  public val collectionId: Long,
  @SerialName("summaryId")
  public val summaryId: Long,
  @SerialName("position")
  public val position: Long? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class CollectionItemsResponse(
  @SerialName("items")
  public val items: List<CollectionItem> = emptyList(),
  @SerialName("pagination")
  public val pagination: Pagination,
)

@Serializable
@Immutable
public data class CollectionListResponse(
  @SerialName("collections")
  public val collections: List<Collection> = emptyList(),
)

@Serializable
@Immutable
public data class CollectionAclEntry(
  @SerialName("userId")
  public val userId: Long? = null,
  @SerialName("role")
  public val role: Role,
  @SerialName("status")
  public val status: Status,
  @SerialName("invitedBy")
  public val invitedBy: Long? = null,
  @SerialName("createdAt")
  public val createdAt: Instant? = null,
  @SerialName("updatedAt")
  public val updatedAt: Instant? = null,
) {
  @Serializable
  @Immutable
  public enum class Role {
    @SerialName("owner")
    OWNER,
    @SerialName("editor")
    EDITOR,
    @SerialName("viewer")
    VIEWER,
  }

  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("active")
    ACTIVE,
    @SerialName("pending")
    PENDING,
    @SerialName("revoked")
    REVOKED,
  }
}

@Serializable
@Immutable
public data class CollectionAclResponse(
  @SerialName("acl")
  public val acl: List<CollectionAclEntry> = emptyList(),
)

@Serializable
@Immutable
public data class CollectionTreeResponse(
  @SerialName("collections")
  public val collections: List<Collection> = emptyList(),
)

@Serializable
@Immutable
public data class CollectionReorderRequest(
  @SerialName("items")
  public val items: List<ItemsItem> = emptyList(),
) {
  @Serializable
  @Immutable
  public data class ItemsItem(
    @SerialName("collection_id")
    public val collectionId: Long,
    @SerialName("position")
    public val position: Long,
  )
}

@Serializable
@Immutable
public data class CollectionItemReorderRequest(
  @SerialName("items")
  public val items: List<ItemsItem> = emptyList(),
) {
  @Serializable
  @Immutable
  public data class ItemsItem(
    @SerialName("summary_id")
    public val summaryId: Long,
    @SerialName("position")
    public val position: Long,
  )
}

@Serializable
@Immutable
public data class CollectionMoveRequest(
  @SerialName("parent_id")
  public val parentId: Long? = null,
  @SerialName("position")
  public val position: Long? = null,
)

@Serializable
@Immutable
public data class CollectionItemMoveRequest(
  @SerialName("summary_ids")
  public val summaryIds: List<Long> = emptyList(),
  @SerialName("target_collection_id")
  public val targetCollectionId: Long,
  @SerialName("position")
  public val position: Long? = null,
)

@Serializable
@Immutable
public data class CollectionShareRequest(
  @SerialName("user_id")
  public val userId: Long,
  @SerialName("role")
  public val role: Role,
) {
  @Serializable
  @Immutable
  public enum class Role {
    @SerialName("editor")
    EDITOR,
    @SerialName("viewer")
    VIEWER,
  }
}

@Serializable
@Immutable
public data class CollectionInviteRequest(
  @SerialName("role")
  public val role: Role,
  @SerialName("expires_at")
  public val expiresAt: Instant? = null,
) {
  @Serializable
  @Immutable
  public enum class Role {
    @SerialName("editor")
    EDITOR,
    @SerialName("viewer")
    VIEWER,
  }
}

@Serializable
@Immutable
public data class CollectionInviteResponse(
  @SerialName("token")
  public val token: String,
  @SerialName("role")
  public val role: String,
  @SerialName("expiresAt")
  public val expiresAt: Instant? = null,
)

@Serializable
@Immutable
public data class CollectionMoveResponse(
  @SerialName("id")
  public val id: Long,
  @SerialName("parentId")
  public val parentId: Long? = null,
  @SerialName("position")
  public val position: Long,
  @SerialName("serverVersion")
  public val serverVersion: Long? = null,
  @SerialName("updatedAt")
  public val updatedAt: Instant? = null,
)

@Serializable
@Immutable
public data class CollectionItemsMoveResponse(
  @SerialName("movedSummaryIds")
  public val movedSummaryIds: List<Long> = emptyList(),
)

@Serializable
@Immutable
public data class CollectionReorderResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SuccessResponse? = null,
)

@Serializable
@Immutable
public data class CollectionMoveResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionMoveResponse? = null,
)

@Serializable
@Immutable
public data class CollectionItemsMoveResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionItemsMoveResponse? = null,
)

@Serializable
@Immutable
public data class CollectionItemsResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionItemsResponse? = null,
)

@Serializable
@Immutable
public data class CollectionAclResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionAclResponse? = null,
)

@Serializable
@Immutable
public data class CollectionTreeResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionTreeResponse? = null,
)

@Serializable
@Immutable
public data class CollectionInviteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionInviteResponse? = null,
)

@Serializable
@Immutable
public data class CollectionResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Collection? = null,
)

@Serializable
@Immutable
public data class CollectionListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CollectionListResponse? = null,
)

@Serializable
@Immutable
public data class Request(
  @SerialName("id")
  public val id: Long,
  @SerialName("type")
  public val type: Type,
  @SerialName("status")
  public val status: Status,
  @SerialName("correlation_id")
  public val correlationId: String,
  @SerialName("input_url")
  public val inputUrl: String? = null,
  @SerialName("normalized_url")
  public val normalizedUrl: String? = null,
  @SerialName("dedupe_hash")
  public val dedupeHash: String? = null,
  @SerialName("lang_detected")
  public val langDetected: String? = null,
  @SerialName("created_at")
  public val createdAt: Instant,
) {
  @Serializable
  @Immutable
  public enum class Type {
    @SerialName("url")
    URL,
    @SerialName("forward")
    FORWARD,
  }

  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("pending")
    PENDING,
    @SerialName("processing")
    PROCESSING,
    @SerialName("success")
    SUCCESS,
    @SerialName("error")
    ERROR,
  }
}

@Serializable
@Immutable
public data class RequestDetail(
  @SerialName("request")
  public val request: Request,
  @SerialName("crawl_result")
  public val crawlResult: CrawlResult? = null,
  @SerialName("llm_calls")
  public val llmCalls: List<LlmCallsItem>? = null,
  @SerialName("summary")
  public val summary: SummaryListItem? = null,
) {
  @Serializable
  @Immutable
  public data class CrawlResult(
    @SerialName("status")
    public val status: String? = null,
    @SerialName("http_status")
    public val httpStatus: Long? = null,
    @SerialName("latency_ms")
    public val latencyMs: Long? = null,
    @SerialName("error")
    public val error: String? = null,
  )

  @Serializable
  @Immutable
  public data class LlmCallsItem(
    @SerialName("id")
    public val id: Long? = null,
    @SerialName("model")
    public val model: String? = null,
    @SerialName("status")
    public val status: String? = null,
    @SerialName("tokens_prompt")
    public val tokensPrompt: Long? = null,
    @SerialName("tokens_completion")
    public val tokensCompletion: Long? = null,
    @SerialName("cost_usd")
    public val costUsd: Double? = null,
    @SerialName("latency_ms")
    public val latencyMs: Long? = null,
    @SerialName("created_at")
    public val createdAt: Instant? = null,
  )
}

@Serializable
@Immutable
public data class RequestDetailResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RequestDetail? = null,
)

@Serializable
@Immutable
public data class SubmitRequestData(
  @SerialName("request")
  public val request: SubmitRequestResponse,
)

@Serializable
@Immutable
public data class SubmitRequestResponse(
  @SerialName("requestId")
  public val requestId: Long,
  @SerialName("correlationId")
  public val correlationId: String,
  @SerialName("type")
  public val type: Type,
  @SerialName("status")
  public val status: Status,
  @SerialName("estimatedWaitSeconds")
  public val estimatedWaitSeconds: Long? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("isDuplicate")
  public val isDuplicate: Boolean,
) {
  @Serializable
  @Immutable
  public enum class Type {
    @SerialName("url")
    URL,
    @SerialName("forward")
    FORWARD,
  }

  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("pending")
    PENDING,
    @SerialName("processing")
    PROCESSING,
    @SerialName("complete")
    COMPLETE,
    @SerialName("failed")
    FAILED,
  }
}

@Serializable
@Immutable
public data class SubmitRequestResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SubmitRequestData? = null,
)

@Serializable
@Immutable
public data class RequestStatusData(
  @SerialName("requestId")
  public val requestId: Long,
  /**
   * Raw request status from storage.
   */
  @SerialName("status")
  public val status: String,
  /**
   * Processing stage (maps to RequestStage enum).
   */
  @SerialName("stage")
  public val stage: Stage,
  @SerialName("progress")
  public val progress: Progress? = null,
  @SerialName("estimatedSecondsRemaining")
  public val estimatedSecondsRemaining: Long? = null,
  /**
   * 1-indexed position in processing queue (only when pending).
   */
  @SerialName("queuePosition")
  public val queuePosition: Long? = null,
  /**
   * Stage where error occurred (only when failed).
   */
  @SerialName("errorStage")
  public val errorStage: String? = null,
  /**
   * Error classification (only when failed).
   */
  @SerialName("errorType")
  public val errorType: String? = null,
  /**
   * Human-readable error message (only when failed).
   */
  @SerialName("errorMessage")
  public val errorMessage: String? = null,
  /**
   * Stable machine-readable reason code for failed requests.
   */
  @SerialName("errorReasonCode")
  public val errorReasonCode: String? = null,
  /**
   * Whether the latest failure can be retried.
   */
  @SerialName("retryable")
  public val retryable: Boolean? = null,
  /**
   * Optional sanitized debug context for failed requests.
   */
  @SerialName("debug")
  public val debug: JsonElement? = null,
  /**
   * Whether the request can be retried.
   */
  @SerialName("canRetry")
  public val canRetry: Boolean,
  @SerialName("correlationId")
  public val correlationId: String? = null,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
) {
  /**
   * Processing stage (maps to RequestStage enum).
   */
  @Serializable
  @Immutable
  public enum class Stage {
    @SerialName("pending")
    PENDING,
    @SerialName("crawling")
    CRAWLING,
    @SerialName("processing")
    PROCESSING,
    @SerialName("complete")
    COMPLETE,
    @SerialName("failed")
    FAILED,
  }

  @Serializable
  @Immutable
  public data class Progress(
    @SerialName("current_step")
    public val currentStep: Long? = null,
    @SerialName("total_steps")
    public val totalSteps: Long? = null,
    @SerialName("percentage")
    public val percentage: Long? = null,
  )
}

@Serializable
@Immutable
public data class RequestStatusResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RequestStatusData? = null,
)

@Serializable
@Immutable
public data class RequestRetryResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("new_request_id")
    public val newRequestId: Long,
    @SerialName("correlation_id")
    public val correlationId: String,
    @SerialName("status")
    public val status: String,
    @SerialName("created_at")
    public val createdAt: Instant,
  )
}

@Serializable
@Immutable
public data class SearchResultItem(
  @SerialName("request_id")
  public val requestId: Long,
  @SerialName("summary_id")
  public val summaryId: Long,
  @SerialName("url")
  public val url: String,
  @SerialName("title")
  public val title: String,
  @SerialName("domain")
  public val domain: String? = null,
  @SerialName("snippet")
  public val snippet: String? = null,
  @SerialName("tldr")
  public val tldr: String? = null,
  @SerialName("published_at")
  public val publishedAt: Instant? = null,
  @SerialName("created_at")
  public val createdAt: Instant,
  @SerialName("relevance_score")
  public val relevanceScore: Double? = null,
  @SerialName("topic_tags")
  public val topicTags: List<String>? = null,
  @SerialName("is_read")
  public val isRead: Boolean? = null,
)

@Serializable
@Immutable
public data class SearchResponseData(
  @SerialName("results")
  public val results: List<SearchResultItem> = emptyList(),
  @SerialName("pagination")
  public val pagination: Pagination,
  @SerialName("query")
  public val query: String,
)

@Serializable
@Immutable
public data class SearchResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SearchResponseData? = null,
)

@Serializable
@Immutable
public data class TrendingTopic(
  @SerialName("tag")
  public val tag: String,
  @SerialName("count")
  public val count: Long,
  @SerialName("trend")
  public val trend: String? = null,
  @SerialName("percentage_change")
  public val percentageChange: Double? = null,
)

@Serializable
@Immutable
public data class TrendingTopicsResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("tags")
    public val tags: List<TrendingTopic> = emptyList(),
    @SerialName("time_range")
    public val timeRange: TimeRange? = null,
  ) {
    @Serializable
    @Immutable
    public data class TimeRange(
      @SerialName("start")
      public val start: Instant? = null,
      @SerialName("end")
      public val end: Instant? = null,
    )
  }
}

@Serializable
@Immutable
public data class RelatedSummary(
  @SerialName("summary_id")
  public val summaryId: Long,
  @SerialName("title")
  public val title: String,
  @SerialName("tldr")
  public val tldr: String,
  @SerialName("created_at")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class RelatedTopicsResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("tag")
    public val tag: String,
    @SerialName("summaries")
    public val summaries: List<RelatedSummary> = emptyList(),
    @SerialName("pagination")
    public val pagination: Pagination,
  )
}

@Serializable
@Immutable
public data class DuplicateUrlCheckData(
  @SerialName("is_duplicate")
  public val isDuplicate: Boolean,
  @SerialName("normalized_url")
  public val normalizedUrl: String,
  @SerialName("dedupe_hash")
  public val dedupeHash: String,
  @SerialName("request_id")
  public val requestId: Long? = null,
  @SerialName("summary_id")
  public val summaryId: Long? = null,
  @SerialName("summarized_at")
  public val summarizedAt: Instant? = null,
  @SerialName("summary")
  public val summary: SummaryListItem? = null,
)

@Serializable
@Immutable
public data class DuplicateUrlCheckResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DuplicateUrlCheckData? = null,
)

@Serializable
@Immutable
public data class SyncSessionData(
  @SerialName("session_id")
  public val sessionId: String,
  @SerialName("expires_at")
  public val expiresAt: Instant,
  @SerialName("default_limit")
  public val defaultLimit: Long,
  @SerialName("max_limit")
  public val maxLimit: Long,
  @SerialName("last_issued_since")
  public val lastIssuedSince: Long? = null,
)

@Serializable
@Immutable
public data class SyncSessionResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SyncSessionData? = null,
)

@Serializable
@Immutable
public data class SyncEntityEnvelope(
  @SerialName("entity_type")
  public val entityType: EntityType,
  /**
   * Entity identifier. Always serialized as a string for KMP / mobile
   * client compatibility (numeric IDs are JSON-encoded as strings).
   */
  @SerialName("id")
  public val id: String,
  @SerialName("server_version")
  public val serverVersion: Long,
  @SerialName("updated_at")
  public val updatedAt: Instant,
  @SerialName("deleted_at")
  public val deletedAt: Instant? = null,
  @SerialName("summary")
  public val summary: Summary? = null,
  @SerialName("request")
  public val request: Request? = null,
  @SerialName("preference")
  public val preference: UserPreferences? = null,
  @SerialName("stat")
  public val stat: UserStats? = null,
) {
  @Serializable
  @Immutable
  public enum class EntityType {
    @SerialName("summary")
    SUMMARY,
    @SerialName("request")
    REQUEST,
    @SerialName("preference")
    PREFERENCE,
    @SerialName("stat")
    STAT,
    @SerialName("crawl_result")
    CRAWL_RESULT,
    @SerialName("llm_call")
    LLM_CALL,
    @SerialName("highlight")
    HIGHLIGHT,
    @SerialName("tag")
    TAG,
    @SerialName("summary_tag")
    SUMMARY_TAG,
  }
}

@Serializable
@Immutable
public data class FullSyncResponseData(
  @SerialName("session_id")
  public val sessionId: String,
  @SerialName("has_more")
  public val hasMore: Boolean,
  @SerialName("next_since")
  public val nextSince: Long? = null,
  @SerialName("items")
  public val items: List<FullSyncItem> = emptyList(),
  @SerialName("pagination")
  public val pagination: Pagination,
)

@Serializable
@Immutable
public data class FullSyncResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: FullSyncResponseData? = null,
)

@Serializable
@Immutable
public data class DeltaSyncResponseData(
  @SerialName("session_id")
  public val sessionId: String,
  @SerialName("since")
  public val since: Long,
  @SerialName("has_more")
  public val hasMore: Boolean,
  @SerialName("next_since")
  public val nextSince: Long? = null,
  @SerialName("created")
  public val created: List<SyncEntityEnvelope> = emptyList(),
  @SerialName("updated")
  public val updated: List<SyncEntityEnvelope> = emptyList(),
  @SerialName("deleted")
  public val deleted: List<SyncEntityEnvelope> = emptyList(),
)

@Serializable
@Immutable
public data class DeltaSyncResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DeltaSyncResponseData? = null,
)

@Serializable
@Immutable
public data class SyncApplyResult(
  /**
   * Entity identifier. Always serialized as a string for KMP / mobile
   * client compatibility (numeric IDs are JSON-encoded as strings).
   */
  @SerialName("id")
  public val id: String,
  @SerialName("entity_type")
  public val entityType: String,
  @SerialName("status")
  public val status: Status,
  @SerialName("server_version")
  public val serverVersion: Long? = null,
  @SerialName("server_snapshot")
  public val serverSnapshot: SyncEntityEnvelope? = null,
  @SerialName("error_code")
  public val errorCode: String? = null,
  @SerialName("message")
  public val message: String? = null,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("applied")
    APPLIED,
    @SerialName("conflict")
    CONFLICT,
    @SerialName("invalid")
    INVALID,
  }
}

@Serializable
@Immutable
public data class SyncApplyResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("session_id")
    public val sessionId: String,
    @SerialName("results")
    public val results: List<SyncApplyResult> = emptyList(),
    @SerialName("conflicts")
    public val conflicts: List<SyncApplyResult>? = null,
    @SerialName("has_more")
    public val hasMore: Boolean? = null,
  )
}

@Serializable
@Immutable
public data class ChannelSubscription(
  @SerialName("id")
  public val id: Long,
  @SerialName("username")
  public val username: String,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("is_active")
  public val isActive: Boolean,
  @SerialName("fetch_error_count")
  public val fetchErrorCount: Long,
  @SerialName("last_error")
  public val lastError: String? = null,
  @SerialName("category_id")
  public val categoryId: Long? = null,
  @SerialName("category_name")
  public val categoryName: String? = null,
  @SerialName("created_at")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class ChannelSubscriptionListData(
  @SerialName("channels")
  public val channels: List<ChannelSubscription> = emptyList(),
  @SerialName("active_count")
  public val activeCount: Long,
  @SerialName("max_channels")
  public val maxChannels: Long? = null,
  @SerialName("unlimited_channels")
  public val unlimitedChannels: Boolean,
)

@Serializable
@Immutable
public data class ChannelSubscriptionListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ChannelSubscriptionListData? = null,
)

@Serializable
@Immutable
public data class SubscribeActionData(
  @SerialName("status")
  public val status: String,
  @SerialName("username")
  public val username: String,
)

@Serializable
@Immutable
public data class SubscribeActionResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SubscribeActionData? = null,
)

@Serializable
@Immutable
public data class ResolveChannelData(
  @SerialName("username")
  public val username: String,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("member_count")
  public val memberCount: Long? = null,
  @SerialName("is_subscribed")
  public val isSubscribed: Boolean,
)

@Serializable
@Immutable
public data class ResolveChannelResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ResolveChannelData? = null,
)

@Serializable
@Immutable
public data class DigestPreferencesData(
  @SerialName("delivery_time")
  public val deliveryTime: String,
  /**
   * "user" if overridden, "global" if inherited from defaults
   */
  @SerialName("delivery_time_source")
  public val deliveryTimeSource: String,
  @SerialName("timezone")
  public val timezone: String,
  @SerialName("timezone_source")
  public val timezoneSource: String,
  @SerialName("hours_lookback")
  public val hoursLookback: Long,
  @SerialName("hours_lookback_source")
  public val hoursLookbackSource: String,
  @SerialName("max_posts_per_digest")
  public val maxPostsPerDigest: Long,
  @SerialName("max_posts_per_digest_source")
  public val maxPostsPerDigestSource: String,
  @SerialName("min_relevance_score")
  public val minRelevanceScore: Double,
  @SerialName("min_relevance_score_source")
  public val minRelevanceScoreSource: String,
)

@Serializable
@Immutable
public data class DigestPreferencesResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DigestPreferencesData? = null,
)

@Serializable
@Immutable
public data class DigestDelivery(
  @SerialName("id")
  public val id: Long,
  @SerialName("delivered_at")
  public val deliveredAt: Instant,
  @SerialName("post_count")
  public val postCount: Long,
  @SerialName("channel_count")
  public val channelCount: Long,
  @SerialName("digest_type")
  public val digestType: String,
)

@Serializable
@Immutable
public data class DigestHistoryData(
  @SerialName("deliveries")
  public val deliveries: List<DigestDelivery> = emptyList(),
  @SerialName("total")
  public val total: Long,
  @SerialName("limit")
  public val limit: Long,
  @SerialName("offset")
  public val offset: Long,
)

@Serializable
@Immutable
public data class DigestHistoryResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DigestHistoryData? = null,
)

@Serializable
@Immutable
public data class TriggerDigestData(
  @SerialName("status")
  public val status: String,
  @SerialName("correlation_id")
  public val correlationId: String,
)

@Serializable
@Immutable
public data class TriggerDigestResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TriggerDigestData? = null,
)

@Serializable
@Immutable
public data class TriggerChannelDigestData(
  @SerialName("status")
  public val status: String,
  @SerialName("channel")
  public val channel: String,
  @SerialName("correlation_id")
  public val correlationId: String,
)

@Serializable
@Immutable
public data class TriggerChannelDigestResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TriggerChannelDigestData? = null,
)

@Serializable
@Immutable
public data class ChannelCategory(
  @SerialName("id")
  public val id: Long,
  @SerialName("name")
  public val name: String,
  @SerialName("position")
  public val position: Long,
  @SerialName("subscription_count")
  public val subscriptionCount: Long,
)

@Serializable
@Immutable
public data class ChannelCategoryListData(
  @SerialName("categories")
  public val categories: List<ChannelCategory> = emptyList(),
)

@Serializable
@Immutable
public data class ChannelCategoryListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ChannelCategoryListData? = null,
)

@Serializable
@Immutable
public data class ChannelCategoryResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ChannelCategory? = null,
)

@Serializable
@Immutable
public data class StatusOnlyData(
  @SerialName("status")
  public val status: String,
)

@Serializable
@Immutable
public data class StatusOnlyResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: StatusOnlyData? = null,
)

@Serializable
@Immutable
public data class BulkOperationItem(
  @SerialName("username")
  public val username: String? = null,
  @SerialName("id")
  public val id: String? = null,
  @SerialName("status")
  public val status: String,
  @SerialName("detail")
  public val detail: String? = null,
)

@Serializable
@Immutable
public data class BulkOperationData(
  @SerialName("results")
  public val results: List<BulkOperationItem> = emptyList(),
  @SerialName("success_count")
  public val successCount: Long,
  @SerialName("error_count")
  public val errorCount: Long,
)

@Serializable
@Immutable
public data class BulkOperationResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: BulkOperationData? = null,
)

@Serializable
@Immutable
public data class ChannelPostAnalysis(
  @SerialName("real_topic")
  public val realTopic: String? = null,
  @SerialName("tldr")
  public val tldr: String? = null,
  @SerialName("relevance_score")
  public val relevanceScore: Double? = null,
  @SerialName("content_type")
  public val contentType: String? = null,
)

@Serializable
@Immutable
public data class ChannelPost(
  @SerialName("message_id")
  public val messageId: Long,
  @SerialName("text")
  public val text: String,
  @SerialName("date")
  public val date: Instant,
  @SerialName("views")
  public val views: Long? = null,
  @SerialName("forwards")
  public val forwards: Long? = null,
  @SerialName("media_type")
  public val mediaType: String? = null,
  @SerialName("url")
  public val url: String? = null,
  @SerialName("analysis")
  public val analysis: ChannelPostAnalysis? = null,
)

@Serializable
@Immutable
public data class ChannelPostListData(
  @SerialName("posts")
  public val posts: List<ChannelPost> = emptyList(),
  @SerialName("total")
  public val total: Long,
  @SerialName("channel_username")
  public val channelUsername: String,
)

@Serializable
@Immutable
public data class ChannelPostListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ChannelPostListData? = null,
)

@Serializable
@Immutable
public data class CustomDigest(
  @SerialName("id")
  public val id: String,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("content")
  public val content: String? = null,
  @SerialName("status")
  public val status: String,
  @SerialName("createdAt")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class CustomDigestResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CustomDigest? = null,
)

@Serializable
@Immutable
public data class CustomDigestListData(
  @SerialName("digests")
  public val digests: List<CustomDigest> = emptyList(),
)

@Serializable
@Immutable
public data class CustomDigestListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: CustomDigestListData? = null,
)

@Serializable
@Immutable
public data class Backup(
  @SerialName("id")
  public val id: Long,
  @SerialName("type")
  public val type: String,
  @SerialName("status")
  public val status: String,
  @SerialName("filePath")
  public val filePath: String? = null,
  @SerialName("fileSizeBytes")
  public val fileSizeBytes: Long? = null,
  @SerialName("itemsCount")
  public val itemsCount: Long? = null,
  @SerialName("error")
  public val error: String? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
)

@Serializable
@Immutable
public data class BackupResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Backup? = null,
)

@Serializable
@Immutable
public data class BackupListData(
  @SerialName("backups")
  public val backups: List<Backup> = emptyList(),
)

@Serializable
@Immutable
public data class BackupListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: BackupListData? = null,
)

@Serializable
@Immutable
public data class BackupSchedule(
  @SerialName("backup_enabled")
  public val backupEnabled: Boolean? = null,
  @SerialName("backup_frequency")
  public val backupFrequency: String? = null,
  @SerialName("backup_retention_count")
  public val backupRetentionCount: Long? = null,
)

@Serializable
@Immutable
public data class BackupScheduleData(
  @SerialName("schedule")
  public val schedule: BackupSchedule,
)

@Serializable
@Immutable
public data class BackupScheduleResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: BackupScheduleData? = null,
)

@Serializable
@Immutable
public data class BackupDeleteData(
  @SerialName("deleted")
  public val deleted: Boolean,
  @SerialName("id")
  public val id: Long,
)

@Serializable
@Immutable
public data class BackupDeleteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: BackupDeleteData? = null,
)

@Serializable
@Immutable
public data class BackupRestoreResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: BackupRestoreData? = null,
)

@Serializable
@Immutable
public data class ImportJobResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ImportJob? = null,
)

@Serializable
@Immutable
public data class ImportJobListData(
  @SerialName("jobs")
  public val jobs: List<ImportJob> = emptyList(),
)

@Serializable
@Immutable
public data class ImportJobListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ImportJobListData? = null,
)

@Serializable
@Immutable
public data class DeletedByIdData(
  @SerialName("deleted")
  public val deleted: Boolean,
  @SerialName("id")
  public val id: Long,
)

@Serializable
@Immutable
public data class DeletedByIdResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DeletedByIdData? = null,
)

@Serializable
@Immutable
public data class UserGoal(
  @SerialName("id")
  public val id: String,
  @SerialName("goalType")
  public val goalType: String,
  @SerialName("targetCount")
  public val targetCount: Long,
  @SerialName("scopeType")
  public val scopeType: String,
  @SerialName("scopeId")
  public val scopeId: Long? = null,
  @SerialName("scopeName")
  public val scopeName: String? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
)

@Serializable
@Immutable
public data class UserGoalResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: UserGoal? = null,
)

@Serializable
@Immutable
public data class UserGoalListData(
  @SerialName("goals")
  public val goals: List<UserGoal> = emptyList(),
)

@Serializable
@Immutable
public data class UserGoalListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: UserGoalListData? = null,
)

@Serializable
@Immutable
public data class UserGoalProgress(
  @SerialName("goalType")
  public val goalType: String,
  @SerialName("targetCount")
  public val targetCount: Long,
  @SerialName("currentCount")
  public val currentCount: Long,
  @SerialName("achieved")
  public val achieved: Boolean,
  @SerialName("scopeType")
  public val scopeType: String,
  @SerialName("scopeId")
  public val scopeId: Long? = null,
  @SerialName("scopeName")
  public val scopeName: String? = null,
)

@Serializable
@Immutable
public data class UserGoalProgressListData(
  @SerialName("progress")
  public val progress: List<UserGoalProgress> = emptyList(),
)

@Serializable
@Immutable
public data class UserGoalProgressListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: UserGoalProgressListData? = null,
)

@Serializable
@Immutable
public data class Streak(
  @SerialName("currentStreak")
  public val currentStreak: Long,
  @SerialName("longestStreak")
  public val longestStreak: Long,
  @SerialName("lastActivityDate")
  public val lastActivityDate: String? = null,
  @SerialName("todayCount")
  public val todayCount: Long,
  @SerialName("weekCount")
  public val weekCount: Long,
  @SerialName("monthCount")
  public val monthCount: Long,
)

@Serializable
@Immutable
public data class StreakResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Streak? = null,
)

@Serializable
@Immutable
public data class DeletedFlagData(
  @SerialName("deleted")
  public val deleted: Boolean,
)

@Serializable
@Immutable
public data class DeletedFlagResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DeletedFlagData? = null,
)

@Serializable
@Immutable
public data class Tag(
  @SerialName("id")
  public val id: Long,
  @SerialName("name")
  public val name: String,
  @SerialName("color")
  public val color: String? = null,
  @SerialName("summaryCount")
  public val summaryCount: Long,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
)

@Serializable
@Immutable
public data class TagResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Tag? = null,
)

@Serializable
@Immutable
public data class TagListData(
  @SerialName("tags")
  public val tags: List<Tag> = emptyList(),
)

@Serializable
@Immutable
public data class TagListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TagListData? = null,
)

@Serializable
@Immutable
public data class TagDeleteData(
  @SerialName("deleted")
  public val deleted: Boolean,
  @SerialName("id")
  public val id: Long,
)

@Serializable
@Immutable
public data class TagDeleteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TagDeleteData? = null,
)

@Serializable
@Immutable
public data class TagDetachData(
  @SerialName("detached")
  public val detached: Boolean,
  @SerialName("summary_id")
  public val summaryId: Long,
  @SerialName("tag_id")
  public val tagId: Long,
)

@Serializable
@Immutable
public data class TagDetachResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: TagDetachData? = null,
)

@Serializable
@Immutable
public data class MergeTagsData(
  @SerialName("merged")
  public val merged: Boolean,
  @SerialName("target_tag_id")
  public val targetTagId: Long,
)

@Serializable
@Immutable
public data class MergeTagsResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: MergeTagsData? = null,
)

@Serializable
@Immutable
public data class Rule(
  @SerialName("id")
  public val id: Long,
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("enabled")
  public val enabled: Boolean,
  @SerialName("eventType")
  public val eventType: String,
  @SerialName("matchMode")
  public val matchMode: String,
  @SerialName("conditions")
  public val conditions: List<JsonElement> = emptyList(),
  @SerialName("actions")
  public val actions: List<JsonElement> = emptyList(),
  @SerialName("priority")
  public val priority: Long,
  @SerialName("runCount")
  public val runCount: Long,
  @SerialName("lastTriggeredAt")
  public val lastTriggeredAt: Instant? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
)

@Serializable
@Immutable
public data class RuleResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Rule? = null,
)

@Serializable
@Immutable
public data class RuleListData(
  @SerialName("rules")
  public val rules: List<Rule> = emptyList(),
)

@Serializable
@Immutable
public data class RuleListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RuleListData? = null,
)

@Serializable
@Immutable
public data class RuleExecutionLog(
  @SerialName("id")
  public val id: Long,
  @SerialName("ruleId")
  public val ruleId: Long,
  @SerialName("summaryId")
  public val summaryId: Long? = null,
  @SerialName("eventType")
  public val eventType: String,
  @SerialName("matched")
  public val matched: Boolean,
  @SerialName("conditionsResult")
  public val conditionsResult: List<JsonElement>? = null,
  @SerialName("actionsTaken")
  public val actionsTaken: List<JsonElement>? = null,
  @SerialName("error")
  public val error: String? = null,
  @SerialName("durationMs")
  public val durationMs: Long? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class RuleLogListData(
  @SerialName("logs")
  public val logs: List<RuleExecutionLog> = emptyList(),
)

@Serializable
@Immutable
public data class RuleLogListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RuleLogListData? = null,
)

@Serializable
@Immutable
public data class RuleTestData(
  @SerialName("matched")
  public val matched: Boolean,
  @SerialName("conditions_result")
  public val conditionsResult: List<JsonElement> = emptyList(),
  @SerialName("would_execute_actions")
  public val wouldExecuteActions: List<JsonElement> = emptyList(),
)

@Serializable
@Immutable
public data class RuleTestResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RuleTestData? = null,
)

@Serializable
@Immutable
public data class RssFeedSubscription(
  @SerialName("subscription_id")
  public val subscriptionId: Long,
  @SerialName("feed_id")
  public val feedId: Long,
  @SerialName("feed_title")
  public val feedTitle: String? = null,
  @SerialName("feed_url")
  public val feedUrl: String? = null,
  @SerialName("site_url")
  public val siteUrl: String? = null,
  @SerialName("category_name")
  public val categoryName: String? = null,
  @SerialName("is_active")
  public val isActive: Boolean,
  @SerialName("created_at")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class RssFeedSubscriptionListData(
  @SerialName("feeds")
  public val feeds: List<RssFeedSubscription> = emptyList(),
)

@Serializable
@Immutable
public data class RssFeedSubscriptionListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RssFeedSubscriptionListData? = null,
)

@Serializable
@Immutable
public data class RssSubscribeData(
  @SerialName("subscription_id")
  public val subscriptionId: Long,
  @SerialName("feed_id")
  public val feedId: Long,
  @SerialName("feed_title")
  public val feedTitle: String? = null,
  @SerialName("feed_url")
  public val feedUrl: String? = null,
)

@Serializable
@Immutable
public data class RssSubscribeResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RssSubscribeData? = null,
)

@Serializable
@Immutable
public data class RssFeedItem(
  @SerialName("id")
  public val id: Long,
  @SerialName("guid")
  public val guid: String? = null,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("url")
  public val url: String? = null,
  @SerialName("author")
  public val author: String? = null,
  @SerialName("published_at")
  public val publishedAt: Instant? = null,
  @SerialName("created_at")
  public val createdAt: Instant,
)

@Serializable
@Immutable
public data class RssFeedItemListData(
  @SerialName("feed_id")
  public val feedId: Long,
  @SerialName("items")
  public val items: List<RssFeedItem> = emptyList(),
)

@Serializable
@Immutable
public data class RssFeedItemListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RssFeedItemListData? = null,
)

@Serializable
@Immutable
public data class RssFeedRefreshData(
  @SerialName("feed_id")
  public val feedId: Long,
  @SerialName("new_items")
  public val newItems: Long,
  @SerialName("not_modified")
  public val notModified: Boolean? = null,
)

@Serializable
@Immutable
public data class RssFeedRefreshResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: RssFeedRefreshData? = null,
)

@Serializable
@Immutable
public data class OPMLImportData(
  @SerialName("imported")
  public val imported: Long,
  @SerialName("errors")
  public val errors: Long,
  @SerialName("total")
  public val total: Long,
)

@Serializable
@Immutable
public data class OPMLImportResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: OPMLImportData? = null,
)

@Serializable
@Immutable
public data class Highlight(
  @SerialName("id")
  public val id: String,
  @SerialName("summaryId")
  public val summaryId: String,
  @SerialName("text")
  public val text: String,
  @SerialName("startOffset")
  public val startOffset: Long? = null,
  @SerialName("endOffset")
  public val endOffset: Long? = null,
  @SerialName("color")
  public val color: String? = null,
  @SerialName("note")
  public val note: String? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("updatedAt")
  public val updatedAt: Instant,
)

@Serializable
@Immutable
public data class HighlightResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Highlight? = null,
)

@Serializable
@Immutable
public data class HighlightListData(
  @SerialName("highlights")
  public val highlights: List<Highlight> = emptyList(),
)

@Serializable
@Immutable
public data class HighlightListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: HighlightListData? = null,
)

@Serializable
@Immutable
public data class HighlightDeleteData(
  @SerialName("deleted")
  public val deleted: Boolean,
  @SerialName("id")
  public val id: String,
)

@Serializable
@Immutable
public data class HighlightDeleteResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: HighlightDeleteData? = null,
)

@Serializable
@Immutable
public data class SummaryAudio(
  @SerialName("summaryId")
  public val summaryId: Long,
  @SerialName("status")
  public val status: String,
  @SerialName("charCount")
  public val charCount: Long? = null,
  @SerialName("fileSizeBytes")
  public val fileSizeBytes: Long? = null,
  @SerialName("latencyMs")
  public val latencyMs: Long? = null,
  @SerialName("error")
  public val error: String? = null,
)

@Serializable
@Immutable
public data class SummaryAudioResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: SummaryAudio? = null,
)

@Serializable
@Immutable
public data class QuickSaveData(
  @SerialName("request_id")
  public val requestId: Long? = null,
  /**
   * "pending" for new submissions, "duplicate" for already-saved URLs.
   */
  @SerialName("status")
  public val status: String,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("url")
  public val url: String,
  @SerialName("duplicate")
  public val duplicate: Boolean,
  @SerialName("summary_id")
  public val summaryId: Long? = null,
  @SerialName("tags_attached")
  public val tagsAttached: List<String>? = null,
)

@Serializable
@Immutable
public data class QuickSaveResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: QuickSaveData? = null,
)

@Serializable
@Immutable
public data class RootResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("message")
    public val message: String? = null,
  )
}

@Serializable
@Immutable
public data class HealthResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    /**
     * Example: ok
     */
    @SerialName("status")
    public val status: String? = null,
  )
}

/**
 * Metadata for forwarded Telegram message.
 */
@Serializable
@Immutable
public data class ForwardMetadata(
  /**
   * Telegram chat ID where the message was forwarded from.
   */
  @SerialName("from_chat_id")
  public val fromChatId: Long,
  /**
   * Telegram message ID of the forwarded message.
   */
  @SerialName("from_message_id")
  public val fromMessageId: Long,
  /**
   * Title of the source chat (if available).
   */
  @SerialName("from_chat_title")
  public val fromChatTitle: String? = null,
  /**
   * ISO 8601 timestamp when message was forwarded.
   */
  @SerialName("forwarded_at")
  public val forwardedAt: String? = null,
)

@Serializable
@Immutable
public data class HTTPValidationError(
  @SerialName("detail")
  public val detail: List<ValidationError>? = null,
)

/**
 * Request body for token refresh. The refresh_token field is optional when using httpOnly cookie transport.
 */
@Serializable
@Immutable
public data class RefreshTokenRequest(
  @SerialName("refresh_token")
  public val refreshToken: String? = null,
)

/**
 * Request body for submitting a forwarded message.
 */
@Serializable
@Immutable
@SerialName("SubmitForwardRequest")
public data class SubmitForwardRequest(
  @SerialName("type")
  public val type: String? = null,
  @SerialName("content_text")
  public val contentText: String,
  @SerialName("forward_metadata")
  public val forwardMetadata: ForwardMetadata,
  @SerialName("lang_preference")
  public val langPreference: LangPreference? = null,
) : V1RequestsRequest {
  @Serializable
  @Immutable
  public enum class LangPreference {
    @SerialName("auto")
    AUTO,
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
  }
}

/**
 * Request body for submitting a URL.
 */
@Serializable
@Immutable
@SerialName("SubmitURLRequest")
public data class SubmitURLRequest(
  /**
   * Request type (always 'url' for URL submissions).
   */
  @SerialName("type")
  public val type: String? = null,
  /**
   * URL to summarize (must be http:// or https://).
   */
  @SerialName("input_url")
  public val inputUrl: String,
  /**
   * Preferred language for the summary.
   */
  @SerialName("lang_preference")
  public val langPreference: LangPreference? = null,
) : V1RequestsRequest {
  /**
   * Preferred language for the summary.
   */
  @Serializable
  @Immutable
  public enum class LangPreference {
    @SerialName("auto")
    AUTO,
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
  }
}

/**
 * Single change to upload during sync.
 */
@Serializable
@Immutable
public data class SyncApplyItem(
  @SerialName("entity_type")
  public val entityType: EntityType,
  @SerialName("id")
  public val id: String,
  @SerialName("action")
  public val action: Action,
  @SerialName("last_seen_version")
  public val lastSeenVersion: Long,
  @SerialName("payload")
  public val payload: JsonElement? = null,
  @SerialName("client_timestamp")
  public val clientTimestamp: String? = null,
) {
  @Serializable
  @Immutable
  public enum class EntityType {
    @SerialName("summary")
    SUMMARY,
    @SerialName("request")
    REQUEST,
    @SerialName("preference")
    PREFERENCE,
    @SerialName("stat")
    STAT,
    @SerialName("crawl_result")
    CRAWL_RESULT,
    @SerialName("llm_call")
    LLM_CALL,
  }

  @Serializable
  @Immutable
  public enum class Action {
    @SerialName("update")
    UPDATE,
    @SerialName("delete")
    DELETE,
  }
}

/**
 * Request body for applying local changes.
 */
@Serializable
@Immutable
public data class SyncApplyRequest(
  @SerialName("session_id")
  public val sessionId: String,
  @SerialName("changes")
  public val changes: List<SyncApplyItem> = emptyList(),
  /**
   * Optional client-generated key (UUID is fine) for safe retries.
   * When set, a duplicate apply with the same (session_id,
   * idempotency_key) within ~5 minutes returns the original response
   * without re-applying any changes. Lets a client retry after a
   * network failure without risking double-apply.
   */
  @SerialName("idempotency_key")
  public val idempotencyKey: String? = null,
)

/**
 * Session creation options.
 */
@Serializable
@Immutable
public data class SyncSessionRequest(
  @SerialName("limit")
  public val limit: Long? = null,
)

/**
 * Request body for Telegram login.
 */
@Serializable
@Immutable
public data class TelegramLoginRequest(
  @SerialName("id")
  public val id: Long,
  @SerialName("hash")
  public val hash: String,
  @SerialName("auth_date")
  public val authDate: Long,
  @SerialName("username")
  public val username: String? = null,
  @SerialName("first_name")
  public val firstName: String? = null,
  @SerialName("last_name")
  public val lastName: String? = null,
  @SerialName("photo_url")
  public val photoUrl: String? = null,
  /**
   * Client application ID (e.g., 'android-app-v1.0', 'ios-app-v2.0')
   */
  @SerialName("client_id")
  public val clientId: String,
)

/**
 * Request body for updating user preferences.
 */
@Serializable
@Immutable
public data class UpdatePreferencesRequest(
  @SerialName("lang_preference")
  public val langPreference: LangPreference? = null,
  @SerialName("notification_settings")
  public val notificationSettings: JsonElement? = null,
  @SerialName("app_settings")
  public val appSettings: JsonElement? = null,
) {
  @Serializable
  @Immutable
  public enum class LangPreference {
    @SerialName("auto")
    AUTO,
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
  }
}

/**
 * Request body for updating a summary.
 */
@Serializable
@Immutable
public data class UpdateSummaryRequest(
  @SerialName("is_read")
  public val isRead: Boolean? = null,
)

@Serializable
@Immutable
public data class ValidationError(
  @SerialName("loc")
  public val loc: List<String> = emptyList(),
  @SerialName("msg")
  public val msg: String,
  @SerialName("type")
  public val type: String,
)

@Serializable
@Immutable
public data class SessionInfo(
  @SerialName("id")
  public val id: Long,
  @SerialName("clientId")
  public val clientId: String? = null,
  @SerialName("deviceInfo")
  public val deviceInfo: String? = null,
  @SerialName("ipAddress")
  public val ipAddress: String? = null,
  @SerialName("lastUsedAt")
  public val lastUsedAt: Instant? = null,
  @SerialName("createdAt")
  public val createdAt: Instant,
  @SerialName("isCurrent")
  public val isCurrent: Boolean? = null,
)

@Serializable
@Immutable
public data class SessionListResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: Data? = null,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("sessions")
    public val sessions: List<SessionInfo> = emptyList(),
  )
}

@Serializable
@Immutable
public data class BaseResponse(
  /**
   * Status indicator (e.g., "ok").
   */
  @SerialName("status")
  public val status: String,
)

@Serializable
@Immutable
public data class HealthComponentStatus(
  @SerialName("status")
  public val status: Status? = null,
  @SerialName("latency_ms")
  public val latencyMs: Double? = null,
  @SerialName("details")
  public val details: JsonElement? = null,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("healthy")
    HEALTHY,
    @SerialName("unhealthy")
    UNHEALTHY,
    @SerialName("degraded")
    DEGRADED,
    @SerialName("unknown")
    UNKNOWN,
  }
}

@Serializable
@Immutable
public data class DetailedHealthData(
  @SerialName("status")
  public val status: Status,
  @SerialName("timestamp")
  public val timestamp: Instant,
  @SerialName("components")
  public val components: Components? = null,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("healthy")
    HEALTHY,
    @SerialName("unhealthy")
    UNHEALTHY,
    @SerialName("degraded")
    DEGRADED,
  }

  @Serializable
  @Immutable
  public data class Components(
    @SerialName("database")
    public val database: HealthComponentStatus? = null,
    @SerialName("redis")
    public val redis: HealthComponentStatus? = null,
    @SerialName("circuit_breakers")
    public val circuitBreakers: HealthComponentStatus? = null,
  )
}

@Serializable
@Immutable
public data class DetailedHealthResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: DetailedHealthData? = null,
)

@Serializable
@Immutable
public data class ReadinessData(
  @SerialName("ready")
  public val ready: Boolean,
  @SerialName("timestamp")
  public val timestamp: Instant,
)

@Serializable
@Immutable
public data class ReadinessResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: ReadinessData? = null,
)

@Serializable
@Immutable
public data class LivenessData(
  @SerialName("alive")
  public val alive: Boolean,
  @SerialName("timestamp")
  public val timestamp: Instant,
)

@Serializable
@Immutable
public data class LivenessResponseEnvelope(
  @SerialName("success")
  public val success: Boolean? = null,
  @SerialName("meta")
  public val meta: Meta? = null,
  @SerialName("data")
  public val `data`: LivenessData? = null,
)

/**
 * Request body for ingesting a GitHub repository by URL.
 */
@Serializable
@Immutable
public data class IngestRepositoryRequest(
  /**
   * github.com repository URL (any of the forms accepted by app.adapters.github.url_patterns.is_github_repo_url).
   */
  @SerialName("url")
  public val url: String,
)

/**
 * Sort order for repository list.
 */
@Serializable
@Immutable
public enum class RepositoryListSort {
  @SerialName("stars_desc")
  STARS_DESC,
  @SerialName("pushed_desc")
  PUSHED_DESC,
  @SerialName("created_desc")
  CREATED_DESC,
  @SerialName("full_name_asc")
  FULL_NAME_ASC,
}

/**
 * Mirror of RepoAnalysis schema for API serialization.
 */
@Serializable
@Immutable
public data class RepositoryAnalysis(
  @SerialName("purpose")
  public val purpose: String,
  @SerialName("tech_stack")
  public val techStack: List<String> = emptyList(),
  @SerialName("architecture_summary")
  public val architectureSummary: String,
  /**
   * List of "{term, explanation}" objects.
   */
  @SerialName("key_concepts")
  public val keyConcepts: List<JsonElement> = emptyList(),
  /**
   * List of "{name, description}" objects.
   */
  @SerialName("code_patterns")
  public val codePatterns: List<JsonElement> = emptyList(),
  @SerialName("use_cases")
  public val useCases: List<String> = emptyList(),
  @SerialName("target_audience")
  public val targetAudience: String,
  @SerialName("maturity")
  public val maturity: String,
  @SerialName("key_dependencies")
  public val keyDependencies: List<String> = emptyList(),
  @SerialName("hallucination_risk")
  public val hallucinationRisk: String,
  @SerialName("confidence")
  public val confidence: Double,
)

@Serializable
@Immutable
public data class RepositoryCompact(
  @SerialName("id")
  public val id: Long,
  @SerialName("github_id")
  public val githubId: Long,
  @SerialName("full_name")
  public val fullName: String,
  @SerialName("owner")
  public val owner: String,
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("primary_language")
  public val primaryLanguage: String? = null,
  @SerialName("topics")
  public val topics: List<String>? = null,
  @SerialName("stars")
  public val stars: Long,
  @SerialName("forks")
  public val forks: Long,
  @SerialName("is_starred")
  public val isStarred: Boolean,
  @SerialName("is_archived")
  public val isArchived: Boolean,
  @SerialName("pushed_at")
  public val pushedAt: Instant? = null,
  @SerialName("last_synced_at")
  public val lastSyncedAt: Instant,
  @SerialName("pending_analysis")
  public val pendingAnalysis: Boolean,
  @SerialName("has_analysis")
  public val hasAnalysis: Boolean,
  /**
   * One of "manual" or "starred".
   */
  @SerialName("source")
  public val source: String,
)

@Serializable
@Immutable
public data class RepositoryDetail(
  @SerialName("id")
  public val id: Long,
  @SerialName("github_id")
  public val githubId: Long,
  @SerialName("full_name")
  public val fullName: String,
  @SerialName("owner")
  public val owner: String,
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("primary_language")
  public val primaryLanguage: String? = null,
  @SerialName("topics")
  public val topics: List<String>? = null,
  @SerialName("stars")
  public val stars: Long,
  @SerialName("forks")
  public val forks: Long,
  @SerialName("is_starred")
  public val isStarred: Boolean,
  @SerialName("is_archived")
  public val isArchived: Boolean,
  @SerialName("pushed_at")
  public val pushedAt: Instant? = null,
  @SerialName("last_synced_at")
  public val lastSyncedAt: Instant,
  @SerialName("pending_analysis")
  public val pendingAnalysis: Boolean,
  @SerialName("has_analysis")
  public val hasAnalysis: Boolean,
  /**
   * One of "manual" or "starred".
   */
  @SerialName("source")
  public val source: String,
  @SerialName("homepage_url")
  public val homepageUrl: String? = null,
  @SerialName("license_spdx")
  public val licenseSpdx: String? = null,
  @SerialName("is_fork")
  public val isFork: Boolean,
  @SerialName("is_template")
  public val isTemplate: Boolean,
  @SerialName("languages")
  public val languages: Map<String, Long>? = null,
  @SerialName("readme_excerpt")
  public val readmeExcerpt: String? = null,
  @SerialName("analysis")
  public val analysis: RepositoryAnalysis? = null,
  @SerialName("analysis_model")
  public val analysisModel: String? = null,
  @SerialName("analysis_at")
  public val analysisAt: Instant? = null,
  @SerialName("content_hash")
  public val contentHash: String? = null,
  @SerialName("created_at_github")
  public val createdAtGithub: Instant? = null,
  @SerialName("watchers")
  public val watchers: Long,
)

@Serializable
@Immutable
public data class RepositoryListResponse(
  @SerialName("repositories")
  public val repositories: List<RepositoryCompact> = emptyList(),
  @SerialName("pagination")
  public val pagination: Pagination,
)

@Serializable
@Immutable
public data class IngestRepositoryResponse(
  @SerialName("repository_id")
  public val repositoryId: Long,
  @SerialName("status")
  public val status: Status,
  @SerialName("full_name")
  public val fullName: String,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("pending")
    PENDING,
    @SerialName("ready")
    READY,
  }
}

@Serializable
@Immutable
public data class RepositorySearchHit(
  @SerialName("id")
  public val id: Long,
  @SerialName("github_id")
  public val githubId: Long,
  @SerialName("full_name")
  public val fullName: String,
  @SerialName("owner")
  public val owner: String,
  @SerialName("name")
  public val name: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("primary_language")
  public val primaryLanguage: String? = null,
  @SerialName("topics")
  public val topics: List<String>? = null,
  @SerialName("stars")
  public val stars: Long,
  @SerialName("forks")
  public val forks: Long,
  @SerialName("is_starred")
  public val isStarred: Boolean,
  @SerialName("is_archived")
  public val isArchived: Boolean,
  @SerialName("pushed_at")
  public val pushedAt: Instant? = null,
  @SerialName("last_synced_at")
  public val lastSyncedAt: Instant,
  @SerialName("pending_analysis")
  public val pendingAnalysis: Boolean,
  @SerialName("has_analysis")
  public val hasAnalysis: Boolean,
  @SerialName("source")
  public val source: String,
  /**
   * Cosine similarity distance from the query embedding.
   */
  @SerialName("distance")
  public val distance: Double,
)

@Serializable
@Immutable
public data class RepositorySearchResponse(
  @SerialName("results")
  public val results: List<RepositorySearchHit> = emptyList(),
  @SerialName("pagination")
  public val pagination: Pagination,
  @SerialName("query")
  public val query: String,
)

@Serializable
@Immutable
public data class PATSubmitRequest(
  /**
   * GitHub Personal Access Token (classic or fine-grained).
   */
  @SerialName("token")
  public val token: String,
)

@Serializable
@Immutable
public data class PATSubmitResponse(
  @SerialName("login")
  public val login: String,
  @SerialName("github_user_id")
  public val githubUserId: Long,
  /**
   * Always "pat" for this endpoint.
   */
  @SerialName("auth_method")
  public val authMethod: String,
  /**
   * Integration status (e.g. "active").
   */
  @SerialName("status")
  public val status: String,
)

@Serializable
@Immutable
public data class GitHubStatusResponse(
  @SerialName("is_connected")
  public val isConnected: Boolean,
  /**
   * One of "pat" or "oauth_device" when connected.
   */
  @SerialName("auth_method")
  public val authMethod: String? = null,
  @SerialName("github_login")
  public val githubLogin: String? = null,
  @SerialName("github_user_id")
  public val githubUserId: Long? = null,
  /**
   * Integration status (e.g. "active", "needs_reauth", "revoked").
   */
  @SerialName("status")
  public val status: String? = null,
  @SerialName("last_synced_at")
  public val lastSyncedAt: Instant? = null,
  @SerialName("repo_count")
  public val repoCount: Long,
)

@Serializable
@Immutable
public data class DeviceFlowStartResponse(
  /**
   * Short user-visible code to enter at verification_uri.
   */
  @SerialName("user_code")
  public val userCode: String,
  /**
   * URL where the user enters the user_code.
   */
  @SerialName("verification_uri")
  public val verificationUri: String,
  /**
   * Long device code; pass back to /v1/auth/github/device/poll.
   */
  @SerialName("device_code")
  public val deviceCode: String,
  /**
   * Minimum interval (seconds) between polls.
   */
  @SerialName("interval")
  public val interval: Long,
  /**
   * Seconds until the device_code expires.
   */
  @SerialName("expires_in")
  public val expiresIn: Long,
)

@Serializable
@Immutable
public data class DeviceFlowPollRequest(
  @SerialName("device_code")
  public val deviceCode: String,
)

@Serializable
@Immutable
public data class DeviceFlowPollResponse(
  @SerialName("status")
  public val status: Status,
  @SerialName("login")
  public val login: String? = null,
  @SerialName("github_user_id")
  public val githubUserId: Long? = null,
  /**
   * Always "oauth_device" on success.
   */
  @SerialName("auth_method")
  public val authMethod: String? = null,
  @SerialName("integration_status")
  public val integrationStatus: String? = null,
) {
  @Serializable
  @Immutable
  public enum class Status {
    @SerialName("pending")
    PENDING,
    @SerialName("slow_down")
    SLOW_DOWN,
    @SerialName("expired")
    EXPIRED,
    @SerialName("ok")
    OK,
    @SerialName("denied")
    DENIED,
  }
}

@Serializable
@Immutable
public enum class GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField {
  @SerialName("summary_250")
  SUMMARY_250,
  @SerialName("summary_1000")
  SUMMARY_1000,
  @SerialName("tldr")
  TLDR,
}

@Serializable
@Immutable
public enum class ListSecretKeysV1AuthSecretKeysGetStatus {
  @SerialName("active")
  ACTIVE,
  @SerialName("revoked")
  REVOKED,
  @SerialName("expired")
  EXPIRED,
  @SerialName("locked")
  LOCKED,
}

@Serializable
@Immutable
public enum class GetSummaryContentV1SummariesSummaryIdContentGetFormat {
  @SerialName("markdown")
  MARKDOWN,
  @SerialName("text")
  TEXT,
}

/**
 * Request payload with discriminator on type
 */
@Serializable
@Immutable
@JsonClassDiscriminator("type")
public sealed interface V1RequestsRequest

@Serializable
@Immutable
public enum class ListAggregationBundlesV1AggregationsGetStatus {
  @SerialName("pending")
  PENDING,
  @SerialName("processing")
  PROCESSING,
  @SerialName("completed")
  COMPLETED,
  @SerialName("partial")
  PARTIAL,
  @SerialName("failed")
  FAILED,
  @SerialName("cancelled")
  CANCELLED,
}

@Serializable
@Immutable
public data class V1Aggregations200Response(
  @SerialName("success")
  public val success: Boolean,
  @SerialName("data")
  public val `data`: Data,
  @SerialName("meta")
  public val meta: Meta,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("session")
    public val session: Session? = null,
    @SerialName("aggregation")
    public val aggregation: JsonElement? = null,
    @SerialName("items")
    public val items: List<ItemsItem>? = null,
  ) {
    @Serializable
    @Immutable
    public data class Session(
      @SerialName("sessionId")
      public val sessionId: Long? = null,
      @SerialName("correlationId")
      public val correlationId: String? = null,
      @SerialName("status")
      public val status: String? = null,
      @SerialName("sourceType")
      public val sourceType: String? = null,
      @SerialName("successfulCount")
      public val successfulCount: Long? = null,
      @SerialName("failedCount")
      public val failedCount: Long? = null,
      @SerialName("duplicateCount")
      public val duplicateCount: Long? = null,
      @SerialName("processingTimeMs")
      public val processingTimeMs: Long? = null,
      @SerialName("queuedAt")
      public val queuedAt: String? = null,
      @SerialName("startedAt")
      public val startedAt: String? = null,
      @SerialName("completedAt")
      public val completedAt: String? = null,
      @SerialName("lastProgressAt")
      public val lastProgressAt: String? = null,
      @SerialName("progress")
      public val progress: JsonElement? = null,
      @SerialName("failure")
      public val failure: JsonElement? = null,
    )

    @Serializable
    @Immutable
    public data class ItemsItem(
      @SerialName("position")
      public val position: Long? = null,
      @SerialName("itemId")
      public val itemId: Long? = null,
      @SerialName("sourceItemId")
      public val sourceItemId: String? = null,
      @SerialName("sourceKind")
      public val sourceKind: String? = null,
      @SerialName("status")
      public val status: String? = null,
      @SerialName("requestId")
      public val requestId: Long? = null,
      @SerialName("failure")
      public val failure: JsonElement? = null,
    )
  }
}

@Serializable
@Immutable
public data class V1AggregationsRequest(
  @SerialName("items")
  public val items: List<ItemsItem> = emptyList(),
  @SerialName("lang_preference")
  public val langPreference: LangPreference? = null,
  @SerialName("metadata")
  public val metadata: JsonElement? = null,
) {
  @Serializable
  @Immutable
  public data class ItemsItem(
    @SerialName("type")
    public val type: String? = null,
    @SerialName("url")
    public val url: String,
    @SerialName("source_kind_hint")
    public val sourceKindHint: SourceKindHint? = null,
    @SerialName("metadata")
    public val metadata: Map<String, JsonElement>? = null,
  ) {
    @Serializable
    @Immutable
    public enum class SourceKindHint {
      @SerialName("x_post")
      X_POST,
      @SerialName("x_article")
      X_ARTICLE,
      @SerialName("threads_post")
      THREADS_POST,
      @SerialName("instagram_post")
      INSTAGRAM_POST,
      @SerialName("instagram_carousel")
      INSTAGRAM_CAROUSEL,
      @SerialName("instagram_reel")
      INSTAGRAM_REEL,
      @SerialName("web_article")
      WEB_ARTICLE,
      @SerialName("telegram_post")
      TELEGRAM_POST,
      @SerialName("youtube_video")
      YOUTUBE_VIDEO,
    }
  }

  @Serializable
  @Immutable
  public enum class LangPreference {
    @SerialName("auto")
    AUTO,
    @SerialName("en")
    EN,
    @SerialName("ru")
    RU,
  }
}

@Serializable
@Immutable
public data class V1AggregationsSessionId200Response(
  @SerialName("success")
  public val success: Boolean,
  @SerialName("data")
  public val `data`: Data,
  @SerialName("meta")
  public val meta: Meta,
) {
  @Serializable
  @Immutable
  public data class Data(
    @SerialName("session")
    public val session: JsonElement? = null,
    @SerialName("items")
    public val items: List<JsonElement>? = null,
    @SerialName("aggregation")
    public val aggregation: JsonElement? = null,
  )
}

@Serializable
@Immutable
public data class V1DigestChannelsSubscribeRequest(
  @SerialName("channel_username")
  public val channelUsername: String,
)

@Serializable
@Immutable
public data class V1DigestChannelsUnsubscribeRequest(
  @SerialName("channel_username")
  public val channelUsername: String,
)

@Serializable
@Immutable
public data class V1DigestPreferencesRequest(
  @SerialName("delivery_time")
  public val deliveryTime: String? = null,
  @SerialName("timezone")
  public val timezone: String? = null,
  @SerialName("hours_lookback")
  public val hoursLookback: Long? = null,
  @SerialName("max_posts_per_digest")
  public val maxPostsPerDigest: Long? = null,
  @SerialName("min_relevance_score")
  public val minRelevanceScore: Double? = null,
)

@Serializable
@Immutable
public data class V1DigestTriggerChannelRequest(
  @SerialName("channel_username")
  public val channelUsername: String,
)

@Serializable
@Immutable
public data class V1DigestChannelsResolveRequest(
  @SerialName("channel_username")
  public val channelUsername: String,
)

@Serializable
@Immutable
public data class V1DigestChannelsBulkUnsubscribeRequest(
  @SerialName("channel_usernames")
  public val channelUsernames: List<String> = emptyList(),
)

@Serializable
@Immutable
public data class V1DigestChannelsBulkCategoryRequest(
  @SerialName("subscription_ids")
  public val subscriptionIds: List<Long> = emptyList(),
  @SerialName("category_id")
  public val categoryId: Long? = null,
)

@Serializable
@Immutable
public data class V1DigestChannelsSubscriptionIdCategoryRequest(
  @SerialName("category_id")
  public val categoryId: Long? = null,
)

@Serializable
@Immutable
public data class V1DigestCategoriesRequest(
  @SerialName("name")
  public val name: String,
)

@Serializable
@Immutable
public data class V1DigestCategoriesCategoryIdRequest(
  @SerialName("name")
  public val name: String,
)

@Serializable
@Immutable
public enum class GetArticleContentV1ArticlesSummaryIdContentGetFormat {
  @SerialName("markdown")
  MARKDOWN,
  @SerialName("text")
  TEXT,
}

@Serializable
@Immutable
public data class V1RssFeedsSubscribeRequest(
  @SerialName("url")
  public val url: String,
  @SerialName("category_id")
  public val categoryId: Long? = null,
)

@Serializable
@Immutable
public data class V1RssImportOpmlRequest(
  @SerialName("file")
  public val `file`: String,
)

@Serializable
@Immutable
public data class V1TagsRequest(
  @SerialName("name")
  public val name: String,
  @SerialName("color")
  public val color: String? = null,
)

@Serializable
@Immutable
public data class V1TagsTagIdRequest(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("color")
  public val color: String? = null,
)

@Serializable
@Immutable
public data class V1TagsMergeRequest(
  @SerialName("source_tag_ids")
  public val sourceTagIds: List<Long> = emptyList(),
  @SerialName("target_tag_id")
  public val targetTagId: Long,
)

@Serializable
@Immutable
public data class V1SummariesSummaryIdTagsRequest(
  @SerialName("tag_ids")
  public val tagIds: List<Long>? = null,
  @SerialName("tag_names")
  public val tagNames: List<String>? = null,
)

@Serializable
@Immutable
public data class V1WebhooksRequest(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("url")
  public val url: String,
  @SerialName("events")
  public val events: List<String> = emptyList(),
)

@Serializable
@Immutable
public data class V1WebhooksWebhookIdRequest(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("url")
  public val url: String? = null,
  @SerialName("events")
  public val events: List<String>? = null,
  @SerialName("enabled")
  public val enabled: Boolean? = null,
)

@Serializable
@Immutable
public data class V1RulesRequest(
  @SerialName("name")
  public val name: String,
  @SerialName("event_type")
  public val eventType: String,
  @SerialName("conditions")
  public val conditions: List<JsonElement>? = null,
  @SerialName("actions")
  public val actions: List<JsonElement> = emptyList(),
  @SerialName("match_mode")
  public val matchMode: String? = null,
  @SerialName("priority")
  public val priority: Long? = null,
  @SerialName("description")
  public val description: String? = null,
)

@Serializable
@Immutable
public data class V1RulesRuleIdRequest(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("event_type")
  public val eventType: String? = null,
  @SerialName("conditions")
  public val conditions: List<JsonElement>? = null,
  @SerialName("actions")
  public val actions: List<JsonElement>? = null,
  @SerialName("match_mode")
  public val matchMode: String? = null,
  @SerialName("priority")
  public val priority: Long? = null,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("enabled")
  public val enabled: Boolean? = null,
)

@Serializable
@Immutable
public data class V1RulesRuleIdTestRequest(
  @SerialName("summary_id")
  public val summaryId: Long,
)

@Serializable
@Immutable
public data class V1BackupsScheduleRequest(
  @SerialName("backup_enabled")
  public val backupEnabled: Boolean? = null,
  @SerialName("backup_frequency")
  public val backupFrequency: String? = null,
  @SerialName("backup_retention_count")
  public val backupRetentionCount: Long? = null,
)

@Serializable
@Immutable
public data class V1BackupsRestoreRequest(
  @SerialName("file")
  public val `file`: String,
)

@Serializable
@Immutable
public enum class ExportBookmarksV1ExportGetFormat {
  @SerialName("json")
  JSON,
  @SerialName("csv")
  CSV,
  @SerialName("html")
  HTML,
}

@Serializable
@Immutable
public data class V1ImportRequest(
  @SerialName("file")
  public val `file`: String,
  /**
   * JSON-encoded import options
   */
  @SerialName("options")
  public val options: String? = null,
)

@Serializable
@Immutable
public data class V1UserGoalsRequest(
  @SerialName("goal_type")
  public val goalType: GoalType,
  @SerialName("target_count")
  public val targetCount: Long,
  @SerialName("scope_type")
  public val scopeType: ScopeType? = null,
  @SerialName("scope_id")
  public val scopeId: Long? = null,
) {
  @Serializable
  @Immutable
  public enum class GoalType {
    @SerialName("daily")
    DAILY,
    @SerialName("weekly")
    WEEKLY,
    @SerialName("monthly")
    MONTHLY,
  }

  @Serializable
  @Immutable
  public enum class ScopeType {
    @SerialName("global")
    GLOBAL,
    @SerialName("tag")
    TAG,
    @SerialName("collection")
    COLLECTION,
  }
}

@Serializable
@Immutable
public data class V1SummariesSummaryIdHighlightsRequest(
  @SerialName("text")
  public val text: String,
  @SerialName("start_offset")
  public val startOffset: Long? = null,
  @SerialName("end_offset")
  public val endOffset: Long? = null,
  @SerialName("color")
  public val color: String? = null,
  @SerialName("note")
  public val note: String? = null,
)

@Serializable
@Immutable
public data class V1SummariesSummaryIdHighlightsHighlightIdRequest(
  @SerialName("color")
  public val color: String? = null,
  @SerialName("note")
  public val note: String? = null,
)

@Serializable
@Immutable
public data class V1SummariesSummaryIdReadingPositionRequest(
  @SerialName("progress")
  public val progress: Double,
  @SerialName("last_read_offset")
  public val lastReadOffset: Long? = null,
)

@Serializable
@Immutable
public data class V1SummariesSummaryIdFeedbackRequest(
  @SerialName("rating")
  public val rating: Long? = null,
  @SerialName("issues")
  public val issues: List<String>? = null,
  @SerialName("comment")
  public val comment: String? = null,
)

@Serializable
@Immutable
public enum class ExportSummaryV1SummariesSummaryIdExportGetFormat {
  @SerialName("pdf")
  PDF,
  @SerialName("md")
  MD,
  @SerialName("html")
  HTML,
}

@Serializable
@Immutable
public data class V1ArticlesSummaryIdReadingPositionRequest(
  @SerialName("progress")
  public val progress: Double,
  @SerialName("last_read_offset")
  public val lastReadOffset: Long? = null,
)

@Serializable
@Immutable
public data class V1ArticlesSummaryIdFeedbackRequest(
  @SerialName("rating")
  public val rating: Long? = null,
  @SerialName("issues")
  public val issues: List<String>? = null,
  @SerialName("comment")
  public val comment: String? = null,
)

@Serializable
@Immutable
public enum class ExportSummaryV1ArticlesSummaryIdExportGetFormat {
  @SerialName("pdf")
  PDF,
  @SerialName("md")
  MD,
  @SerialName("html")
  HTML,
}

@Serializable
@Immutable
public data class V1DigestsCustomRequest(
  @SerialName("summary_ids")
  public val summaryIds: List<String> = emptyList(),
  @SerialName("format")
  public val format: String? = null,
  @SerialName("title")
  public val title: String? = null,
)

@Serializable
@Immutable
public data class V1QuickSaveRequest(
  @SerialName("url")
  public val url: String,
  @SerialName("title")
  public val title: String? = null,
  @SerialName("selected_text")
  public val selectedText: String? = null,
  @SerialName("tag_names")
  public val tagNames: List<String>? = null,
  @SerialName("summarize")
  public val summarize: Boolean? = null,
)

@Serializable
@Immutable
public enum class ListRepositoriesV1RepositoriesGetSource {
  @SerialName("manual")
  MANUAL,
  @SerialName("starred")
  STARRED,
}

@Serializable
@Immutable
public enum class SearchRepositoriesV1SearchRepositoriesGetSource {
  @SerialName("manual")
  MANUAL,
  @SerialName("starred")
  STARRED,
}

public typealias SignalItem = JsonElement

public typealias SignalSourceHealth = JsonElement

public typealias FullSyncItem = SyncEntityEnvelope

/**
 * Summary of items restored from an uploaded backup archive. Extra fields
 * may be present depending on which entity types the archive contained.
 */
public typealias BackupRestoreData = JsonElement

/**
 * Asynchronous import job tracking record. Snake_case keys match the
 * backend Pydantic ImportJobResponse model serialization.
 */
public typealias ImportJob = JsonElement
