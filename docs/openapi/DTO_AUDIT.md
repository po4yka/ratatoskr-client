# DTO Audit: OpenAPI Schema Alignment

Audit date: 2026-02-21
Spec version: [`docs/openapi/mobile_api.yaml`](https://github.com/nickolay-p/bite-size-reader/blob/main/docs/openapi/mobile_api.yaml) (canonical source in backend repo)

## Legend

- **Aligned**: DTO matches spec field-for-field
- **Fixed**: Drift was found and corrected in this audit
- **Skipped**: Not used by mobile client (system/admin endpoints)
- **N/A**: No dedicated DTO needed (uses generic envelope)

## Schema Alignment Table

| OpenAPI Schema | DTO Class | Status | Notes |
|----------------|-----------|--------|-------|
| Meta | `MetaDto` | Aligned | All fields match |
| Pagination | `PaginationDto` | Aligned | All fields match |
| BaseSuccessResponse | `ApiResponseDto<T>` | Aligned | Generic envelope pattern |
| ErrorObject | `ErrorResponseDto` | Aligned | All fields match |
| ErrorResponse | `ApiResponseDto` (error path) | Aligned | |
| AuthTokens | `TokensDto` | Aligned | |
| LoginData | `LoginDataDto` | Aligned | |
| TelegramLoginRequest | `TelegramLoginRequestDto` | Aligned | |
| AppleLoginRequest | `AppleLoginRequestDto` | Aligned | |
| GoogleLoginRequest | `GoogleLoginRequestDto` | Aligned | |
| SecretLoginRequest | `SecretLoginRequestDto` | Aligned | |
| RefreshTokenRequest | `TokenRefreshRequestDto` | Aligned | |
| SecretKeyCreateRequest | `SecretKeyCreateRequestDto` | Aligned | |
| SecretKeyRotateRequest | `SecretKeyRotateRequestDto` | Aligned | |
| SecretKeyRevokeRequest | `SecretKeyRevokeRequestDto` | Aligned | |
| ClientSecretInfo | `ClientSecretInfoDto` | Aligned | |
| SecretKeyCreateResponse | `SecretKeyCreateResponseDto` | Aligned | |
| SecretKeyActionResponse | `SecretKeyActionResponseDto` | Aligned | |
| SecretKeyListResponse | `SecretKeyListResponseDto` | Aligned | |
| TelegramLinkStatus | `TelegramLinkStatusDto` | Aligned | |
| TelegramLinkBeginResponse | `TelegramLinkBeginResponseDto` | Aligned | |
| TelegramLinkCompleteRequest | `TelegramLinkCompleteRequestDto` | Aligned | |
| User | `UserDto` | **Fixed** | `@SerialName("user_id")` -> `@SerialName("id")` |
| UserPreferences | `UserPreferencesDto` | **Fixed** | Added `user_id`, `telegram_username`; made `lang_preference` nullable |
| UserStats | `UserStatsDto` | Aligned | |
| DeviceRegistrationPayload | `DeviceRegistrationPayload` | Aligned | |
| SessionInfo | `SessionInfoDto` | **Fixed** | Changed snake_case SerialNames to camelCase per spec |
| SummaryPayload | (parsed via `JsonElement`) | Aligned | Accessed dynamically in `SummaryDetailDto.jsonPayload` |
| Summary | `SummaryDetailDto` | Aligned | |
| SummaryListItem | `SummaryCompactDto` | **Fixed** | Added `is_favorited`, `image_url` fields |
| SummaryStats | `SummaryStatsDto` | Aligned | |
| PaginatedSummariesData | `SummaryListDataDto` | **Fixed** | Renamed `summaries` -> `items`; made `stats` nullable |
| SummaryDetailData | `SummaryDetailDataDto` | Aligned | |
| SummaryContent | `SummaryContentResponseDto` | **Fixed** | Renamed `id` -> `summary_id`; added 8 missing fields |
| SummaryContentData | `SummaryContentDataDto` | **Fixed** | New wrapper DTO added |
| UpdateSummaryRequest | `UpdateSummaryRequestDto` | Aligned | |
| Request | `RequestInfoDto` | Aligned | |
| RequestDetail | `RequestDetailDto` | **Fixed** | `summary` field type changed to `SummaryCompactDto` |
| RequestStatusData | `RequestStatusResponseDto` | **Fixed** | Added `queue_position` field |
| SubmitURLRequest | `SubmitURLRequestDto` | Aligned | |
| SubmitRequestData | `SubmitRequestResponseDto` | Aligned | |
| SearchResultItem | `SearchResultDto` | Aligned | |
| SearchResponseData | `SearchResponseDataDto` | Aligned | |
| TrendingTopic | `TrendingTopicDto` | **Fixed** | Added `trend`, `percentage_change` fields |
| TrendingTopicsResponseEnvelope.data | `TrendingTopicsDataDto` | **Fixed** | Renamed from `TrendingTopicsResponseDto`; `topics` -> `tags`; added `time_range` |
| RelatedSummary | `RelatedSummaryDto` | Aligned | |
| RelatedTopicsResponseEnvelope.data | `RelatedSummariesResponseDto` | Aligned | |
| Collection | `CollectionDto` | **Fixed** | Removed `is_public`, `owner_id`; added `share_count`; made `updated_at` nullable |
| CollectionCreateRequest | `CollectionCreateRequest` | **Fixed** | Removed `is_public`; added `position` |
| CollectionUpdateRequest | `CollectionUpdateRequest` | **Fixed** | Removed `is_public`; added `position` |
| CollectionItemCreateRequest | `CollectionItemCreateRequest` | **Fixed** | Removed `notes`; type `summaryId` Long->Int |
| CollectionItem | `CollectionItemDto` | **Fixed** | `added_at` -> `created_at`; added `position`; removed `notes`, `summary` |
| CollectionItemsResponse | `CollectionItemsResponse` | Aligned | |
| CollectionListResponse | `CollectionListResponse` | Aligned | |
| CollectionAclEntry | `CollectionAclEntry` | **Fixed** | Made `user_id` nullable per spec |
| CollectionAclResponse | `CollectionAclResponse` | Aligned | |
| CollectionTreeResponse | `CollectionTreeResponse` | **Fixed** | `nodes: List<CollectionTreeNodeDto>` -> `collections: List<CollectionDto>` |
| CollectionReorderRequest | `CollectionReorderRequest` | Aligned | |
| CollectionItemReorderRequest | `CollectionItemReorderRequest` | Aligned | |
| CollectionMoveRequest | `CollectionMoveRequest` | Aligned | |
| CollectionItemMoveRequest | `CollectionItemMoveRequest` | Aligned | |
| CollectionShareRequest | `CollectionShareRequest` | Aligned | |
| CollectionInviteRequest | `CollectionInviteRequest` | Aligned | |
| CollectionInviteResponse | `CollectionInviteResponse` | Aligned | |
| CollectionMoveResponse | `CollectionMoveResponse` | Aligned | |
| CollectionItemsMoveResponse | `CollectionItemsMoveResponse` | Aligned | |
| AclSummary (inline) | `AclSummaryDto` | **Fixed** | `roles: Map<String,Int>` -> `roles: List<String>` |
| DuplicateUrlCheckData | `DuplicateUrlCheckDataDto` | Aligned | |
| SyncSessionData | `SyncSessionResponseDto` | Aligned | Field names match |
| FullSyncResponseData | `FullSyncResponseDto` | Aligned | |
| DeltaSyncResponseData | `DeltaSyncResponseDto` | Aligned | |
| SyncApplyRequest | `SyncApplyRequestDto` | Aligned | |
| SyncApplyResult | `SyncApplyResultDto` | Aligned | |
| HealthComponentStatus | N/A | Skipped | System/admin endpoint |
| DetailedHealthData | N/A | Skipped | System/admin endpoint |
| ReadinessData | N/A | Skipped | System/admin endpoint |
| LivenessData | N/A | Skipped | System/admin endpoint |
| BaseResponse | `BaseResponse` | Aligned | |
| SuccessResponse | `SuccessResponse` | Aligned | |

## Removed DTOs

| DTO | Reason |
|-----|--------|
| `CollectionTreeNodeDto` | Spec uses `Collection` schema for tree nodes |
| `SummarySimpleDto` | Was only used in `RequestDetailDto.summary`; replaced with `SummaryCompactDto` |

## Domain Model Changes

| Model | Change | Reason |
|-------|--------|--------|
| `Collection` | `isPublic: Boolean` -> `isShared: Boolean` | Spec uses `is_shared`, not `is_public` |
| `Collection` | Removed `ownerId: Long?` | Not in spec |
| `CollectionAcl` | `userId: Int` -> `userId: Int?` | Spec marks as nullable |
