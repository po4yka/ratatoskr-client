---
name: kotlin-coroutines
description: Use when launching coroutines, owning a `CoroutineScope`, catching broad exceptions, writing repository / service APIs that need async work, or handling cancellation. Trigger on `viewModelScope`, `CoroutineScope`, `launch`, `runBlocking`, `runCatching`, `catch (Exception)`, `catch (Throwable)`, `CancellationException`, `Mutex`, `@Volatile`, or any sync-orchestration question. Ratatoskr has stricter rules than a generic structured-concurrency guide — codified here.
user-invocable: false
---

# Coroutines & Structured Concurrency

The Ratatoskr codebase follows a strict form of structured concurrency: only one
class owns a `CoroutineScope` (`BaseViewModel`), repositories and services
expose `suspend` APIs exclusively, and every broad-catch site rethrows
`CancellationException` first. This skill codifies the rules and points at the
canonical helpers so new code doesn't drift.

## The one place a scope is constructed

`BaseViewModel` in
`core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/viewmodel/BaseViewModel.kt`
is the **only** class in the codebase that builds a `CoroutineScope`:

```kotlin
protected val viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)
```

Notes:

- `SupervisorJob` — a failed child does not take siblings down.
- `dispatcher = Dispatchers.Default` (not `Dispatchers.Main`) for KMP
  compatibility. Tests pass a `TestDispatcher` for deterministic behaviour.
- Cancelled automatically by Decompose's `InstanceKeeper.Instance.onDestroy()`
  when the owning component is destroyed — no manual lifecycle code per
  ViewModel.

Subclasses call `viewModelScope.launch { … }`. Never construct your own scope in
a subclass.

## Rules for everything else

### Repositories, services, use cases: `suspend` only

Public APIs on repositories, services, and use cases are `suspend` functions or
`Flow`-returning. They never:

- Store a `CoroutineScope` field.
- Construct their own scope (`MainScope()`, `CoroutineScope(...)`, `GlobalScope`).
- Launch in `init { … }`.
- Fire-and-forget from a non-suspend public method.

The caller's coroutine owns cancellation. If the caller cancels, the work
stops. There are currently zero violations across all features. The rule is
"keep it that way", not "fix existing problems".

### Broad catches: use `runCatchingDomain`, not hand-rolled try/catch

`core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/util/error/RunCatchingDomain.kt`
is the canonical broad-catch wrapper. It rethrows `CancellationException`
first and only then captures `Throwable` into `Result.failure`:

```kotlin
runCatchingDomain { work() }
    .onSuccess(::onSuccess)
    .onFailure { e -> onFailure(e.toUserMessage()) }
```

There is exactly one `@Suppress("TooGenericExceptionCaught")` in the codebase
(inside that helper). Call sites stay clean — no suppressions, no hand-rolled
rethrow.

If you genuinely need a try/catch that does something other than wrap into a
`Result`, the pattern is:

```kotlin
try {
    work()
} catch (ce: CancellationException) {
    throw ce
} catch (t: Throwable) {
    // domain-specific handling
}
```

`CancellationException` **must** be caught and rethrown first. Reference
implementations include `core/data/.../ApiClient.kt`'s `HttpCallValidator`,
`feature/sync/.../PendingOperationFlusher.kt`'s queue drain, and
`feature/summary/.../SearchRepositoryImpl.kt`'s search timeout fall-through.

### `CancellationException` import: kotlin.coroutines.cancellation, not kotlinx

Always:

```kotlin
import kotlin.coroutines.cancellation.CancellationException
```

Never:

```kotlin
import kotlinx.coroutines.CancellationException  // JVM-only typealias
```

The `kotlinx.coroutines` re-export is a JVM typealias and links incorrectly on
iOS. Use the `kotlin.coroutines.cancellation` symbol in shared code so the
import works across all source sets.

### One-active-job orchestrators: `@Volatile` + `Mutex`

When a caller outside the coroutine needs to cancel ongoing work
(`viewModel.cancelSync()`), follow the `SyncRepositoryImpl` pattern:

- Hold the active `Job` in `@Volatile var currentSyncJob: Job? = null`.
- Wrap work in `coroutineScope { currentSyncJob = coroutineContext[Job]; … }`.
- Gate stateful entry / exit with a `Mutex` so two callers can't both start.
- `cancelSync()` reads the `@Volatile` field without taking the mutex — the
  `Job.cancel()` itself is thread-safe.

Reference: `feature/sync/.../SyncRepositoryImpl.kt` `sync()` and `cancelSync()`.

### `runBlocking` is banned in production code

There are zero non-test uses today. Tests use `runTest` for virtual time and
proper teardown. The only theoretical Android exception (a `ContentProvider`
synchronous override) does not exist in this codebase.

### `viewModelScope.launch` from non-suspend UI callbacks is OK

The carve-out: a UI state holder (ViewModel, Decompose Component) may launch
into its lifecycle-bound scope from a non-suspend UI event handler:

```kotlin
fun onRefreshClicked() {
    viewModelScope.launch { repository.refresh() }
}
```

This is correct because the class is a state holder (not a repository), the
scope is lifecycle-bound (`viewModelScope`, not an app-global), and the caller
is a genuine UI event. The repository underneath still exposes a `suspend` API.

## The 7 anti-patterns at a glance

| Anti-pattern | Project status | Rule |
|---|---|---|
| Stored `CoroutineScope` property on repo / manager | not present | Use `suspend`. |
| `init { launch { … } }` | not present | Expose `suspend fun init()`. |
| Fire-and-forget from non-suspend public method | not present | Make the API `suspend`. |
| `MainScope()` / `GlobalScope` / `CoroutineScope(...)` outside `BaseViewModel` | not present | Don't introduce one. |
| Swallowed `CancellationException` | not present | Use `runCatchingDomain` or rethrow explicitly. |
| `runBlocking` in production code | not present | Use `suspend` or `runTest`. |
| DI-bound launcher (singleton launches from constructor) | not present | Expose `suspend fun run()`; explicit startup. |

## Related skills

- [[building-kmp-features]] — DI rules including the `*FeatureBindings.kt` exception.
- [[flow-state-events]] — how state and one-shot events sit on top of these primitives.
- [[sync-orchestration]] — the orchestrator pattern in `feature/sync` that uses every rule here.
- [[expect-actual]] — the same import-target rule applies to other source-set-sensitive types.
