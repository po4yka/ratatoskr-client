# Repository Pattern Guide

Quick reference for implementing repositories in this project.

## Structure

```
domain/repository/MyRepository.kt     <- Interface
data/repository/MyRepositoryImpl.kt   <- Implementation
```

## Interface Template

```kotlin
// domain/repository/MyRepository.kt
package com.po4yka.bitesizereader.domain.repository

interface MyRepository {
    // Observable data
    fun getData(): Flow<Data>

    // One-shot operations
    suspend fun createItem(item: Item): Item
    suspend fun updateItem(id: Long, item: Item)
    suspend fun deleteItem(id: Long)
}
```

## Implementation Template

```kotlin
// data/repository/MyRepositoryImpl.kt
package com.po4yka.bitesizereader.data.repository

import org.koin.core.annotation.Single

@Single
class MyRepositoryImpl(
    private val database: Database,
    private val api: MyApi,
) : MyRepository {

    override fun getData(): Flow<Data> {
        // Offline-first: read from local DB
        return database.myQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun createItem(item: Item): Item {
        // 1. Send to server
        val dto = api.create(item.toDto())
        // 2. Save to local DB
        database.myQueries.insert(dto.toEntity())
        return dto.toDomain()
    }
}
```

## Koin Binding

Use `@Single` with interface binding:

```kotlin
@Single(binds = [MyRepository::class])
class MyRepositoryImpl(...) : MyRepository
```

Or with `@ComponentScan` in module:

```kotlin
@Module
@ComponentScan("com.po4yka.bitesizereader.data.repository")
class RepositoryModule
```

## Offline-First Pattern

```
Read Request:
  Local DB --> Return cached data --> Background refresh --> Update UI

Write Request:
  API call --> On success --> Update Local DB --> Return result
```

## Data Flow Layers

```
API Response (DTO)
       |
       v   [Mapper: toEntity()]
Local DB (Entity)
       |
       v   [Mapper: toDomain()]
Domain Model
```

## Example: Repository with State

```kotlin
@Single
class SyncRepositoryImpl(
    private val database: Database,
    private val api: SyncApi,
) : SyncRepository {

    // Expose observable state
    private val _syncProgress = MutableStateFlow<SyncProgress?>(null)
    override val syncProgress: StateFlow<SyncProgress?> = _syncProgress.asStateFlow()

    // Cancellation support
    private var currentJob: Job? = null

    override fun cancelSync() {
        currentJob?.cancel()
        _syncProgress.value = _syncProgress.value?.copy(phase = SyncPhase.CANCELLED)
    }

    override suspend fun sync(forceFull: Boolean) {
        // Implementation with progress updates
    }
}
```

## Existing Repositories

| Repository | Purpose |
|------------|---------|
| `SummaryRepository` | Summary CRUD and offline cache |
| `AuthRepository` | Authentication and token management |
| `UserRepository` | User profile and preferences |
| `CollectionRepository` | Collections management |
| `RequestRepository` | URL submission requests |
| `SearchRepository` | Full-text search |
| `SyncRepository` | Data synchronization |

## File Locations

| File | Location |
|------|----------|
| Interfaces | `feature/<name>/.../domain/repository/` |
| Implementations | `feature/<name>/.../data/repository/` |
| DTOs | `feature/<name>/.../data/remote/dto/` |
| Mappers | `feature/<name>/.../data/mappers/` |

---

**Related**: [USE_CASE_GUIDE.md](USE_CASE_GUIDE.md) | [VIEWMODEL_GUIDE.md](VIEWMODEL_GUIDE.md)
