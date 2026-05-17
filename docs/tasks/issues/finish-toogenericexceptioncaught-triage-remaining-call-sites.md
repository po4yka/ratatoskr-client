---
title: Finish TooGenericExceptionCaught triage across remaining call sites
status: backlog
area: kmp
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Finish TooGenericExceptionCaught triage across remaining call sites #repo/ratatoskr-client #area/kmp #status/backlog 🔽

Follow-up to `triage-too-generic-exception-caught-suppressions` (landed the `runCatchingDomain { ... }` helper in `core/common/util/error/` with 5 contract tests covering CancellationException rethrow + child-coroutine cancellation propagation, plus tightened the top-5 offenders: `DigestViewModel` (-7), `CollectionViewViewModel` (-8), `SummaryDetailViewModel` (-4), `SearchViewModel` (-4), `AuthViewModel` (-4) — 24 of 67 suppressions removed, a ~36% reduction).

## Objective

Drive the remaining ~43 `@Suppress("TooGenericExceptionCaught")` occurrences down to the ≤16 baseline cap (75% total reduction from 67) by migrating call sites to the new `runCatchingDomain` helper.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

Mechanical conversion at each remaining site:

- Repositories: `feature/summary/data/repository/SummaryRepositoryImpl.kt`, `HighlightRepositoryImpl.kt`, `SearchRepositoryImpl.kt`; `feature/sync/data/repository/SyncRepositoryImpl.kt`, `PendingOperationFlusher.kt`.
- Delegates: `feature/summary/presentation/viewmodel/{CollectionDelegate,FeedbackDelegate,ReadingSessionDelegate,AudioDelegate,SummarySearchDelegate}.kt`.
- ViewModels: `feature/summary/presentation/viewmodel/{SummaryListViewModel,SummaryListActionHandler,RecommendationsViewModel}.kt`, `feature/digest/.../viewmodel/{CustomDigestCreateViewModel,CustomDigestViewViewModel}.kt`, `feature/collections/.../viewmodel/CollectionsViewModel.kt`.
- Sync appliers + handlers: `feature/summary/.../data/sync/{SummarySyncItemAppliers,SummaryPendingOperationHandlers}.kt`, `feature/collections/.../data/sync/TagSyncItemAppliers.kt`, `feature/sync/.../data/mappers/SyncMapper.kt`, `feature/sync/.../domain/usecase/SyncDataUseCase.kt`.
- Worker: `androidApp/.../worker/SyncWorker.kt`.

The conversion template:

```kotlin
// before
@Suppress("TooGenericExceptionCaught")
fun X() { viewModelScope.launch { try { work() } catch (e: Exception) { handle(e) } } }

// after
fun X() { viewModelScope.launch { runCatchingDomain { work() }.onFailure(::handle) } }
```

## Constraints

- Preserve observable error-handling behavior — same `toUserMessage()`/`toAppError()` mapping, same retry policy.
- Always rely on `runCatchingDomain` to rethrow `CancellationException`; do not introduce a fresh `catch (CancellationException) { throw it }` block at call sites.
- Repositories that catch and rethrow as `AppError` subtypes should keep their narrow `try { ... } catch (e: SQLiteException)` patterns and only suppress the wide catch — they aren't candidates for `runCatchingDomain` if they don't surface `Result`.

## Definition of done

- `rg -c '@Suppress\("TooGenericExceptionCaught"\)' --type kotlin | awk -F: '{s+=\$2}END{print s}'` ≤ 16 (75% reduction from the 67-baseline).
- `./gradlew detekt` green.
- All feature module `desktopTest` / `allTests` runs green.
- No regression in error-handling tests (`SummaryDetailViewModelTest`, `DigestViewModelTest`, etc.).
