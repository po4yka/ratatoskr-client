# Use Case Development Guide

Quick reference for implementing domain use cases in this project.

## Structure Template

```kotlin
package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.MyRepository
import org.koin.core.annotation.Factory

@Factory
class MyUseCase(private val repository: MyRepository) {

    // Option 1: Suspend function for one-shot operations
    suspend operator fun invoke(param: String): Result {
        return repository.doSomething(param)
    }

    // Option 2: Flow for observable data
    operator fun invoke(): Flow<Data> {
        return repository.observeData()
    }
}
```

## Key Rules

| Rule | Description |
|------|-------------|
| `@Factory` annotation | New instance per injection (stateless) |
| Constructor injection | All dependencies via constructor |
| Single responsibility | One use case = one business operation |
| `operator fun invoke` | Enables `useCase()` call syntax |
| No Android/iOS imports | Keep use cases platform-agnostic |

## Return Type Patterns

### One-Shot Operations (suspend)

```kotlin
@Factory
class SubmitUrlUseCase(private val repository: RequestRepository) {
    suspend operator fun invoke(url: String): Request {
        return repository.submitUrl(url)
    }
}
```

### Observable Data (Flow)

```kotlin
@Factory
class GetSummariesUseCase(private val repository: SummaryRepository) {
    operator fun invoke(page: Int, pageSize: Int): Flow<List<Summary>> {
        return repository.getSummaries(page, pageSize)
    }
}
```

### With State Exposure

```kotlin
@Factory
class SyncDataUseCase(private val repository: SyncRepository) {
    // Expose repository's state flow
    val syncProgress: StateFlow<SyncProgress?> = repository.syncProgress

    suspend operator fun invoke(forceFull: Boolean = false) {
        repository.sync(forceFull = forceFull)
    }

    fun cancelSync() {
        repository.cancelSync()
    }
}
```

## Error Handling

Use cases typically throw exceptions (caught by ViewModel):

```kotlin
@Factory
class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(credentials: Credentials): User {
        // Throws on failure - ViewModel handles via runCatching
        return repository.login(credentials)
    }
}
```

## Multi-Step Operations

For complex operations, coordinate multiple repositories:

```kotlin
@Factory
class LinkTelegramUseCase(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend fun begin(): String {
        return authRepository.createLinkNonce()
    }

    suspend fun complete(nonce: String, authData: TelegramAuth): LinkStatus {
        authRepository.validateNonce(nonce)
        return userRepository.linkTelegram(authData)
    }
}
```

## File Location

Use cases live in the owning feature module under `feature/<name>/.../domain/usecase/`, or in `core/common` only when the behavior is intentionally cross-feature.

## Naming Convention

| Operation | Name Pattern |
|-----------|--------------|
| Get data | `Get{Entity}UseCase` |
| Create | `Create{Entity}UseCase` |
| Update | `Update{Entity}UseCase` |
| Delete | `Delete{Entity}UseCase` |
| Action | `{Action}{Entity}UseCase` |

Examples: `GetSummariesUseCase`, `MarkSummaryAsReadUseCase`, `SyncDataUseCase`

## Existing Use Cases (33 total)

Categories:
- **Summaries**: GetSummaries, GetSummaryById, MarkAsRead, Delete, Search
- **Auth**: LoginWithTelegram, LoginWithGoogle, LoginWithApple, Logout
- **Sync**: SyncData
- **Collections**: GetCollection, GetCollectionItems, UpdateCollection, DeleteCollection
- **User**: GetCurrentUser, GetUserPreferences, UpdateUserPreferences, DeleteAccount

---

**Related**: [VIEWMODEL_GUIDE.md](VIEWMODEL_GUIDE.md) | [REPOSITORY_PATTERNS.md](REPOSITORY_PATTERNS.md)
