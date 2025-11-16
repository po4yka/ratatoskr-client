# Sync Strategy

Comprehensive guide for implementing offline-first synchronization with the Bite-Size Reader backend.

## Table of Contents

1. [Overview](#overview)
2. [Sync Architecture](#sync-architecture)
3. [Full Sync](#full-sync)
4. [Delta Sync](#delta-sync)
5. [Conflict Resolution](#conflict-resolution)
6. [Background Sync](#background-sync)
7. [Performance Optimization](#performance-optimization)
8. [Implementation Guide](#implementation-guide)

---

## Overview

The sync strategy enables offline-first functionality by maintaining a local copy of data that synchronizes with the backend.

### Sync Objectives

1. **Offline Access**: Users can read summaries without internet connection
2. **Automatic Sync**: Data syncs in background without user intervention
3. **Minimal Bandwidth**: Only sync changes since last update
4. **Consistency**: Resolve conflicts when local/server data diverges
5. **Battery Efficiency**: Optimize sync timing and frequency

### Sync Types

| Type | When to Use | Data Volume | Network | Battery Impact |
|------|------------|-------------|---------|----------------|
| **Full Sync** | First install, after logout, corrupted local DB | All summaries | High | High |
| **Delta Sync** | Regular updates | Only changes | Low | Low |
| **On-Demand** | Manual refresh | Varies | Varies | Medium |
| **Upload Changes** | Local modifications | Modified records | Low | Low |

---

## Sync Architecture

### Data Flow

```
┌─────────────────────────────────────────────────────────┐
│                    Mobile Client                        │
│                                                         │
│  ┌───────────────┐      ┌──────────────┐              │
│  │  UI Layer     │◄─────┤  ViewModel   │              │
│  └───────────────┘      └──────┬───────┘              │
│                                 │                       │
│                         ┌───────▼────────┐             │
│                         │  Use Cases     │             │
│                         └───────┬────────┘             │
│                                 │                       │
│  ┌──────────────────────────────▼──────────────────┐   │
│  │          Repository (Store Pattern)             │   │
│  │                                                  │   │
│  │  ┌──────────────┐         ┌─────────────────┐  │   │
│  │  │              │         │                 │  │   │
│  │  │   Local DB   │◄────────►   Remote API   │  │   │
│  │  │  (SQLDelight)│         │     (Ktor)      │  │   │
│  │  │              │         │                 │  │   │
│  │  └──────────────┘         └─────────────────┘  │   │
│  │         ▲                          ▲            │   │
│  │         │                          │            │   │
│  │    ┌────▼──────────────────────────▼────┐      │   │
│  │    │       Sync Manager                 │      │   │
│  │    │  - Full sync                       │      │   │
│  │    │  - Delta sync                      │      │   │
│  │    │  - Conflict resolution             │      │   │
│  │    └────────────────────────────────────┘      │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### Sync State Machine

```
┌─────────┐
│  IDLE   │
└────┬────┘
     │ sync()
     ▼
┌─────────┐
│SYNCING  │──────── Error ─────► ERROR
└────┬────┘                        │
     │ Success                     │ retry()
     ▼                            ▼
┌─────────┐                  ┌─────────┐
│SUCCESS  │                  │RETRYING │
└─────────┘                  └─────────┘
```

**State Model**:

```kotlin
// domain/model/SyncState.kt
sealed class SyncState {
    object Idle : SyncState()

    data class Syncing(
        val type: SyncType,
        val progress: Int,  // 0-100
        val currentStep: String
    ) : SyncState()

    data class Success(
        val type: SyncType,
        val syncedAt: Instant,
        val itemsSynced: Int
    ) : SyncState()

    data class Error(
        val type: SyncType,
        val error: String,
        val canRetry: Boolean
    ) : SyncState()

    data class Retrying(
        val type: SyncType,
        val attemptNumber: Int,
        val maxAttempts: Int
    ) : SyncState()
}

enum class SyncType {
    FULL,
    DELTA,
    UPLOAD
}
```

---

## Full Sync

Full synchronization downloads entire summary database to local storage.

### When to Trigger

1. **First app launch** (no local data)
2. **After user logout** (clear local data)
3. **Database corruption** detected
4. **User manual refresh** (pull-to-refresh with force sync)
5. **Delta sync failure** (fallback to full sync)

### Full Sync Flow

```
1. Check if full sync needed
   └─► GET /v1/sync/full
       └─► Receive sync session ID + total chunks

2. Download chunks sequentially
   └─► GET /v1/sync/full/{sync_id}/chunk/{chunk_num}
       └─► Receive chunk data (summaries batch)
       └─► Save to local DB
       └─► Update progress (chunk_num / total_chunks)

3. Finalize sync
   └─► Update last_sync_timestamp
   └─► Mark sync complete
```

### Chunked Download

Backend returns data in manageable chunks to avoid large payloads:

**Chunk Size**: 50 summaries per chunk

**Request**:
```http
GET /v1/sync/full
Authorization: Bearer <token>
```

**Response**:
```json
{
  "success": true,
  "data": {
    "sync_id": "sync-abc123",
    "total_summaries": 247,
    "chunks": 5,
    "chunk_size": 50,
    "expires_at": "2025-01-16T12:00:00Z"
  }
}
```

**Download Chunk**:
```http
GET /v1/sync/full/sync-abc123/chunk/1
```

**Response**:
```json
{
  "success": true,
  "data": {
    "sync_id": "sync-abc123",
    "chunk_number": 1,
    "total_chunks": 5,
    "summaries": [
      { /* full summary object */ },
      ...
    ]
  }
}
```

### Implementation

```kotlin
// data/repository/SyncRepositoryImpl.kt
class SyncRepositoryImpl(
    private val syncApi: SyncApi,
    private val localDb: Database,
    private val preferences: SyncPreferences
) : SyncRepository {

    override suspend fun performFullSync(): Flow<SyncState> = flow {
        emit(SyncState.Syncing(SyncType.FULL, 0, "Initializing"))

        try {
            // 1. Initiate full sync
            val session = syncApi.initiateFull Sync()
            val totalChunks = session.chunks

            // 2. Download chunks
            val allSummaries = mutableListOf<SummaryDetailDto>()

            for (chunkNum in 1..totalChunks) {
                emit(SyncState.Syncing(
                    type = SyncType.FULL,
                    progress = ((chunkNum - 1) * 100) / totalChunks,
                    currentStep = "Downloading chunk $chunkNum of $totalChunks"
                ))

                val chunk = syncApi.getChunk(session.syncId, chunkNum)
                allSummaries.addAll(chunk.summaries)
            }

            // 3. Clear and repopulate local database
            emit(SyncState.Syncing(
                type = SyncType.FULL,
                progress = 90,
                currentStep = "Saving to local database"
            ))

            localDb.transaction {
                localDb.clearAllSummaries()
                allSummaries.forEach { dto ->
                    localDb.insertSummary(dto.toDomain())
                }
            }

            // 4. Update sync metadata
            preferences.setLastFullSync(Clock.System.now())
            preferences.setLastSyncTimestamp(Clock.System.now())

            emit(SyncState.Success(
                type = SyncType.FULL,
                syncedAt = Clock.System.now(),
                itemsSynced = allSummaries.size
            ))

        } catch (e: Exception) {
            emit(SyncState.Error(
                type = SyncType.FULL,
                error = e.message ?: "Full sync failed",
                canRetry = e !is ValidationError
            ))
        }
    }
}
```

---

## Delta Sync

Incremental synchronization downloads only changes since last sync.

### When to Trigger

1. **App foreground** (if >5 minutes since last sync)
2. **Background periodic sync** (every 15-30 minutes)
3. **User pull-to-refresh**
4. **After submitting new URL** (to fetch result)

### Delta Sync Flow

```
1. Get last sync timestamp from local preferences
   └─► Read: last_sync_timestamp

2. Request delta changes
   └─► GET /v1/sync/delta?since=<timestamp>
       └─► Receive: new summaries, updated summaries, deleted IDs

3. Apply changes locally
   └─► Insert new summaries
   └─► Update existing summaries
   └─► Delete summaries by ID

4. Update last sync timestamp
   └─► Write: last_sync_timestamp = current_time
```

### API Request

```http
GET /v1/sync/delta?since=2025-01-15T10:00:00Z
Authorization: Bearer <token>
```

**Response**:
```json
{
  "success": true,
  "data": {
    "summaries": [
      {
        "id": 1,
        "action": "update",
        "data": { /* full summary object */ }
      },
      {
        "id": 5,
        "action": "insert",
        "data": { /* full summary object */ }
      }
    ],
    "deleted_ids": [42, 43],
    "sync_timestamp": "2025-01-16T12:00:00Z"
  }
}
```

### Implementation

```kotlin
override suspend fun performDeltaSync(): Flow<SyncState> = flow {
    emit(SyncState.Syncing(SyncType.DELTA, 0, "Checking for updates"))

    try {
        // 1. Get last sync timestamp
        val lastSync = preferences.getLastSyncTimestamp()
            ?: run {
                // No previous sync - do full sync instead
                emitAll(performFullSync())
                return@flow
            }

        // 2. Fetch delta changes
        emit(SyncState.Syncing(SyncType.DELTA, 30, "Fetching changes"))

        val delta = syncApi.getDeltaSync(lastSync.toString())

        // 3. Apply changes to local database
        emit(SyncState.Syncing(SyncType.DELTA, 60, "Applying changes"))

        var itemsChanged = 0

        localDb.transaction {
            // Handle updates and inserts
            delta.summaries.forEach { change ->
                when (change.action) {
                    "insert" -> {
                        change.data?.let {
                            localDb.insertSummary(it.toDomain())
                            itemsChanged++
                        }
                    }
                    "update" -> {
                        change.data?.let {
                            localDb.updateSummary(it.toDomain())
                            itemsChanged++
                        }
                    }
                }
            }

            // Handle deletions
            delta.deletedIds.forEach { id ->
                localDb.deleteSummary(id)
                itemsChanged++
            }
        }

        // 4. Update sync timestamp
        preferences.setLastSyncTimestamp(
            Instant.parse(delta.syncTimestamp)
        )

        emit(SyncState.Success(
            type = SyncType.DELTA,
            syncedAt = Clock.System.now(),
            itemsSynced = itemsChanged
        ))

    } catch (e: Exception) {
        emit(SyncState.Error(
            type = SyncType.DELTA,
            error = e.message ?: "Delta sync failed",
            canRetry = true
        ))
    }
}
```

---

## Conflict Resolution

Handle conflicts when local and server data diverge.

### Conflict Scenarios

1. **User marks summary as read locally** (offline) → Server hasn't seen the change
2. **Server summary updated** → Local summary also modified
3. **Summary deleted on server** → User has local modifications

### Resolution Strategy

**Server Wins for Content**:
- Summary content (title, tldr, key_ideas, etc.) always from server
- Server is source of truth for summary JSON

**Client Wins for User Actions**:
- `is_read` status preserved from client
- User's reading progress takes precedence

**Last-Write-Wins for Timestamps**:
- Compare `updated_at` timestamps
- Most recent change wins

### Implementation

```kotlin
// data/repository/ConflictResolver.kt
class ConflictResolver {

    data class SummaryConflict(
        val localSummary: Summary,
        val serverSummary: Summary
    )

    fun resolve(conflict: SummaryConflict): Summary {
        val local = conflict.localSummary
        val server = conflict.serverSummary

        // Server wins for content fields
        return server.copy(
            // Preserve client-side user actions
            isRead = local.isRead,

            // Use most recent timestamp
            updatedAt = maxOf(
                local.updatedAt ?: Instant.DISTANT_PAST,
                server.updatedAt ?: Instant.DISTANT_PAST
            )
        )
    }
}

// Usage in delta sync
delta.summaries.forEach { change ->
    when (change.action) {
        "update" -> {
            val serverSummary = change.data?.toDomain()
            val localSummary = localDb.getSummaryById(change.id)

            if (localSummary != null && serverSummary != null) {
                // Conflict detected - resolve
                val resolved = conflictResolver.resolve(
                    SummaryConflict(localSummary, serverSummary)
                )
                localDb.updateSummary(resolved)
            } else {
                // No conflict - direct update
                serverSummary?.let { localDb.updateSummary(it) }
            }
        }
    }
}
```

### Upload Local Changes

Upload user modifications to server:

```kotlin
override suspend fun uploadLocalChanges(): Flow<SyncState> = flow {
    emit(SyncState.Syncing(SyncType.UPLOAD, 0, "Collecting changes"))

    // 1. Get locally modified summaries
    val modifiedSummaries = localDb.getModifiedSummaries()

    if (modifiedSummaries.isEmpty()) {
        emit(SyncState.Success(
            type = SyncType.UPLOAD,
            syncedAt = Clock.System.now(),
            itemsSynced = 0
        ))
        return@flow
    }

    // 2. Upload changes
    emit(SyncState.Syncing(
        type = SyncType.UPLOAD,
        progress = 30,
        currentStep = "Uploading ${modifiedSummaries.size} changes"
    ))

    val changes = modifiedSummaries.map { summary ->
        SyncUploadChange(
            summaryId = summary.id,
            action = "update",
            fields = mapOf("is_read" to summary.isRead),
            clientTimestamp = Clock.System.now().toString()
        )
    }

    syncApi.uploadChanges(SyncUploadRequest(
        changes = changes,
        deviceId = preferences.getDeviceId(),
        lastSync = preferences.getLastSyncTimestamp().toString()
    ))

    // 3. Mark as synced
    localDb.markSummariesAsSynced(modifiedSummaries.map { it.id })

    emit(SyncState.Success(
        type = SyncType.UPLOAD,
        syncedAt = Clock.System.now(),
        itemsSynced = modifiedSummaries.size
    ))
}
```

---

## Background Sync

Automatic synchronization in the background without user interaction.

### Platform Implementations

#### Android (WorkManager)

```kotlin
// androidMain/kotlin/sync/SyncWorker.kt
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val syncRepository = KoinJavaComponent.inject<SyncRepository>(SyncRepository::class.java)

        return try {
            syncRepository.value.performDeltaSync().collect { state ->
                when (state) {
                    is SyncState.Success -> {
                        setProgress(workDataOf("status" to "completed"))
                    }
                    is SyncState.Error -> {
                        setProgress(workDataOf("status" to "error", "message" to state.error))
                    }
                    is SyncState.Syncing -> {
                        setProgress(workDataOf("progress" to state.progress))
                    }
                    else -> {}
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

// Schedule periodic sync
fun schedulePeriodicSync(context: Context) {
    val syncWork = PeriodicWorkRequestBuilder<SyncWorker>(
        repeatInterval = 30,
        repeatIntervalTimeUnit = TimeUnit.MINUTES
    )
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        )
        .setBackoffCriteria(
            BackoffPolicy.EXPONENTIAL,
            WorkRequest.MIN_BACKOFF_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .build()

    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "sync_summaries",
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
}
```

#### iOS (Background Tasks)

```swift
// iosApp/Background/BackgroundSyncTask.swift
import BackgroundTasks

class BackgroundSyncManager {
    static let shared = BackgroundSyncManager()
    private let taskIdentifier = "com.bitesizereader.sync"

    func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(
            forTaskWithIdentifier: taskIdentifier,
            using: nil
        ) { task in
            self.handleSyncTask(task: task as! BGAppRefreshTask)
        }
    }

    func handleSyncTask(task: BGAppRefreshTask) {
        scheduleNextSync()

        let syncRepository = // Get from Koin

        Task {
            do {
                for await state in syncRepository.performDeltaSync() {
                    if case .success = state {
                        task.setTaskCompleted(success: true)
                        return
                    } else if case .error = state {
                        task.setTaskCompleted(success: false)
                        return
                    }
                }
            } catch {
                task.setTaskCompleted(success: false)
            }
        }

        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }
    }

    func scheduleNextSync() {
        let request = BGAppRefreshTaskRequest(identifier: taskIdentifier)
        request.earliestBeginDate = Date(timeIntervalSinceNow: 30 * 60) // 30 minutes

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Failed to schedule background sync: \(error)")
        }
    }
}
```

### Sync Triggers

```kotlin
// Sync trigger logic
class SyncTriggerManager(
    private val syncRepository: SyncRepository,
    private val preferences: SyncPreferences
) {
    suspend fun shouldTriggerSync(): Boolean {
        val lastSync = preferences.getLastSyncTimestamp() ?: return true

        val timeSinceLastSync = Clock.System.now() - lastSync

        return when {
            // No sync in last 6 hours - definitely sync
            timeSinceLastSync > 6.hours -> true

            // App in foreground, >5 minutes since last sync
            timeSinceLastSync > 5.minutes -> true

            else -> false
        }
    }

    suspend fun triggerSyncIfNeeded() {
        if (shouldTriggerSync()) {
            syncRepository.performDeltaSync().collect { /* handle state */ }
        }
    }
}
```

---

## Performance Optimization

### Batch Database Operations

Use transactions for bulk inserts/updates:

```kotlin
localDb.transaction {
    summaries.forEach { summary ->
        insertSummary(summary)
    }
}
// vs single insert (slow)
summaries.forEach { summary ->
    localDb.insertSummary(summary)
}
```

### Pagination for Large Syncs

Don't load entire dataset into memory:

```kotlin
// Process chunks as they arrive
flow {
    for (chunkNum in 1..totalChunks) {
        val chunk = syncApi.getChunk(syncId, chunkNum)

        // Process and save immediately
        localDb.transaction {
            chunk.summaries.forEach { dto ->
                insertSummary(dto.toDomain())
            }
        }

        emit(progress = chunkNum * 100 / totalChunks)
    }
}
```

### Network Optimization

**Compression**: Request gzip encoding:

```kotlin
val httpClient = HttpClient {
    install(ContentEncoding) {
        gzip()
        deflate()
    }
}
```

**Conditional Requests**: Use ETags for caching:

```kotlin
val etag = preferences.getDeltaSyncETag()

val response = client.get("/v1/sync/delta") {
    parameter("since", lastSync)
    etag?.let { header("If-None-Match", it) }
}

if (response.status == HttpStatusCode.NotModified) {
    // No changes - skip processing
    return
}

// Save new ETag
preferences.setDeltaSyncETag(response.headers["ETag"])
```

### Battery Optimization

**Defer non-critical syncs**:

```kotlin
fun shouldSyncNow(): Boolean {
    return when {
        isCharging() -> true  // Sync freely when charging
        batteryLevel() > 50 -> true  // Sync if good battery
        timeSinceLastSync() > 1.days -> true  // Force if very stale
        else -> false  // Defer until better conditions
    }
}
```

---

## Implementation Guide

### Step 1: Setup Sync Preferences

```kotlin
// data/local/SyncPreferences.kt
interface SyncPreferences {
    fun getLastSyncTimestamp(): Instant?
    fun setLastSyncTimestamp(timestamp: Instant)
    fun getLastFullSync(): Instant?
    fun setLastFullSync(timestamp: Instant)
    fun getDeviceId(): String
    fun getDeltaSyncETag(): String?
    fun setDeltaSyncETag(etag: String)
}

// Platform-specific implementations using SharedPreferences/UserDefaults
```

### Step 2: Implement Sync Repository

```kotlin
// domain/repository/SyncRepository.kt
interface SyncRepository {
    suspend fun performFullSync(): Flow<SyncState>
    suspend fun performDeltaSync(): Flow<SyncState>
    suspend fun uploadLocalChanges(): Flow<SyncState>
    suspend fun cancelSync()
}
```

### Step 3: Create Sync Use Case

```kotlin
// domain/usecase/SyncDataUseCase.kt
class SyncDataUseCase(
    private val syncRepository: SyncRepository,
    private val preferences: SyncPreferences
) {
    suspend operator fun invoke(force: Boolean = false): Flow<SyncState> {
        return if (force || needsFullSync()) {
            syncRepository.performFullSync()
        } else {
            syncRepository.performDeltaSync()
        }
    }

    private fun needsFullSync(): Boolean {
        val lastFullSync = preferences.getLastFullSync()
        return lastFullSync == null ||
               (Clock.System.now() - lastFullSync) > 7.days
    }
}
```

### Step 4: Handle Sync in ViewModel

```kotlin
// presentation/viewmodel/SummaryListViewModel.kt
class SummaryListViewModel(
    private val syncDataUseCase: SyncDataUseCase
) : ViewModel() {

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    fun sync(force: Boolean = false) {
        viewModelScope.launch {
            syncDataUseCase(force).collect { state ->
                _syncState.value = state

                // Auto-refresh list on successful sync
                if (state is SyncState.Success) {
                    loadSummaries()
                }
            }
        }
    }

    fun refresh() {
        sync(force = false)
    }
}
```

### Step 5: Setup Background Sync

**Android**:
```kotlin
// In Application class
WorkManagerSyncScheduler.schedulePeriodicSync(this)
```

**iOS**:
```swift
// In AppDelegate
BackgroundSyncManager.shared.registerBackgroundTasks()
```

---

**Last Updated**: 2025-11-16
