/* 
 * NOTE: This file is auto generated. Do not edit the file manually!
 * 
 * Ratatoskr Mobile API
 * RESTful API for Android/iOS mobile clients
 * Version 1.0.0
 * 
 * Generated Tue, 12 May 2026 20:56:58 +0400
 * OpenAPI KMP Gen (version 1.3.0) by kroegerama
 */
@file:Suppress("ArrayInDataClass", "RedundantVisibilityModifier", "unused", "ConstPropertyName")

package com.po4yka.ratatoskr.api.generated.api

import arrow.core.Either
import com.kroegerama.openapi.kmp.gen.`companion`.AuthPlugin.Plugin.authKeys
import com.kroegerama.openapi.kmp.gen.`companion`.CallException
import com.kroegerama.openapi.kmp.gen.`companion`.HttpCallResponse
import com.kroegerama.openapi.kmp.gen.`companion`.appendSerializedHeaderParameter
import com.kroegerama.openapi.kmp.gen.`companion`.appendSerializedQueryParameter
import com.kroegerama.openapi.kmp.gen.`companion`.createSerializedPathSegment
import com.kroegerama.openapi.kmp.gen.`companion`.eitherRequest
import com.po4yka.ratatoskr.api.generated.Api
import com.po4yka.ratatoskr.api.generated.Auth
import com.po4yka.ratatoskr.api.generated.models.AuthTokensResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.BaseResponse
import com.po4yka.ratatoskr.api.generated.models.ChangePasswordRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionAclResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionCreateRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionInviteRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionInviteResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionItemCreateRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionItemMoveRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionItemReorderRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionItemsMoveResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionItemsResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionListResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionMoveRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionMoveResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionReorderRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionReorderResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionShareRequest
import com.po4yka.ratatoskr.api.generated.models.CollectionTreeResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.CollectionUpdateRequest
import com.po4yka.ratatoskr.api.generated.models.CredentialsLoginRequest
import com.po4yka.ratatoskr.api.generated.models.DeltaSyncResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.DetailedHealthResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.DeviceFlowPollRequest
import com.po4yka.ratatoskr.api.generated.models.DeviceFlowPollResponse
import com.po4yka.ratatoskr.api.generated.models.DeviceFlowStartResponse
import com.po4yka.ratatoskr.api.generated.models.DeviceRegistrationPayload
import com.po4yka.ratatoskr.api.generated.models.DuplicateUrlCheckResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.ExportBookmarksV1ExportGetFormat
import com.po4yka.ratatoskr.api.generated.models.ExportSummaryV1ArticlesSummaryIdExportGetFormat
import com.po4yka.ratatoskr.api.generated.models.ExportSummaryV1SummariesSummaryIdExportGetFormat
import com.po4yka.ratatoskr.api.generated.models.FullSyncResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField
import com.po4yka.ratatoskr.api.generated.models.GetArticleContentV1ArticlesSummaryIdContentGetFormat
import com.po4yka.ratatoskr.api.generated.models.GetSummaryContentV1SummariesSummaryIdContentGetFormat
import com.po4yka.ratatoskr.api.generated.models.GitHubStatusResponse
import com.po4yka.ratatoskr.api.generated.models.HealthResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.IngestRepositoryRequest
import com.po4yka.ratatoskr.api.generated.models.IngestRepositoryResponse
import com.po4yka.ratatoskr.api.generated.models.ListAggregationBundlesV1AggregationsGetStatus
import com.po4yka.ratatoskr.api.generated.models.ListRepositoriesV1RepositoriesGetSource
import com.po4yka.ratatoskr.api.generated.models.ListSecretKeysV1AuthSecretKeysGetStatus
import com.po4yka.ratatoskr.api.generated.models.LivenessResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.LoginResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.PATSubmitRequest
import com.po4yka.ratatoskr.api.generated.models.PATSubmitResponse
import com.po4yka.ratatoskr.api.generated.models.PaginatedSummariesResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.ReadinessResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.RefreshTokenRequest
import com.po4yka.ratatoskr.api.generated.models.RelatedTopicsResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.RepositoryDetail
import com.po4yka.ratatoskr.api.generated.models.RepositoryListResponse
import com.po4yka.ratatoskr.api.generated.models.RepositoryListSort
import com.po4yka.ratatoskr.api.generated.models.RepositorySearchResponse
import com.po4yka.ratatoskr.api.generated.models.RequestDetailResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.RequestRetryResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.RequestStatusResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.RootResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SearchRepositoriesV1SearchRepositoriesGetSource
import com.po4yka.ratatoskr.api.generated.models.SearchResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SecretKeyActionResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SecretKeyCreateRequest
import com.po4yka.ratatoskr.api.generated.models.SecretKeyCreateResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SecretKeyListResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SecretKeyRevokeRequest
import com.po4yka.ratatoskr.api.generated.models.SecretKeyRotateRequest
import com.po4yka.ratatoskr.api.generated.models.SecretLoginRequest
import com.po4yka.ratatoskr.api.generated.models.SessionListResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalFeedbackRequest
import com.po4yka.ratatoskr.api.generated.models.SignalFeedbackResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalHealthResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalListResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalSourceActiveResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalSourceHealthResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SignalTopicResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SourceActiveRequest
import com.po4yka.ratatoskr.api.generated.models.SubmitRequestResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SuccessMessageEnvelope
import com.po4yka.ratatoskr.api.generated.models.SuccessResponse
import com.po4yka.ratatoskr.api.generated.models.SummaryContentResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SummaryDeleteResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SummaryDetailResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SummaryUpdateResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SyncApplyRequest
import com.po4yka.ratatoskr.api.generated.models.SyncApplyResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.SyncSessionRequest
import com.po4yka.ratatoskr.api.generated.models.SyncSessionResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.TelegramLinkBeginResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.TelegramLinkCompleteRequest
import com.po4yka.ratatoskr.api.generated.models.TelegramLinkStatusEnvelope
import com.po4yka.ratatoskr.api.generated.models.TelegramLoginRequest
import com.po4yka.ratatoskr.api.generated.models.ToggleFavoriteResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.TopicPreferenceRequest
import com.po4yka.ratatoskr.api.generated.models.TrendingTopicsResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.UpdatePreferencesRequest
import com.po4yka.ratatoskr.api.generated.models.UpdateSummaryRequest
import com.po4yka.ratatoskr.api.generated.models.UserPreferencesResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.UserResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.UserStatsResponseEnvelope
import com.po4yka.ratatoskr.api.generated.models.V1Aggregations200Response
import com.po4yka.ratatoskr.api.generated.models.V1AggregationsRequest
import com.po4yka.ratatoskr.api.generated.models.V1AggregationsSessionId200Response
import com.po4yka.ratatoskr.api.generated.models.V1ArticlesSummaryIdFeedbackRequest
import com.po4yka.ratatoskr.api.generated.models.V1ArticlesSummaryIdReadingPositionRequest
import com.po4yka.ratatoskr.api.generated.models.V1BackupsScheduleRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestCategories200Response
import com.po4yka.ratatoskr.api.generated.models.V1DigestCategoriesCategoryIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestCategoriesRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsBulkCategoryRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsBulkUnsubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsResolveRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsSubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsSubscriptionIdCategoryRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestChannelsUnsubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestPreferencesRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestTriggerChannelRequest
import com.po4yka.ratatoskr.api.generated.models.V1DigestsCustomRequest
import com.po4yka.ratatoskr.api.generated.models.V1QuickSaveRequest
import com.po4yka.ratatoskr.api.generated.models.V1RequestsRequest
import com.po4yka.ratatoskr.api.generated.models.V1RssFeedsSubscribeRequest
import com.po4yka.ratatoskr.api.generated.models.V1RulesRequest
import com.po4yka.ratatoskr.api.generated.models.V1RulesRuleIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1RulesRuleIdTestRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdFeedbackRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdHighlightsHighlightIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdHighlightsRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdReadingPositionRequest
import com.po4yka.ratatoskr.api.generated.models.V1SummariesSummaryIdTagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsMergeRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsRequest
import com.po4yka.ratatoskr.api.generated.models.V1TagsTagIdRequest
import com.po4yka.ratatoskr.api.generated.models.V1UserGoalsRequest
import com.po4yka.ratatoskr.api.generated.models.V1WebhooksRequest
import com.po4yka.ratatoskr.api.generated.models.V1WebhooksWebhookIdRequest
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Double
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlinx.serialization.json.JsonElement

public object AuthenticationApi {
  /**
   * **Telegram Login**
   *
   * `POST /v1/auth/telegram-login`
   *
   * Exchange Telegram authentication data for JWT tokens.
   *
   * Verifies Telegram auth hash using HMAC-SHA256 and returns access + refresh tokens.
   *
   * The authentication data must come from Telegram Login Widget and include:
   * - id: Telegram user ID
   * - hash: HMAC-SHA256 hash of auth data
   * - auth_date: Unix timestamp of authentication
   * - client_id: Client application ID
   * - Optional: username, first_name, last_name, photo_url
   *
   * @return Login success
   */
  public suspend fun telegramLoginV1AuthTelegramLoginPost(body: TelegramLoginRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<LoginResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    url.appendPathSegments(
      "v1",
      "auth",
      "telegram-login",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Refresh Access Token**
   *
   * `POST /v1/auth/refresh`
   *
   * Refresh an expired access token using a refresh token.
   *
   * @return Tokens refreshed
   */
  public suspend fun refreshAccessTokenV1AuthRefreshPost(body: RefreshTokenRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<AuthTokensResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    url.appendPathSegments(
      "v1",
      "auth",
      "refresh",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Logout**
   *
   * `POST /v1/auth/logout`
   *
   * Revoke the specified refresh token.
   *
   * @return Logout successful
   */
  public suspend fun logoutV1AuthLogoutPost(body: RefreshTokenRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "logout",
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Active Sessions**
   *
   * `GET /v1/auth/sessions`
   *
   * List all active refresh token sessions for the current user.
   *
   * @return List of sessions
   */
  public suspend fun listSessionsV1AuthSessionsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SessionListResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "sessions",
    )
    decorator()
  }

  /**
   * **Get Current User Info**
   *
   * `GET /v1/auth/me`
   *
   * Get current authenticated user information.
   *
   * @return Current user
   */
  public suspend fun getCurrentUserInfoV1AuthMeGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<UserResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
    )
    decorator()
  }

  /**
   * **Delete Account**
   *
   * `DELETE /v1/auth/me`
   *
   * Delete the current user account and all associated data.
   *
   * @return Account deleted
   */
  public suspend fun deleteAccountV1AuthMeDelete(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
    )
    decorator()
  }

  /**
   * **Secret Key Login**
   *
   * `POST /v1/auth/secret-login`
   *
   * Exchange a pre-registered client secret for JWT tokens.
   *
   * @return Login success
   */
  public suspend fun secretLoginV1AuthSecretLoginPost(body: SecretLoginRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<AuthTokensResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    url.appendPathSegments(
      "v1",
      "auth",
      "secret-login",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Credentials Login (nickname/email + password)**
   *
   * `POST /v1/auth/credentials-login`
   *
   * Exchange nickname/email + password for JWT tokens. Bootstrap is
   * CLI-only (`ratatoskr credentials set`) — no public signup. Route
   * is gated on `CREDENTIALS_LOGIN_PEPPER` presence; when unset, returns
   * 503 Configuration error. `remember_me=True` issues a 30-day refresh
   * token; `False` issues a short-lived (default 12h) refresh and a
   * session-only cookie (no Max-Age).
   *
   * @return Login success
   */
  public suspend fun credentialsLoginV1AuthCredentialsLoginPost(body: CredentialsLoginRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<AuthTokensResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    url.appendPathSegments(
      "v1",
      "auth",
      "credentials-login",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Change password (owner)**
   *
   * `POST /v1/auth/credentials/change-password`
   *
   * Change the current user's password. Requires the current password.
   * Returns generic 401 on any failure (unknown user, mismatched current
   * password, locked-out row) to avoid disclosing whether password auth
   * is set up for the caller.
   *
   * @return Password changed
   */
  public suspend fun changePasswordV1AuthCredentialsChangePasswordPost(body: ChangePasswordRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SuccessMessageEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "credentials",
      "change-password",
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Client Secrets (owner-only)**
   *
   * `GET /v1/auth/secret-keys`
   *
   * List client secrets with optional filters.
   *
   * @return Secrets list
   */
  public suspend fun listSecretKeysV1AuthSecretKeysGet(
    userId: Long? = null,
    clientId: String? = null,
    status: ListSecretKeysV1AuthSecretKeysGetStatus? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SecretKeyListResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "secret-keys",
    )
    appendSerializedQueryParameter(name = "user_id", value = userId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "client_id", value = clientId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "status", value = status, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Create Client Secret (owner-only)**
   *
   * `POST /v1/auth/secret-keys`
   *
   * Create or register a client secret for a user. Returns plaintext secret once.
   *
   * @return Secret created
   */
  public suspend fun createSecretKeyV1AuthSecretKeysPost(body: SecretKeyCreateRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SecretKeyCreateResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "secret-keys",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Rotate Client Secret (owner-only)**
   *
   * `POST /v1/auth/secret-keys/{key_id}/rotate`
   *
   * Rotate an existing client secret; returns plaintext secret once.
   *
   * @return Secret rotated
   */
  public suspend fun rotateSecretKeyV1AuthSecretKeysKeyIdRotatePost(
    keyId: Long,
    body: SecretKeyRotateRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SecretKeyCreateResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "secret-keys",
      createSerializedPathSegment(value = keyId, explode = false, json = Api.json),
      "rotate",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Revoke Client Secret (owner-only)**
   *
   * `POST /v1/auth/secret-keys/{key_id}/revoke`
   *
   * Revoke an existing client secret.
   *
   * @return Secret revoked
   */
  public suspend fun revokeSecretKeyV1AuthSecretKeysKeyIdRevokePost(
    keyId: Long,
    body: SecretKeyRevokeRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SecretKeyActionResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "secret-keys",
      createSerializedPathSegment(value = keyId, explode = false, json = Api.json),
      "revoke",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Telegram Link Status**
   *
   * `GET /v1/auth/me/telegram`
   *
   * Return current Telegram linkage state for the authenticated user.
   *
   * @return Link status
   */
  public suspend fun getTelegramLinkStatusV1AuthMeTelegramGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<TelegramLinkStatusEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
      "telegram",
    )
    decorator()
  }

  /**
   * **Unlink Telegram Account**
   *
   * `DELETE /v1/auth/me/telegram`
   *
   * Remove Telegram linkage for the authenticated user.
   *
   * @return Unlinked
   */
  public suspend fun unlinkTelegramV1AuthMeTelegramDelete(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<TelegramLinkStatusEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
      "telegram",
    )
    decorator()
  }

  /**
   * **Begin Telegram Linking**
   *
   * `POST /v1/auth/me/telegram/link`
   *
   * Issue a nonce to start Telegram linking via Telegram Login Widget.
   *
   * @return Nonce issued
   */
  public suspend fun beginTelegramLinkV1AuthMeTelegramLinkPost(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<TelegramLinkBeginResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
      "telegram",
      "link",
    )
    decorator()
  }

  /**
   * **Complete Telegram Linking**
   *
   * `POST /v1/auth/me/telegram/complete`
   *
   * Validate Telegram login payload and nonce to complete linking.
   *
   * @return Linked
   */
  public suspend fun completeTelegramLinkV1AuthMeTelegramCompletePost(body: TelegramLinkCompleteRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<TelegramLinkStatusEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "me",
      "telegram",
      "complete",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Revoke Session**
   *
   * `DELETE /v1/auth/sessions/{session_id}`
   *
   * Revoke a specific session by ID.
   *
   * @return Session revoked
   */
  public suspend fun revokeSessionV1AuthSessionsSessionIdDelete(sessionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "sessions",
      createSerializedPathSegment(value = sessionId, explode = false, json = Api.json),
    )
    decorator()
  }
}

public object SummariesApi {
  /**
   * **Toggle favorite status**
   *
   * `POST /v1/summaries/{summary_id}/favorite`
   *
   * @return Favorite status updated
   */
  public suspend fun toggleFavorite(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<ToggleFavoriteResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "favorite",
    )
    decorator()
  }

  /**
   * **Get Summary Audio**
   *
   * `GET /v1/summaries/{summary_id}/audio`
   *
   * Stream or download the generated audio file for a summary. Returns audio/mpeg.
   *
   * @return Audio file stream
   */
  public suspend fun getSummaryAudioV1SummariesSummaryIdAudioGet(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "audio",
    )
    decorator()
  }

  /**
   * **Generate Summary Audio**
   *
   * `POST /v1/summaries/{summary_id}/audio`
   *
   * Trigger audio generation for a summary. Returns immediately with status if already cached, otherwise generates on-demand.
   *
   * @return Audio generation result
   */
  public suspend fun generateSummaryAudioV1SummariesSummaryIdAudioPost(
    summaryId: Long,
    sourceField: GenerateSummaryAudioV1SummariesSummaryIdAudioPostSourceField? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "audio",
    )
    appendSerializedQueryParameter(name = "source_field", value = sourceField, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Summaries**
   *
   * `GET /v1/summaries`
   *
   * Get paginated list of summaries.
   *
   * Query Parameters:
   * - limit: Items per page (1-100, default 20)
   * - offset: Pagination offset (default 0)
   * - is_read: Filter by read status (optional)
   * - lang: Filter by language (en/ru/auto)
   * - start_date: Filter by creation date (ISO 8601)
   * - end_date: Filter by creation date (ISO 8601)
   * - sort: Sort order (created_at_desc/created_at_asc)
   *
   * @return Paginated summaries
   */
  public suspend fun getSummariesV1SummariesGet(
    limit: Long? = null,
    offset: Long? = null,
    isRead: Boolean? = null,
    isFavorited: Boolean? = null,
    lang: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    sort: String? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<PaginatedSummariesResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_read", value = isRead, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_favorited", value = isFavorited, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "lang", value = lang, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "start_date", value = startDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "end_date", value = endDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "sort", value = sort, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Summary**
   *
   * `GET /v1/summaries/{summary_id}`
   *
   * Get a single summary with full details.
   *
   * @return Summary detail
   */
  public suspend fun getSummaryV1SummariesSummaryIdGet(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDetailResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Summary**
   *
   * `DELETE /v1/summaries/{summary_id}`
   *
   * Delete a summary (soft delete).
   *
   * @return Summary deleted
   */
  public suspend fun deleteSummaryV1SummariesSummaryIdDelete(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDeleteResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Summary**
   *
   * `PATCH /v1/summaries/{summary_id}`
   *
   * Update summary metadata (e.g., mark as read).
   *
   * @return Summary updated
   */
  public suspend fun updateSummaryV1SummariesSummaryIdPatch(
    summaryId: Long,
    body: UpdateSummaryRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SummaryUpdateResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Summary Content**
   *
   * `GET /v1/summaries/{summary_id}/content`
   *
   * Get full article content for offline reading (Markdown by default).
   *
   * @return Summary content
   */
  public suspend fun getSummaryContentV1SummariesSummaryIdContentGet(
    summaryId: Long,
    format: GetSummaryContentV1SummariesSummaryIdContentGetFormat? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SummaryContentResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "content",
    )
    appendSerializedQueryParameter(name = "format", value = format, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Summary by URL**
   *
   * `GET /v1/summaries/by-url`
   *
   * Look up a summary by the original article URL.
   *
   * @param url Original URL of the article
   * @return Summary detail
   */
  public suspend fun getSummaryByUrlV1SummariesByUrlGet(reqUrl: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDetailResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      "by-url",
    )
    appendSerializedQueryParameter(name = "url", value = reqUrl, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Save Reading Position**
   *
   * `PATCH /v1/summaries/{summary_id}/reading-position`
   *
   * Save the reading position (scroll progress) for a summary.
   *
   * @return Reading position saved
   */
  public suspend fun saveReadingPositionV1SummariesSummaryIdReadingPositionPatch(
    summaryId: Long,
    body: V1SummariesSummaryIdReadingPositionRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "reading-position",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Submit Feedback**
   *
   * `POST /v1/summaries/{summary_id}/feedback`
   *
   * Submit or update feedback for a summary.
   *
   * @return Feedback submitted
   */
  public suspend fun submitFeedbackV1SummariesSummaryIdFeedbackPost(
    summaryId: Long,
    body: V1SummariesSummaryIdFeedbackRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "feedback",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Export Summary**
   *
   * `GET /v1/summaries/{summary_id}/export`
   *
   * Export a summary as PDF, Markdown, or HTML.
   *
   * @return Exported file
   */
  public suspend fun exportSummaryV1SummariesSummaryIdExportGet(
    summaryId: Long,
    format: ExportSummaryV1SummariesSummaryIdExportGetFormat? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "export",
    )
    appendSerializedQueryParameter(name = "format", value = format, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Recommendations**
   *
   * `GET /v1/summaries/recommendations`
   *
   * Get personalized summary recommendations based on reading history.
   *
   * @return List of recommended summaries
   */
  public suspend fun getRecommendationsV1SummariesRecommendationsGet(limit: Long? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      "recommendations",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    decorator()
  }
}

public object ProxyApi {
  /**
   * **Proxy Image**
   *
   * `GET /v1/proxy/image`
   *
   * Proxy an image from a remote URL to bypass CORS/hotlink protection.
   *
   * @param url URL of the image to proxy
   * @return Image content stream
   */
  public suspend fun proxyImage(reqUrl: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "v1",
      "proxy",
      "image",
    )
    appendSerializedQueryParameter(name = "url", value = reqUrl, explode = true, json = Api.json)
    decorator()
  }
}

public object NotificationsApi {
  /**
   * **Register mobile device**
   *
   * `POST /v1/notifications/device`
   *
   * Register a device token (FCM/APNS) for push notifications.
   *
   * @return Device registered successfully
   */
  public suspend fun registerDevice(body: DeviceRegistrationPayload, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<BaseResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "notifications",
      "device",
    )
    setBody(body)
    decorator()
  }
}

public object CollectionsApi {
  /**
   * **List collections**
   *
   * `GET /v1/collections`
   *
   * @return List of user collections
   */
  public suspend fun getCollections(
    parentId: Long? = null,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionListResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
    )
    appendSerializedQueryParameter(name = "parent_id", value = parentId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Create collection**
   *
   * `POST /v1/collections`
   *
   * @return Collection created
   */
  public suspend fun createCollection(body: CollectionCreateRequest? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<CollectionResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get collection details**
   *
   * `GET /v1/collections/{collection_id}`
   *
   * @return Collection details
   */
  public suspend fun getCollection(collectionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<CollectionResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete collection**
   *
   * `DELETE /v1/collections/{collection_id}`
   *
   * @return Collection deleted
   */
  public suspend fun deleteCollection(collectionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update collection**
   *
   * `PATCH /v1/collections/{collection_id}`
   *
   * @return Collection updated
   */
  public suspend fun updateCollection(
    collectionId: Long,
    body: CollectionUpdateRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **List items in collection**
   *
   * `GET /v1/collections/{collection_id}/items`
   *
   * @return Items listed
   */
  public suspend fun listCollectionItems(
    collectionId: Long,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionItemsResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "items",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Add item to collection**
   *
   * `POST /v1/collections/{collection_id}/items`
   *
   * @return Item added
   */
  public suspend fun addCollectionItem(
    collectionId: Long,
    body: CollectionItemCreateRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "items",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Remove item from collection**
   *
   * `DELETE /v1/collections/{collection_id}/items/{summary_id}`
   *
   * @return Item removed
   */
  public suspend fun removeCollectionItem(
    collectionId: Long,
    summaryId: Long,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "items",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **List collections as tree**
   *
   * `GET /v1/collections/tree`
   *
   * @return Collection tree
   */
  public suspend fun getCollectionTree(maxDepth: Long? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<CollectionTreeResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      "tree",
    )
    appendSerializedQueryParameter(name = "max_depth", value = maxDepth, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get collection ACL**
   *
   * `GET /v1/collections/{collection_id}/acl`
   *
   * @return ACL entries
   */
  public suspend fun getCollectionAcl(collectionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<CollectionAclResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "acl",
    )
    decorator()
  }

  /**
   * **Add collaborator**
   *
   * `POST /v1/collections/{collection_id}/share`
   *
   * @return Collaborator added
   */
  public suspend fun addCollectionCollaborator(
    collectionId: Long,
    body: CollectionShareRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "share",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Remove collaborator**
   *
   * `DELETE /v1/collections/{collection_id}/share/{target_user_id}`
   *
   * @return Collaborator removed
   */
  public suspend fun removeCollectionCollaborator(
    collectionId: Long,
    targetUserId: Long,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "share",
      createSerializedPathSegment(value = targetUserId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Create invite token**
   *
   * `POST /v1/collections/{collection_id}/invite`
   *
   * @return Invite created
   */
  public suspend fun createCollectionInvite(
    collectionId: Long,
    body: CollectionInviteRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionInviteResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "invite",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Accept collection invite**
   *
   * `POST /v1/collections/invites/{token}/accept`
   *
   * @return Invite accepted
   */
  public suspend fun acceptCollectionInvite(token: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SuccessResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      "invites",
      createSerializedPathSegment(value = token, explode = false, json = Api.json),
      "accept",
    )
    decorator()
  }

  /**
   * **Reorder child collections**
   *
   * `POST /v1/collections/{collection_id}/reorder`
   *
   * @return Reordered
   */
  public suspend fun reorderCollections(
    collectionId: Long,
    body: CollectionReorderRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionReorderResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "reorder",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Reorder collection items**
   *
   * `POST /v1/collections/{collection_id}/items/reorder`
   *
   * @return Items reordered
   */
  public suspend fun reorderCollectionItems(
    collectionId: Long,
    body: CollectionItemReorderRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionReorderResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "items",
      "reorder",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Move collection to another parent**
   *
   * `POST /v1/collections/{collection_id}/move`
   *
   * @return Collection moved
   */
  public suspend fun moveCollection(
    collectionId: Long,
    body: CollectionMoveRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionMoveResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "move",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Move items to another collection**
   *
   * `POST /v1/collections/{collection_id}/items/move`
   *
   * @return Items moved
   */
  public suspend fun moveCollectionItems(
    collectionId: Long,
    body: CollectionItemMoveRequest? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<CollectionItemsMoveResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "items",
      "move",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Evaluate Smart Collection**
   *
   * `POST /v1/collections/{collection_id}/evaluate`
   *
   * Force re-evaluation of a smart collection's items.
   *
   * @return Evaluation result
   */
  public suspend fun evaluateSmartCollectionV1CollectionsCollectionIdEvaluatePost(collectionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "collections",
      createSerializedPathSegment(value = collectionId, explode = false, json = Api.json),
      "evaluate",
    )
    decorator()
  }
}

public object RequestsApi {
  /**
   * **Submit Request**
   *
   * `POST /v1/requests`
   *
   * Submit a new URL or forwarded message for processing.
   *
   * Returns request_id and correlation_id for status polling.
   * Checks for duplicates and returns existing summary if found.
   * Processing happens asynchronously in the background.
   *
   * @return Request accepted
   */
  public suspend fun submitRequestV1RequestsPost(body: V1RequestsRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SubmitRequestResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "requests",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Request**
   *
   * `GET /v1/requests/{request_id}`
   *
   * Get details about a specific request.
   *
   * @return Request detail
   */
  public suspend fun getRequestV1RequestsRequestIdGet(requestId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RequestDetailResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "requests",
      createSerializedPathSegment(value = requestId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Get Request Status**
   *
   * `GET /v1/requests/{request_id}/status`
   *
   * Poll for real-time processing status.
   *
   * @return Request status
   */
  public suspend fun getRequestStatusV1RequestsRequestIdStatusGet(requestId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RequestStatusResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "requests",
      createSerializedPathSegment(value = requestId, explode = false, json = Api.json),
      "status",
    )
    decorator()
  }

  /**
   * **Retry Request**
   *
   * `POST /v1/requests/{request_id}/retry`
   *
   * Retry a failed request. Processes asynchronously in the background.
   *
   * @return Request retried
   */
  public suspend fun retryRequestV1RequestsRequestIdRetryPost(requestId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RequestRetryResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "requests",
      createSerializedPathSegment(value = requestId, explode = false, json = Api.json),
      "retry",
    )
    decorator()
  }
}

public object StreamsApi {
  /**
   * **Stream summary progress events as Server-Sent Events**
   *
   * `GET /v1/requests/{request_id}/stream`
   *
   * Returns a Server-Sent Events (SSE) stream of progress events for the given
   * request. The connection stays open until the underlying summarization reaches
   * a terminal state ('done' or 'error') or the client disconnects.
   *
   * Events use the standard SSE format. The 'event:' field is one of
   * 'phase' | 'section' | 'done' | 'error'; the 'data:' field is a JSON object
   * whose 'kind' matches the event field, with payload schemas as documented
   * below (StreamPhaseEvent, StreamSectionEvent, StreamDoneEvent, StreamErrorEvent).
   * The server emits a comment-line ': keepalive' heartbeat every 15s on idle.
   *
   * @param requestId Numeric request id; the authenticated user must own this request.
   * @return SSE stream open. See StreamPhaseEvent / StreamSectionEvent / StreamDoneEvent / StreamErrorEvent for payload shapes.
   */
  public suspend fun streamRequest(requestId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "requests",
      createSerializedPathSegment(value = requestId, explode = false, json = Api.json),
      "stream",
    )
    decorator()
  }
}

public object AggregationsApi {
  /**
   * **List Aggregation Bundles**
   *
   * `GET /v1/aggregations`
   *
   * Return recent persisted aggregation sessions for the authenticated user.
   *
   * @return Aggregation bundle list
   */
  public suspend fun listAggregationBundlesV1AggregationsGet(
    limit: Long? = null,
    offset: Long? = null,
    status: ListAggregationBundlesV1AggregationsGetStatus? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<V1Aggregations200Response>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "aggregations",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "status", value = status, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Create Aggregation Bundle**
   *
   * `POST /v1/aggregations`
   *
   * Execute mixed-source aggregation for one submitted bundle of 1-25 URL items. This endpoint is blocking and returns the final persisted session snapshot on success.
   *
   * @return Aggregation bundle completed or partially completed
   */
  public suspend fun createAggregationBundleV1AggregationsPost(body: V1AggregationsRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<V1Aggregations200Response>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "aggregations",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Aggregation Bundle**
   *
   * `GET /v1/aggregations/{session_id}`
   *
   * Return one persisted aggregation session with bundle items and synthesized output.
   *
   * @return Aggregation bundle detail
   */
  public suspend fun getAggregationBundleV1AggregationsSessionIdGet(sessionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<V1AggregationsSessionId200Response>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "aggregations",
      createSerializedPathSegment(value = sessionId, explode = false, json = Api.json),
    )
    decorator()
  }
}

public object SearchApi {
  /**
   * **Search Summaries**
   *
   * `GET /v1/search`
   *
   * Full-text search across all summaries using FTS5.
   *
   * Search Syntax:
   * - Wildcard: bitcoin*
   * - Phrase: "artificial intelligence"
   * - Boolean: blockchain AND crypto
   * - Exclusion: crypto NOT bitcoin
   * @param startDate ISO date (YYYY-MM-DD)
   * @param endDate ISO date (YYYY-MM-DD)
   *
   * @return Search results
   */
  public suspend fun searchSummariesV1SearchGet(
    q: String,
    limit: Long? = null,
    offset: Long? = null,
    mode: String? = null,
    language: String? = null,
    tags: List<String>? = null,
    domains: List<String>? = null,
    startDate: String? = null,
    endDate: String? = null,
    isRead: Boolean? = null,
    isFavorited: Boolean? = null,
    minSimilarity: Double? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SearchResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "search",
    )
    appendSerializedQueryParameter(name = "q", value = q, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "mode", value = mode, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "language", value = language, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "tags", value = tags, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "domains", value = domains, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "start_date", value = startDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "end_date", value = endDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_read", value = isRead, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_favorited", value = isFavorited, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "min_similarity", value = minSimilarity, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Semantic Search Summaries**
   *
   * `GET /v1/search/semantic`
   *
   * Semantic search across summaries using Qdrant embeddings.
   * @param startDate ISO date (YYYY-MM-DD)
   * @param endDate ISO date (YYYY-MM-DD)
   *
   * @return Semantic search results
   */
  public suspend fun semanticSearchSummariesV1SearchSemanticGet(
    q: String,
    limit: Long? = null,
    offset: Long? = null,
    language: String? = null,
    tags: List<String>? = null,
    domains: List<String>? = null,
    startDate: String? = null,
    endDate: String? = null,
    isRead: Boolean? = null,
    isFavorited: Boolean? = null,
    userScope: String? = null,
    minSimilarity: Double? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SearchResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "search",
      "semantic",
    )
    appendSerializedQueryParameter(name = "q", value = q, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "language", value = language, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "tags", value = tags, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "domains", value = domains, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "start_date", value = startDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "end_date", value = endDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_read", value = isRead, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_favorited", value = isFavorited, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "user_scope", value = userScope, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "min_similarity", value = minSimilarity, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Search Insights**
   *
   * `GET /v1/search/insights`
   *
   * Search analytics snapshot including trends, entities, diversity, topic mix and coverage gaps.
   *
   * @return Search insights data
   */
  public suspend fun getSearchInsightsV1SearchInsightsGet(
    days: Long? = null,
    limit: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SearchResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "search",
      "insights",
    )
    appendSerializedQueryParameter(name = "days", value = days, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Trending Topics**
   *
   * `GET /v1/topics/trending`
   *
   * Get trending topic tags across recent summaries.
   *
   * @return Trending topics
   */
  public suspend fun getTrendingTopicsV1TopicsTrendingGet(
    limit: Long? = null,
    days: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<TrendingTopicsResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "topics",
      "trending",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "days", value = days, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Related Summaries**
   *
   * `GET /v1/topics/related`
   *
   * Get summaries related to a specific topic tag.
   *
   * @return Related summaries
   */
  public suspend fun getRelatedSummariesV1TopicsRelatedGet(
    tag: String,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<RelatedTopicsResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "topics",
      "related",
    )
    appendSerializedQueryParameter(name = "tag", value = tag, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Check Duplicate**
   *
   * `GET /v1/urls/check-duplicate`
   *
   * Check if a URL has already been summarized.
   *
   * @return Duplicate check result
   */
  public suspend fun checkDuplicateV1UrlsCheckDuplicateGet(
    reqUrl: String,
    includeSummary: Boolean? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<DuplicateUrlCheckResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "urls",
      "check-duplicate",
    )
    appendSerializedQueryParameter(name = "url", value = reqUrl, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "include_summary", value = includeSummary, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Search Repositories**
   *
   * `GET /v1/search/repositories`
   *
   * Semantic search over GitHub repositories using Qdrant embeddings, filtered by optional language/topic/source/starred flags.
   *
   * @return Repository search results
   */
  public suspend fun searchRepositoriesV1SearchRepositoriesGet(
    q: String,
    limit: Long? = null,
    offset: Long? = null,
    minSimilarity: Double? = null,
    languages: List<String>? = null,
    topics: List<String>? = null,
    isStarred: Boolean? = null,
    source: SearchRepositoriesV1SearchRepositoriesGetSource? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<RepositorySearchResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "search",
      "repositories",
    )
    appendSerializedQueryParameter(name = "q", value = q, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "min_similarity", value = minSimilarity, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "languages", value = languages, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "topics", value = topics, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_starred", value = isStarred, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "source", value = source, explode = true, json = Api.json)
    decorator()
  }
}

public object SyncApi {
  /**
   * **Create Sync Session**
   *
   * `POST /v1/sync/sessions`
   *
   * Create or resume a sync session.
   *
   * @return Sync session created
   */
  public suspend fun createSyncSessionV1SyncSessionsPost(body: SyncSessionRequest? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SyncSessionResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "sync",
      "sessions",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Full Sync**
   *
   * `GET /v1/sync/full`
   *
   * Fetch full sync data in bounded chunks.
   *
   * @param sessionId Sync session identifier
   * @return Full sync chunk
   */
  public suspend fun fullSyncV1SyncFullGet(
    sessionId: String,
    limit: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<FullSyncResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "sync",
      "full",
    )
    appendSerializedQueryParameter(name = "session_id", value = sessionId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Delta Sync**
   *
   * `GET /v1/sync/delta`
   *
   * Fetch delta sync (created/updated/deleted) since a cursor.
   *
   * @param sessionId Sync session identifier
   * @param since Last seen server_version cursor
   * @return Delta sync data
   */
  public suspend fun deltaSyncV1SyncDeltaGet(
    sessionId: String,
    since: Long,
    limit: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<DeltaSyncResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "sync",
      "delta",
    )
    appendSerializedQueryParameter(name = "session_id", value = sessionId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "since", value = since, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Apply Changes**
   *
   * `POST /v1/sync/apply`
   *
   * Apply client-side changes with conflict detection.
   *
   * @return Apply results
   */
  public suspend fun applyChangesV1SyncApplyPost(body: SyncApplyRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SyncApplyResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "sync",
      "apply",
    )
    setBody(body)
    decorator()
  }
}

public object UserApi {
  /**
   * **Get User Preferences**
   *
   * `GET /v1/user/preferences`
   *
   * Get user preferences.
   *
   * @return User preferences
   */
  public suspend fun getUserPreferencesV1UserPreferencesGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<UserPreferencesResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "preferences",
    )
    decorator()
  }

  /**
   * **Update User Preferences**
   *
   * `PATCH /v1/user/preferences`
   *
   * Update user preferences.
   *
   * @return Preferences updated
   */
  public suspend fun updateUserPreferencesV1UserPreferencesPatch(body: UpdatePreferencesRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<UserPreferencesResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "preferences",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get User Stats**
   *
   * `GET /v1/user/stats`
   *
   * Get user statistics.
   *
   * @return User stats
   */
  public suspend fun getUserStatsV1UserStatsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<UserStatsResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "stats",
    )
    decorator()
  }

  /**
   * **List Reading Goals**
   *
   * `GET /v1/user/goals`
   *
   * List all reading goals for the current user.
   *
   * @return List of reading goals
   */
  public suspend fun listGoalsV1UserGoalsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "goals",
    )
    decorator()
  }

  /**
   * **Create or Update Reading Goal**
   *
   * `POST /v1/user/goals`
   *
   * Create or update a reading goal (one per goal_type+scope per user).
   *
   * @return Goal created or updated
   */
  public suspend fun upsertGoalV1UserGoalsPost(body: V1UserGoalsRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "goals",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Delete Global Goal by Type**
   *
   * `DELETE /v1/user/goals/{goal_type}`
   *
   * Remove a global reading goal by type (legacy endpoint).
   *
   * @return Goal deleted
   */
  public suspend fun deleteGoalV1UserGoalsGoalTypeDelete(goalType: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "goals",
      createSerializedPathSegment(value = goalType, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Goal by ID**
   *
   * `DELETE /v1/user/goals/by-id/{goal_id}`
   *
   * Remove a reading goal by its UUID.
   *
   * @return Goal deleted
   */
  public suspend fun deleteGoalByIdV1UserGoalsByIdGoalIdDelete(goalId: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "goals",
      "by-id",
      createSerializedPathSegment(value = goalId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Get Goal Progress**
   *
   * `GET /v1/user/goals/progress`
   *
   * Return each goal with current progress.
   *
   * @return Goal progress list
   */
  public suspend fun getGoalProgressV1UserGoalsProgressGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "goals",
      "progress",
    )
    decorator()
  }

  /**
   * **Get Reading Streak**
   *
   * `GET /v1/user/streak`
   *
   * Compute and return the user's reading streak data.
   *
   * @return Streak data
   */
  public suspend fun getStreakV1UserStreakGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "user",
      "streak",
    )
    decorator()
  }
}

public object DefaultApi {
  /**
   * **Root**
   *
   * `GET /`
   *
   * API root endpoint.
   *
   * @return Root ping
   */
  public suspend fun rootGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RootResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "",
    )
    decorator()
  }
}

public object HealthApi {
  /**
   * **Health Check**
   *
   * `GET /health`
   *
   * Lightweight health check endpoint.
   *
   * @return Health status
   */
  public suspend fun healthCheckHealthGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HealthResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "health",
    )
    decorator()
  }

  /**
   * **Detailed Health Check**
   *
   * `GET /health/detailed`
   *
   * Comprehensive health check including database, Redis, and circuit breaker status. Has a 10-second timeout on component checks.
   *
   * @return Detailed health status with component breakdown
   */
  public suspend fun detailedHealthCheckHealthDetailedGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<DetailedHealthResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "health",
      "detailed",
    )
    decorator()
  }

  /**
   * **Readiness Probe**
   *
   * `GET /health/ready`
   *
   * Kubernetes readiness probe. Checks database connectivity. Returns 503 if not ready.
   *
   * @return Service is ready
   */
  public suspend fun readinessCheckHealthReadyGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<ReadinessResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "health",
      "ready",
    )
    decorator()
  }

  /**
   * **Liveness Probe**
   *
   * `GET /health/live`
   *
   * Kubernetes liveness probe. Minimal check that the process is responsive.
   *
   * @return Service is alive
   */
  public suspend fun livenessCheckHealthLiveGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<LivenessResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "health",
      "live",
    )
    decorator()
  }

  /**
   * **Prometheus Metrics**
   *
   * `GET /metrics`
   *
   * Prometheus-compatible metrics endpoint for scraping.
   *
   * @return Prometheus text metrics
   */
  public suspend fun metricsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    url.appendPathSegments(
      "metrics",
    )
    decorator()
  }
}

public object DigestApi {
  /**
   * **List Digest Channels**
   *
   * `GET /v1/digest/channels`
   *
   * List user's channel subscriptions and slot usage.
   *
   * @return Digest channel subscriptions
   */
  public suspend fun listChannelsV1DigestChannelsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
    )
    decorator()
  }

  /**
   * **Subscribe Digest Channel**
   *
   * `POST /v1/digest/channels/subscribe`
   *
   * Subscribe to a Telegram channel for digest generation.
   *
   * @return Subscription result
   */
  public suspend fun subscribeChannelV1DigestChannelsSubscribePost(body: V1DigestChannelsSubscribeRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      "subscribe",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Unsubscribe Digest Channel**
   *
   * `POST /v1/digest/channels/unsubscribe`
   *
   * Unsubscribe from a Telegram channel.
   *
   * @return Unsubscribe result
   */
  public suspend fun unsubscribeChannelV1DigestChannelsUnsubscribePost(body: V1DigestChannelsUnsubscribeRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      "unsubscribe",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Digest Preferences**
   *
   * `GET /v1/digest/preferences`
   *
   * Get merged digest preferences (user overrides plus global defaults).
   *
   * @return Digest preferences
   */
  public suspend fun getPreferencesV1DigestPreferencesGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "preferences",
    )
    decorator()
  }

  /**
   * **Update Digest Preferences**
   *
   * `PATCH /v1/digest/preferences`
   *
   * Update user digest preferences.
   *
   * @return Preferences update result
   */
  public suspend fun updatePreferencesV1DigestPreferencesPatch(body: V1DigestPreferencesRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "preferences",
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Digest History**
   *
   * `GET /v1/digest/history`
   *
   * Get paginated list of past digest deliveries.
   *
   * @return Digest history
   */
  public suspend fun listHistoryV1DigestHistoryGet(
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "history",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Trigger Digest**
   *
   * `POST /v1/digest/trigger`
   *
   * Trigger an on-demand digest generation for current user.
   *
   * @return Trigger accepted
   */
  public suspend fun triggerDigestV1DigestTriggerPost(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "trigger",
    )
    decorator()
  }

  /**
   * **Trigger Channel Digest**
   *
   * `POST /v1/digest/trigger-channel`
   *
   * Trigger digest for a single channel.
   *
   * @return Trigger accepted
   */
  public suspend fun triggerChannelDigestV1DigestTriggerChannelPost(body: V1DigestTriggerChannelRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "trigger-channel",
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Channel Posts**
   *
   * `GET /v1/digest/channels/{username}/posts`
   *
   * List recent posts for a subscribed channel.
   *
   * @return Paginated list of channel posts
   */
  public suspend fun listChannelPostsV1DigestChannelsUsernamePostsGet(
    username: String,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      createSerializedPathSegment(value = username, explode = false, json = Api.json),
      "posts",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Resolve Channel**
   *
   * `POST /v1/digest/channels/resolve`
   *
   * Resolve a channel username and return metadata preview.
   *
   * @return Channel metadata
   */
  public suspend fun resolveChannelV1DigestChannelsResolvePost(body: V1DigestChannelsResolveRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      "resolve",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Bulk Unsubscribe**
   *
   * `POST /v1/digest/channels/bulk-unsubscribe`
   *
   * Unsubscribe from multiple channels at once.
   *
   * @return Bulk unsubscribe result
   */
  public suspend fun bulkUnsubscribeV1DigestChannelsBulkUnsubscribePost(body: V1DigestChannelsBulkUnsubscribeRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      "bulk-unsubscribe",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Bulk Assign Category**
   *
   * `PATCH /v1/digest/channels/bulk-category`
   *
   * Assign multiple subscriptions to a category at once.
   *
   * @return Bulk assign result
   */
  public suspend fun bulkAssignCategoryV1DigestChannelsBulkCategoryPatch(body: V1DigestChannelsBulkCategoryRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      "bulk-category",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Assign Category To Subscription**
   *
   * `PATCH /v1/digest/channels/{subscription_id}/category`
   *
   * Assign a subscription to a category, or remove the assignment with null.
   *
   * @return Assign result
   */
  public suspend fun assignCategoryV1DigestChannelsSubscriptionIdCategoryPatch(
    subscriptionId: Long,
    body: V1DigestChannelsSubscriptionIdCategoryRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "channels",
      createSerializedPathSegment(value = subscriptionId, explode = false, json = Api.json),
      "category",
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Categories**
   *
   * `GET /v1/digest/categories`
   *
   * List the authenticated user's channel categories.
   *
   * @return List of categories
   */
  public suspend fun listCategoriesV1DigestCategoriesGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<V1DigestCategories200Response>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "categories",
    )
    decorator()
  }

  /**
   * **Create Category**
   *
   * `POST /v1/digest/categories`
   *
   * Create a new channel category.
   *
   * @return Created category
   */
  public suspend fun createCategoryV1DigestCategoriesPost(body: V1DigestCategoriesRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "categories",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Delete Category**
   *
   * `DELETE /v1/digest/categories/{category_id}`
   *
   * Delete a channel category. Subscriptions in the category become uncategorized.
   *
   * @return Delete result
   */
  public suspend fun deleteCategoryV1DigestCategoriesCategoryIdDelete(categoryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "categories",
      createSerializedPathSegment(value = categoryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Category**
   *
   * `PATCH /v1/digest/categories/{category_id}`
   *
   * Update the name of a channel category.
   *
   * @return Updated category
   */
  public suspend fun updateCategoryV1DigestCategoriesCategoryIdPatch(
    categoryId: Long,
    body: V1DigestCategoriesCategoryIdRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digest",
      "categories",
      createSerializedPathSegment(value = categoryId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }
}

public object SystemApi {
  /**
   * **Get Database Info**
   *
   * `GET /v1/system/db-info`
   *
   * Return database file size and table row counts. Owner-only.
   *
   * @return Database information
   */
  public suspend fun getDbInfoV1SystemDbInfoGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "system",
      "db-info",
    )
    decorator()
  }

  /**
   * **Clear URL Cache**
   *
   * `POST /v1/system/clear-cache`
   *
   * Clear Redis URL cache keys. Owner-only.
   *
   * @return Cache clear result
   */
  public suspend fun clearCacheV1SystemClearCachePost(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "system",
      "clear-cache",
    )
    decorator()
  }

  /**
   * **Download Database Backup**
   *
   * `GET /v1/system/db-dump`
   *
   * Download a consistent SQLite database snapshot. Owner-only. Supports Range header for resumable downloads.
   *
   * @param range Byte range for resumable downloads (e.g., "bytes=0-1023")
   * @return SQLite database file
   */
  public suspend fun downloadDatabaseV1SystemDbDumpGet(range: String? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "system",
      "db-dump",
    )
    appendSerializedHeaderParameter(name = "Range", value = range, explode = false, json = Api.json)
    decorator()
  }

  /**
   * **Database Backup Pre-flight**
   *
   * `HEAD /v1/system/db-dump`
   *
   * HEAD request for database backup. Returns headers (size, ETag) without body. Owner-only.
   *
   * @return Headers only (Content-Length, ETag, Accept-Ranges)
   */
  public suspend fun headDatabaseV1SystemDbDumpHead(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<Unit>> = Api.client.eitherRequest {
    method = HttpMethod.parse("HEAD")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "system",
      "db-dump",
    )
    decorator()
  }
}

public object ArticlesApi {
  /**
   * **Get Articles (alias)**
   *
   * `GET /v1/articles`
   *
   * Alias for GET /v1/summaries. Returns paginated list of summaries.
   * This endpoint mirrors /v1/summaries exactly and exists for API naming convenience.
   *
   * @return Paginated summaries
   */
  public suspend fun getArticlesV1ArticlesGet(
    limit: Long? = null,
    offset: Long? = null,
    isRead: Boolean? = null,
    isFavorited: Boolean? = null,
    lang: String? = null,
    startDate: String? = null,
    endDate: String? = null,
    sort: String? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<PaginatedSummariesResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_read", value = isRead, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "is_favorited", value = isFavorited, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "lang", value = lang, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "start_date", value = startDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "end_date", value = endDate, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "sort", value = sort, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Article (alias)**
   *
   * `GET /v1/articles/{summary_id}`
   *
   * Alias for GET /v1/summaries/{summary_id}. Returns full summary details.
   *
   * @return Summary detail
   */
  public suspend fun getArticleV1ArticlesSummaryIdGet(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDetailResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Article (alias)**
   *
   * `DELETE /v1/articles/{summary_id}`
   *
   * Alias for DELETE /v1/summaries/{summary_id}.
   *
   * @return Summary deleted
   */
  public suspend fun deleteArticleV1ArticlesSummaryIdDelete(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDeleteResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Article (alias)**
   *
   * `PATCH /v1/articles/{summary_id}`
   *
   * Alias for PATCH /v1/summaries/{summary_id}.
   *
   * @return Summary updated
   */
  public suspend fun updateArticleV1ArticlesSummaryIdPatch(
    summaryId: Long,
    body: UpdateSummaryRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SummaryUpdateResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Article Content (alias)**
   *
   * `GET /v1/articles/{summary_id}/content`
   *
   * Alias for GET /v1/summaries/{summary_id}/content.
   *
   * @return Summary content
   */
  public suspend fun getArticleContentV1ArticlesSummaryIdContentGet(
    summaryId: Long,
    format: GetArticleContentV1ArticlesSummaryIdContentGetFormat? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SummaryContentResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "content",
    )
    appendSerializedQueryParameter(name = "format", value = format, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Toggle Article Favorite (alias)**
   *
   * `POST /v1/articles/{summary_id}/favorite`
   *
   * Alias for POST /v1/summaries/{summary_id}/favorite.
   *
   * @return Favorite status updated
   */
  public suspend fun toggleArticleFavoriteV1ArticlesSummaryIdFavoritePost(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<ToggleFavoriteResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "favorite",
    )
    decorator()
  }

  /**
   * **Get Article by URL (alias)**
   *
   * `GET /v1/articles/by-url`
   *
   * Alias for GET /v1/summaries/by-url.
   *
   * @param url Original URL of the article
   * @return Summary detail
   */
  public suspend fun getArticleByUrlV1ArticlesByUrlGet(reqUrl: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SummaryDetailResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      "by-url",
    )
    appendSerializedQueryParameter(name = "url", value = reqUrl, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Save Reading Position (alias)**
   *
   * `PATCH /v1/articles/{summary_id}/reading-position`
   *
   * Alias for PATCH /v1/summaries/{summary_id}/reading-position.
   *
   * @return Reading position saved
   */
  public suspend fun saveReadingPositionV1ArticlesSummaryIdReadingPositionPatch(
    summaryId: Long,
    body: V1ArticlesSummaryIdReadingPositionRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "reading-position",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Submit Feedback (alias)**
   *
   * `POST /v1/articles/{summary_id}/feedback`
   *
   * Alias for POST /v1/summaries/{summary_id}/feedback.
   *
   * @return Feedback submitted
   */
  public suspend fun submitFeedbackV1ArticlesSummaryIdFeedbackPost(
    summaryId: Long,
    body: V1ArticlesSummaryIdFeedbackRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "feedback",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Export Article (alias)**
   *
   * `GET /v1/articles/{summary_id}/export`
   *
   * Alias for GET /v1/summaries/{summary_id}/export.
   *
   * @return Exported file
   */
  public suspend fun exportSummaryV1ArticlesSummaryIdExportGet(
    summaryId: Long,
    format: ExportSummaryV1ArticlesSummaryIdExportGetFormat? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "export",
    )
    appendSerializedQueryParameter(name = "format", value = format, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Get Recommendations (alias)**
   *
   * `GET /v1/articles/recommendations`
   *
   * Alias for GET /v1/summaries/recommendations.
   *
   * @return List of recommended summaries
   */
  public suspend fun getRecommendationsV1ArticlesRecommendationsGet(limit: Long? = null, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "articles",
      "recommendations",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    decorator()
  }
}

public object RSSApi {
  /**
   * **List RSS Feed Subscriptions**
   *
   * `GET /v1/rss/feeds`
   *
   * List user's RSS feed subscriptions.
   *
   * @return List of RSS subscriptions
   */
  public suspend fun listFeedsV1RssFeedsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "feeds",
    )
    decorator()
  }

  /**
   * **Subscribe to RSS Feed**
   *
   * `POST /v1/rss/feeds/subscribe`
   *
   * Subscribe to an RSS feed by URL.
   *
   * @return Subscription created
   */
  public suspend fun subscribeV1RssFeedsSubscribePost(body: V1RssFeedsSubscribeRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "feeds",
      "subscribe",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Unsubscribe from RSS Feed**
   *
   * `DELETE /v1/rss/feeds/{subscription_id}`
   *
   * Unsubscribe from an RSS feed (verify ownership).
   *
   * @return Subscription deleted
   */
  public suspend fun unsubscribeV1RssFeedsSubscriptionIdDelete(subscriptionId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "feeds",
      createSerializedPathSegment(value = subscriptionId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **List Feed Items**
   *
   * `GET /v1/rss/feeds/{feed_id}/items`
   *
   * List paginated items for a feed.
   *
   * @return Paginated feed items
   */
  public suspend fun listFeedItemsV1RssFeedsFeedIdItemsGet(
    feedId: Long,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "feeds",
      createSerializedPathSegment(value = feedId, explode = false, json = Api.json),
      "items",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Refresh Feed**
   *
   * `POST /v1/rss/feeds/{feed_id}/refresh`
   *
   * Trigger a fetch for a specific feed and store new items.
   *
   * @return Feed refresh result
   */
  public suspend fun refreshFeedV1RssFeedsFeedIdRefreshPost(feedId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "feeds",
      createSerializedPathSegment(value = feedId, explode = false, json = Api.json),
      "refresh",
    )
    decorator()
  }

  /**
   * **Export Subscriptions as OPML**
   *
   * `GET /v1/rss/export/opml`
   *
   * Export user's RSS subscriptions as OPML 2.0.
   *
   * @return OPML XML file
   */
  public suspend fun exportOpmlV1RssExportOpmlGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "export",
      "opml",
    )
    decorator()
  }

  /**
   * **Import OPML File**
   *
   * `POST /v1/rss/import/opml`
   *
   * Import OPML file and subscribe to each feed URL.
   *
   * @return Import result
   */
  public suspend fun importOpmlV1RssImportOpmlPost(body: MultiPartFormDataContent, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rss",
      "import",
      "opml",
    )
    setBody(body)
    decorator()
  }
}

public object TagsApi {
  /**
   * **List Tags**
   *
   * `GET /v1/tags/`
   *
   * List all tags for the current user.
   *
   * @return List of tags
   */
  public suspend fun listTagsV1TagsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      "",
    )
    decorator()
  }

  /**
   * **Create Tag**
   *
   * `POST /v1/tags/`
   *
   * Create a new tag.
   *
   * @return Tag created
   */
  public suspend fun createTagV1TagsPost(body: V1TagsRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      "",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Tag**
   *
   * `GET /v1/tags/{tag_id}`
   *
   * Get tag details.
   *
   * @return Tag details
   */
  public suspend fun getTagV1TagsTagIdGet(tagId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      createSerializedPathSegment(value = tagId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Tag**
   *
   * `DELETE /v1/tags/{tag_id}`
   *
   * Soft-delete a tag.
   *
   * @return Tag deleted
   */
  public suspend fun deleteTagV1TagsTagIdDelete(tagId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      createSerializedPathSegment(value = tagId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Tag**
   *
   * `PATCH /v1/tags/{tag_id}`
   *
   * Update a tag's name or color.
   *
   * @return Tag updated
   */
  public suspend fun updateTagV1TagsTagIdPatch(
    tagId: Long,
    body: V1TagsTagIdRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      createSerializedPathSegment(value = tagId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **Merge Tags**
   *
   * `POST /v1/tags/merge`
   *
   * Merge source tags into a target tag.
   *
   * @return Tags merged
   */
  public suspend fun mergeTagsV1TagsMergePost(body: V1TagsMergeRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "tags",
      "merge",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Attach Tags to Summary**
   *
   * `POST /v1/summaries/{summary_id}/tags`
   *
   * Attach tags to a summary by ID or name (auto-create if needed).
   *
   * @return Tags attached
   */
  public suspend fun attachTagsV1SummariesSummaryIdTagsPost(
    summaryId: Long,
    body: V1SummariesSummaryIdTagsRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "tags",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Detach Tag from Summary**
   *
   * `DELETE /v1/summaries/{summary_id}/tags/{tag_id}`
   *
   * Detach a tag from a summary.
   *
   * @return Tag detached
   */
  public suspend fun detachTagV1SummariesSummaryIdTagsTagIdDelete(
    summaryId: Long,
    tagId: Long,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "tags",
      createSerializedPathSegment(value = tagId, explode = false, json = Api.json),
    )
    decorator()
  }
}

public object WebhooksApi {
  /**
   * **List Webhook Subscriptions**
   *
   * `GET /v1/webhooks/`
   *
   * List user's webhook subscriptions.
   *
   * @return List of webhook subscriptions
   */
  public suspend fun listSubscriptionsV1WebhooksGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      "",
    )
    decorator()
  }

  /**
   * **Create Webhook Subscription**
   *
   * `POST /v1/webhooks/`
   *
   * Create a new webhook subscription.
   *
   * @return Webhook subscription created
   */
  public suspend fun createSubscriptionV1WebhooksPost(body: V1WebhooksRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      "",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Webhook Subscription**
   *
   * `GET /v1/webhooks/{webhook_id}`
   *
   * Get a webhook subscription's details.
   *
   * @return Webhook subscription details
   */
  public suspend fun getSubscriptionV1WebhooksWebhookIdGet(webhookId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Webhook Subscription**
   *
   * `DELETE /v1/webhooks/{webhook_id}`
   *
   * Soft-delete a webhook subscription.
   *
   * @return Webhook subscription deleted
   */
  public suspend fun deleteSubscriptionV1WebhooksWebhookIdDelete(webhookId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Webhook Subscription**
   *
   * `PATCH /v1/webhooks/{webhook_id}`
   *
   * Update a webhook subscription.
   *
   * @return Webhook subscription updated
   */
  public suspend fun updateSubscriptionV1WebhooksWebhookIdPatch(
    webhookId: Long,
    body: V1WebhooksWebhookIdRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Webhook Deliveries**
   *
   * `GET /v1/webhooks/{webhook_id}/deliveries`
   *
   * Return paginated delivery history for a webhook subscription.
   *
   * @return Paginated delivery history
   */
  public suspend fun listDeliveriesV1WebhooksWebhookIdDeliveriesGet(
    webhookId: Long,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
      "deliveries",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Rotate Webhook Secret**
   *
   * `POST /v1/webhooks/{webhook_id}/rotate-secret`
   *
   * Generate a new secret for the subscription. Returns the new secret once.
   *
   * @return New secret generated
   */
  public suspend fun rotateSecretV1WebhooksWebhookIdRotateSecretPost(webhookId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
      "rotate-secret",
    )
    decorator()
  }

  /**
   * **Send Test Webhook**
   *
   * `POST /v1/webhooks/{webhook_id}/test`
   *
   * Send a test event to the webhook URL and return the delivery result.
   *
   * @return Test delivery result
   */
  public suspend fun sendTestWebhookV1WebhooksWebhookIdTestPost(webhookId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "webhooks",
      createSerializedPathSegment(value = webhookId, explode = false, json = Api.json),
      "test",
    )
    decorator()
  }
}

public object RulesApi {
  /**
   * **List Automation Rules**
   *
   * `GET /v1/rules/`
   *
   * List all automation rules for the current user.
   *
   * @return List of automation rules
   */
  public suspend fun listRulesV1RulesGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      "",
    )
    decorator()
  }

  /**
   * **Create Automation Rule**
   *
   * `POST /v1/rules/`
   *
   * Create a new automation rule.
   *
   * @return Rule created
   */
  public suspend fun createRuleV1RulesPost(body: V1RulesRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      "",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Automation Rule**
   *
   * `GET /v1/rules/{rule_id}`
   *
   * Get rule details.
   *
   * @return Rule details
   */
  public suspend fun getRuleV1RulesRuleIdGet(ruleId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      createSerializedPathSegment(value = ruleId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Automation Rule**
   *
   * `DELETE /v1/rules/{rule_id}`
   *
   * Soft-delete an automation rule.
   *
   * @return Rule deleted
   */
  public suspend fun deleteRuleV1RulesRuleIdDelete(ruleId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      createSerializedPathSegment(value = ruleId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Automation Rule**
   *
   * `PATCH /v1/rules/{rule_id}`
   *
   * Update an automation rule.
   *
   * @return Rule updated
   */
  public suspend fun updateRuleV1RulesRuleIdPatch(
    ruleId: Long,
    body: V1RulesRuleIdRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      createSerializedPathSegment(value = ruleId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }

  /**
   * **List Rule Execution Logs**
   *
   * `GET /v1/rules/{rule_id}/logs`
   *
   * Return paginated execution history for a rule.
   *
   * @return Paginated execution logs
   */
  public suspend fun listExecutionLogsV1RulesRuleIdLogsGet(
    ruleId: Long,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      createSerializedPathSegment(value = ruleId, explode = false, json = Api.json),
      "logs",
    )
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Dry-Run Rule Test**
   *
   * `POST /v1/rules/{rule_id}/test`
   *
   * Dry-run a rule against a summary without side effects.
   *
   * @return Dry-run result
   */
  public suspend fun dryRunRuleV1RulesRuleIdTestPost(
    ruleId: Long,
    body: V1RulesRuleIdTestRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "rules",
      createSerializedPathSegment(value = ruleId, explode = false, json = Api.json),
      "test",
    )
    setBody(body)
    decorator()
  }
}

public object BackupsApi {
  /**
   * **List Backups**
   *
   * `GET /v1/backups/`
   *
   * List user's backups.
   *
   * @return List of backups
   */
  public suspend fun listBackupsV1BackupsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      "",
    )
    decorator()
  }

  /**
   * **Create Backup**
   *
   * `POST /v1/backups/`
   *
   * Create a new backup archive. Processing happens in the background.
   *
   * @return Backup creation initiated
   */
  public suspend fun createBackupV1BackupsPost(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      "",
    )
    decorator()
  }

  /**
   * **Get Backup Schedule**
   *
   * `GET /v1/backups/schedule`
   *
   * Read the user's backup schedule preferences.
   *
   * @return Backup schedule preferences
   */
  public suspend fun getBackupScheduleV1BackupsScheduleGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      "schedule",
    )
    decorator()
  }

  /**
   * **Update Backup Schedule**
   *
   * `PATCH /v1/backups/schedule`
   *
   * Update the user's backup schedule preferences.
   *
   * @return Schedule updated
   */
  public suspend fun updateBackupScheduleV1BackupsSchedulePatch(body: V1BackupsScheduleRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      "schedule",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Backup**
   *
   * `GET /v1/backups/{backup_id}`
   *
   * Get backup details.
   *
   * @return Backup details
   */
  public suspend fun getBackupV1BackupsBackupIdGet(backupId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      createSerializedPathSegment(value = backupId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Backup**
   *
   * `DELETE /v1/backups/{backup_id}`
   *
   * Delete a backup record and its file from disk.
   *
   * @return Backup deleted
   */
  public suspend fun deleteBackupV1BackupsBackupIdDelete(backupId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      createSerializedPathSegment(value = backupId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Download Backup**
   *
   * `GET /v1/backups/{backup_id}/download`
   *
   * Download the backup ZIP file.
   *
   * @return Backup ZIP file
   */
  public suspend fun downloadBackupV1BackupsBackupIdDownloadGet(backupId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<HttpResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      createSerializedPathSegment(value = backupId, explode = false, json = Api.json),
      "download",
    )
    decorator()
  }

  /**
   * **Restore Backup**
   *
   * `POST /v1/backups/restore`
   *
   * Restore user data from an uploaded backup ZIP.
   *
   * @return Restore result
   */
  public suspend fun restoreBackupV1BackupsRestorePost(body: MultiPartFormDataContent, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "backups",
      "restore",
    )
    setBody(body)
    decorator()
  }
}

public object ImportExportApi {
  /**
   * **Export Bookmarks**
   *
   * `GET /v1/export`
   *
   * Export user summaries in the requested format (json, csv, html).
   *
   * @return Exported file
   */
  public suspend fun exportBookmarksV1ExportGet(
    format: ExportBookmarksV1ExportGetFormat? = null,
    tag: String? = null,
    collectionId: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<String>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "export",
    )
    appendSerializedQueryParameter(name = "format", value = format, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "tag", value = tag, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "collection_id", value = collectionId, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **List Import Jobs**
   *
   * `GET /v1/import`
   *
   * List user's import jobs.
   *
   * @return List of import jobs
   */
  public suspend fun listImportJobsV1ImportGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "import",
    )
    decorator()
  }

  /**
   * **Import Bookmarks**
   *
   * `POST /v1/import`
   *
   * Import bookmarks from an uploaded file.
   *
   * @return Import job created
   */
  public suspend fun importBookmarksV1ImportPost(body: MultiPartFormDataContent, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "import",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Import Job**
   *
   * `GET /v1/import/{job_id}`
   *
   * Get import job status and progress.
   *
   * @return Import job details
   */
  public suspend fun getImportJobV1ImportJobIdGet(jobId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "import",
      createSerializedPathSegment(value = jobId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Import Job**
   *
   * `DELETE /v1/import/{job_id}`
   *
   * Delete an import job.
   *
   * @return Import job deleted
   */
  public suspend fun deleteImportJobV1ImportJobIdDelete(jobId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "import",
      createSerializedPathSegment(value = jobId, explode = false, json = Api.json),
    )
    decorator()
  }
}

public object HighlightsApi {
  /**
   * **List Highlights**
   *
   * `GET /v1/summaries/{summary_id}/highlights`
   *
   * List all highlights for a summary.
   *
   * @return List of highlights
   */
  public suspend fun listHighlightsV1SummariesSummaryIdHighlightsGet(summaryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "highlights",
    )
    decorator()
  }

  /**
   * **Create Highlight**
   *
   * `POST /v1/summaries/{summary_id}/highlights`
   *
   * Create a highlight on a summary.
   *
   * @return Highlight created
   */
  public suspend fun createHighlightV1SummariesSummaryIdHighlightsPost(
    summaryId: Long,
    body: V1SummariesSummaryIdHighlightsRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "highlights",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Delete Highlight**
   *
   * `DELETE /v1/summaries/{summary_id}/highlights/{highlight_id}`
   *
   * Delete a highlight.
   *
   * @return Highlight deleted
   */
  public suspend fun deleteHighlightV1SummariesSummaryIdHighlightsHighlightIdDelete(
    summaryId: Long,
    highlightId: String,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "highlights",
      createSerializedPathSegment(value = highlightId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Update Highlight**
   *
   * `PATCH /v1/summaries/{summary_id}/highlights/{highlight_id}`
   *
   * Update a highlight's color or note.
   *
   * @return Highlight updated
   */
  public suspend fun updateHighlightV1SummariesSummaryIdHighlightsHighlightIdPatch(
    summaryId: Long,
    highlightId: String,
    body: V1SummariesSummaryIdHighlightsHighlightIdRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("PATCH")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "summaries",
      createSerializedPathSegment(value = summaryId, explode = false, json = Api.json),
      "highlights",
      createSerializedPathSegment(value = highlightId, explode = false, json = Api.json),
    )
    setBody(body)
    decorator()
  }
}

public object DigestsApi {
  /**
   * **List Custom Digests**
   *
   * `GET /v1/digests/custom`
   *
   * List all custom digests for the current user, newest first.
   *
   * @return List of custom digests
   */
  public suspend fun listCustomDigestsV1DigestsCustomGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digests",
      "custom",
    )
    decorator()
  }

  /**
   * **Create Custom Digest**
   *
   * `POST /v1/digests/custom`
   *
   * Create a custom digest from a list of summary IDs.
   *
   * @return Custom digest created
   */
  public suspend fun createCustomDigestV1DigestsCustomPost(body: V1DigestsCustomRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digests",
      "custom",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Custom Digest**
   *
   * `GET /v1/digests/custom/{digest_id}`
   *
   * Get a specific custom digest by ID.
   *
   * @return Custom digest details
   */
  public suspend fun getCustomDigestV1DigestsCustomDigestIdGet(digestId: String, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "digests",
      "custom",
      createSerializedPathSegment(value = digestId, explode = false, json = Api.json),
    )
    decorator()
  }
}

public object AdminApi {
  /**
   * **List Users**
   *
   * `GET /v1/admin/users`
   *
   * List all users with per-user summary/request/tag/collection counts.
   *
   * @return User list with stats
   */
  public suspend fun listUsersV1AdminUsersGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "admin",
      "users",
    )
    decorator()
  }

  /**
   * **Content Health Report**
   *
   * `GET /v1/admin/health/content`
   *
   * Content pipeline health: totals, failure breakdown, recent errors.
   *
   * @return Content health report
   */
  public suspend fun contentHealthV1AdminHealthContentGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "admin",
      "health",
      "content",
    )
    decorator()
  }

  /**
   * **Background Job Status**
   *
   * `GET /v1/admin/jobs`
   *
   * Pipeline and import job status overview.
   *
   * @return Job status overview
   */
  public suspend fun jobStatusV1AdminJobsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "admin",
      "jobs",
    )
    decorator()
  }

  /**
   * **System Metrics**
   *
   * `GET /v1/admin/metrics`
   *
   * Database, LLM, and scraper metrics.
   *
   * @return System metrics
   */
  public suspend fun systemMetricsV1AdminMetricsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "admin",
      "metrics",
    )
    decorator()
  }

  /**
   * **Audit Log**
   *
   * `GET /v1/admin/audit-log`
   *
   * Paginated, filterable audit log.
   *
   * @param action Filter by event name
   * @param userId Filter by user_id in details
   * @param since ISO datetime lower bound
   * @return Paginated audit log entries
   */
  public suspend fun auditLogV1AdminAuditLogGet(
    action: String? = null,
    userId: Long? = null,
    since: String? = null,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "admin",
      "audit-log",
    )
    appendSerializedQueryParameter(name = "action", value = action, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "user_id", value = userId, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "since", value = since, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }
}

public object QuickSaveApi {
  /**
   * **Quick Save URL**
   *
   * `POST /v1/quick-save`
   *
   * Save a page from the browser extension with optional summarization and tags.
   *
   * @return Quick save result
   */
  public suspend fun quickSaveV1QuickSavePost(body: V1QuickSaveRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<JsonElement>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "quick-save",
    )
    setBody(body)
    decorator()
  }
}

public object SignalsApi {
  /**
   * **List Signals**
   *
   * `GET /v1/signals`
   *
   * List scored signal candidates for the authenticated user.
   *
   * @return Signal queue
   */
  public suspend fun listSignalsV1SignalsGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SignalListResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
    )
    decorator()
  }

  /**
   * **Signal Health**
   *
   * `GET /v1/signals/health`
   *
   * Return Qdrant readiness and source health counts for signal scoring.
   *
   * @return Signal scoring health
   */
  public suspend fun signalHealthV1SignalsHealthGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SignalHealthResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
      "health",
    )
    decorator()
  }

  /**
   * **Signal Source Health**
   *
   * `GET /v1/signals/sources/health`
   *
   * List signal source health rows visible to the authenticated user.
   *
   * @return Source health rows
   */
  public suspend fun sourceHealthV1SignalsSourcesHealthGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SignalSourceHealthResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
      "sources",
      "health",
    )
    decorator()
  }

  /**
   * **Set Signal Source Active**
   *
   * `POST /v1/signals/sources/{source_id}/active`
   *
   * Enable or pause a signal source if the authenticated user is subscribed to it.
   *
   * @return Source active state updated
   */
  public suspend fun setSourceActiveV1SignalsSourcesSourceIdActivePost(
    sourceId: Long,
    body: SourceActiveRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SignalSourceActiveResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
      "sources",
      createSerializedPathSegment(value = sourceId, explode = false, json = Api.json),
      "active",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Update Signal Feedback**
   *
   * `POST /v1/signals/{signal_id}/feedback`
   *
   * Write feedback for one signal candidate.
   *
   * @return Feedback recorded
   */
  public suspend fun updateSignalFeedbackV1SignalsSignalIdFeedbackPost(
    signalId: Long,
    body: SignalFeedbackRequest,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<SignalFeedbackResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
      createSerializedPathSegment(value = signalId, explode = false, json = Api.json),
      "feedback",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Upsert Signal Topic**
   *
   * `POST /v1/signals/topics`
   *
   * Create or update a single-user topic preference for signal scoring.
   *
   * @return Topic preference saved
   */
  public suspend fun upsertTopicV1SignalsTopicsPost(body: TopicPreferenceRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<SignalTopicResponseEnvelope>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "signals",
      "topics",
    )
    setBody(body)
    decorator()
  }
}

public object AuthGithubApi {
  /**
   * **Revoke GitHub Integration**
   *
   * `DELETE /v1/auth/github`
   *
   * Revoke (delete) the GitHub integration for the authenticated user.
   *
   * @return Integration revoked (no content).
   */
  public suspend fun revokeV1AuthGithubDelete(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<Unit>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "github",
    )
    decorator()
  }

  /**
   * **Get GitHub Integration Status**
   *
   * `GET /v1/auth/github/status`
   *
   * Return the current GitHub integration status for the authenticated user.
   *
   * @return Current integration status
   */
  public suspend fun getStatusV1AuthGithubStatusGet(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<GitHubStatusResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "github",
      "status",
    )
    decorator()
  }

  /**
   * **Submit GitHub Personal Access Token**
   *
   * `POST /v1/auth/github/pat`
   *
   * Store and validate a GitHub Personal Access Token (PAT) for the
   * authenticated user. Validates the token against the GitHub API and,
   * on success, persists it Fernet-encrypted in the user_github_integrations
   * table.
   *
   * @return Token accepted and integration created/updated
   */
  public suspend fun submitPatV1AuthGithubPatPost(body: PATSubmitRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<PATSubmitResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "github",
      "pat",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Start GitHub OAuth Device Flow**
   *
   * `POST /v1/auth/github/device/start`
   *
   * Initiate the GitHub OAuth Device Flow. POSTs to GitHub to get a
   * device_code and user_code, stores the device_code in Redis bound to
   * the authenticated user_id, and returns the values needed by the client
   * to display the user_code and verification_uri.
   *
   * Requires `GITHUB_OAUTH_APP_CLIENT_ID` to be configured and Redis to be
   * available; returns 503 otherwise.
   *
   * @return Device flow initiated
   */
  public suspend fun deviceFlowStartV1AuthGithubDeviceStartPost(decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<DeviceFlowStartResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "github",
      "device",
      "start",
    )
    decorator()
  }

  /**
   * **Poll GitHub OAuth Device Flow**
   *
   * `POST /v1/auth/github/device/poll`
   *
   * Poll GitHub for the Device Flow access token using the device_code
   * previously returned by /v1/auth/github/device/start. Returns one of
   * `pending`, `slow_down`, `expired`, `ok`, or `denied`. On `ok`, the
   * access token is persisted and a working GitHub integration record is
   * created for the authenticated user.
   *
   * @return Poll result
   */
  public suspend fun deviceFlowPollV1AuthGithubDevicePollPost(body: DeviceFlowPollRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<DeviceFlowPollResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "auth",
      "github",
      "device",
      "poll",
    )
    setBody(body)
    decorator()
  }
}

public object RepositoriesApi {
  /**
   * **List Repositories**
   *
   * `GET /v1/repositories`
   *
   * List GitHub repositories for the authenticated user with optional filters and sort.
   *
   * @return Repositories matching the filters
   */
  public suspend fun listRepositoriesV1RepositoriesGet(
    isStarred: Boolean? = null,
    language: String? = null,
    topic: String? = null,
    source: ListRepositoriesV1RepositoriesGetSource? = null,
    pendingAnalysis: Boolean? = null,
    sort: RepositoryListSort? = null,
    limit: Long? = null,
    offset: Long? = null,
    decorator: HttpRequestBuilder.() -> Unit = {},
  ): Either<CallException, HttpCallResponse<RepositoryListResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "repositories",
    )
    appendSerializedQueryParameter(name = "is_starred", value = isStarred, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "language", value = language, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "topic", value = topic, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "source", value = source, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "pending_analysis", value = pendingAnalysis, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "sort", value = sort, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "limit", value = limit, explode = true, json = Api.json)
    appendSerializedQueryParameter(name = "offset", value = offset, explode = true, json = Api.json)
    decorator()
  }

  /**
   * **Ingest Repository**
   *
   * `POST /v1/repositories`
   *
   * Ingest a GitHub repository by URL. Validates the URL is a github.com
   * repository URL, fetches metadata via the GitHub adapter, persists the
   * Repository row, and (asynchronously) triggers analysis. Returns
   * immediately with a repository id and a status of either `ready` (if
   * the repository was processed inline) or `pending`.
   *
   * @return Ingestion accepted
   */
  public suspend fun ingestRepositoryV1RepositoriesPost(body: IngestRepositoryRequest, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<IngestRepositoryResponse>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    contentType(ContentType.Application.Json)
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "repositories",
    )
    setBody(body)
    decorator()
  }

  /**
   * **Get Repository**
   *
   * `GET /v1/repositories/{repository_id}`
   *
   * Return full detail for a single repository owned by the authenticated user.
   *
   * @return Repository detail
   */
  public suspend fun getRepositoryV1RepositoriesRepositoryIdGet(repositoryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RepositoryDetail>> = Api.client.eitherRequest {
    method = HttpMethod.parse("GET")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "repositories",
      createSerializedPathSegment(value = repositoryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Delete Repository**
   *
   * `DELETE /v1/repositories/{repository_id}`
   *
   * Delete a repository and its Qdrant embedding point. Idempotent for missing rows (returns 404).
   *
   * @return Repository deleted (no content).
   */
  public suspend fun deleteRepositoryV1RepositoriesRepositoryIdDelete(repositoryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<Unit>> = Api.client.eitherRequest {
    method = HttpMethod.parse("DELETE")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "repositories",
      createSerializedPathSegment(value = repositoryId, explode = false, json = Api.json),
    )
    decorator()
  }

  /**
   * **Reanalyze Repository**
   *
   * `POST /v1/repositories/{repository_id}/reanalyze`
   *
   * Force re-analysis of a repository. Re-runs the analysis agent and updates the stored analysis_json + analysis_at columns.
   *
   * @return Re-analysis completed and updated detail returned
   */
  public suspend fun reanalyzeRepositoryV1RepositoriesRepositoryIdReanalyzePost(repositoryId: Long, decorator: HttpRequestBuilder.() -> Unit = {}): Either<CallException, HttpCallResponse<RepositoryDetail>> = Api.client.eitherRequest {
    method = HttpMethod.parse("POST")
    authKeys(
      Auth.HTTPBearer.ID,
    )
    url.appendPathSegments(
      "v1",
      "repositories",
      createSerializedPathSegment(value = repositoryId, explode = false, json = Api.json),
      "reanalyze",
    )
    decorator()
  }
}
