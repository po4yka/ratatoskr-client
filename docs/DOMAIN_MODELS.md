# Domain Models Reference

Catalog of domain entities in this project.

## Model Inventory

### Core Models

| Model | Purpose | Key Fields |
|-------|---------|------------|
| `Summary` | Article/video summary | id, title, content, sourceUrl, tags, isRead |
| `User` | User profile | id, username, email |
| `Request` | URL submission request | id, url, status, createdAt |
| `Collection` | User-created collection | id, name, description, summaryIds |

### Authentication

| Model | Purpose | Key Fields |
|-------|---------|------------|
| `AuthTokens` | JWT tokens | accessToken, refreshToken, expiresAt |
| `Session` | User session | id, deviceInfo, createdAt |
| `TelegramLinkStatus` | Telegram link state | isLinked, username |

### Sync Models

| Model | Purpose | Key Fields |
|-------|---------|------------|
| `SyncState` | Last sync info | lastSyncTime, lastSyncHash |
| `SyncProgress` | Ongoing sync progress | phase, totalItems, processedItems, errorCount |
| `SyncResult` | Sync operation result | createdCount, updatedCount, deletedCount, hasMore |
| `SyncConflict` | Detected conflict | id, entityType, clientVersion, serverVersion |

### Enums

| Enum | Values |
|------|--------|
| `SyncPhase` | CREATING_SESSION, FETCHING_FULL, FETCHING_DELTA, PROCESSING, VALIDATING, COMPLETED, FAILED, CANCELLED |
| `RequestStatus` | PENDING, PROCESSING, COMPLETED, FAILED |
| `ProcessingStage` | QUEUED, EXTRACTING, SUMMARIZING, STORING, DONE, ERROR |

### Supporting Models

| Model | Purpose |
|-------|---------|
| `SearchQuery` | Search parameters |
| `UserPreferences` | User settings |
| `UserStats` | Usage statistics |
| `CollectionAcl` | Collection access control |

## Location

Shared immutable models live in `core/common/src/commonMain/kotlin/com/po4yka/bitesizereader/domain/`. Feature-owned public contracts live beside their owning feature in `feature/<name>/.../api` or `.../domain`.

## Example: Summary

```kotlin
data class Summary(
    val id: String,
    val title: String,
    val content: String,
    val sourceUrl: String,
    val imageUrl: String?,
    val createdAt: Instant,
    val isRead: Boolean,
    val tags: List<String>,
)
```

## Example: SyncProgress

```kotlin
data class SyncProgress(
    val phase: SyncPhase,
    val totalItems: Int? = null,
    val processedItems: Int = 0,
    val currentBatch: Int = 0,
    val totalBatches: Int? = null,
    val errorCount: Int = 0,
    val startTime: Instant,
    val errorMessage: String? = null,
) {
    val progressFraction: Float?  // 0.0 to 1.0
    val isInProgress: Boolean
}
```

## Example: SyncPhase

```kotlin
enum class SyncPhase {
    CREATING_SESSION,  // Creating sync session
    FETCHING_FULL,     // Full sync download
    FETCHING_DELTA,    // Delta sync (changes only)
    PROCESSING,        // Processing items
    VALIDATING,        // Validating integrity
    COMPLETED,         // Success
    FAILED,            // Error
    CANCELLED,         // User cancelled
}
```

## Conventions

- All models are `data class` (immutable)
- Use `Instant` from `kotlin.time` for timestamps
- Use nullable types (`?`) for optional fields
- No platform-specific imports

---

**Related**: [REPOSITORY_PATTERNS.md](REPOSITORY_PATTERNS.md) | [USE_CASE_GUIDE.md](USE_CASE_GUIDE.md)
