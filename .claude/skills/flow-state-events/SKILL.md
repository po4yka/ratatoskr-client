---
name: flow-state-events
description: Use when designing or modifying state exposure on ViewModels, Decompose components, repositories, or use cases — or when picking between `StateFlow`, `SharedFlow`, `Channel`, and Decompose `Value<T>`. Trigger on `MutableStateFlow`, `asStateFlow`, `update {`, `stateIn`, `SharedFlow`, `Channel`, `Value<`, state-class design, sentinel placeholders, or delegate-collaborator ViewModels. Ratatoskr is StateFlow-only by deliberate choice — this skill explains why and how.
user-invocable: false
---

# Flow, State, and Events

Ratatoskr exposes async state through `StateFlow` exclusively. Zero
`SharedFlow`, zero `Channel`-backed flows in feature code today. The codebase
has 255 `update { it.copy(...) }` mutation sites and 0 direct `.value =`
assignments. This skill documents the shape so new code stays inside the small,
well-tested surface.

## State exposure pattern

`BaseViewModel` in `core/common` provides only `viewModelScope` and the
Decompose lifecycle hook. Subclasses own state:

```kotlin
class AuthViewModel(
    private val authRepository: AuthRepository,
) : BaseViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onLoginClicked(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            runCatchingDomain { authRepository.login(email, password) }
                .onSuccess { user -> _state.update { it.copy(isLoading = false, user = user) } }
                .onFailure { e -> _state.update { it.copy(isLoading = false, error = e.toUserMessage()) } }
        }
    }
}
```

Rules:

- `_state` private; `state: StateFlow<T>` public via `asStateFlow()`.
- One `MutableStateFlow` per ViewModel — not one per UI concern.
- Mutate via `_state.update { it.copy(...) }`. Never `_state.value = _state.value.copy(...)`.

## State shape: nullable + boolean flags, not sentinels

State data classes use nullable fields and primitive flags:

```kotlin
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isAuthenticated: Boolean = false,
    val savedDeveloperCredentials: DeveloperCredentials? = null,
)
```

There are no `sealed class ScreenState { object Loading; object Empty; data
class Loaded(...); … }` patterns. The flat data class:

- Keeps screens trivially diffable (Compose stability behaves well).
- Lets one render pass handle multiple concurrent UI concerns (loading + error
  + partial result).
- Avoids the "transition between sealed cases" footgun where two concurrent
  updates discard each other's progress.

When a real value isn't ready yet, model it with a nullable type (`user:
User?`), not a sentinel object (`User.None`).

## `update { … }` is atomic; `.value =` is not

`MutableStateFlow.update { }` applies the transform atomically against the
latest state. If two coroutines mutate simultaneously, neither loses an update.
`_state.value = _state.value.copy(...)` does a read-then-write and races.

The repo enforces `update {}` everywhere. The block may be re-invoked if a
concurrent update slipped in, so keep expensive allocations outside the lambda.

## Phased emission > sentinel initial state

When the real initial value depends on an async load, don't publish a placeholder
StateFlow. Either:

1. Start with a sensible default (`isLoading = true`), then `update {}` when
   data arrives — most cases.
2. Defer publishing entirely if `null` would lead consumers to render wrong data.

Reference: `feature/summary/.../SummaryListViewModel.kt` initialises with the
empty list + `isLoading = true`, then drives concurrent paginated and
search-debounced loads.

## Delegate collaborators via `StateAccessor<T>`

Big ViewModels split responsibilities into delegates. The delegates **don't
hold state**; they mutate the parent ViewModel's state through a
`StateAccessor<T>` interface defined in `core/common`:

```kotlin
interface StateAccessor<T> {
    val value: T
    fun update(function: (T) -> T)
}

class MutableStateFlowAccessor<T>(
    private val flow: MutableStateFlow<T>,
) : StateAccessor<T> {
    override val value: T get() = flow.value
    override fun update(function: (T) -> T) = flow.update(function)
}
```

Source: `core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/presentation/viewmodel/StateAccessor.kt`.

`SettingsViewModel` is the canonical use site — it injects
`TelegramLinkingDelegate`, `SyncSettingsDelegate`, `AccountSettingsDelegate`
and hands each a sub-state accessor.

Why this works:

- Only one `MutableStateFlow` exists (on the ViewModel).
- Delegates can be tested with a fake `StateAccessor` — no Flow machinery in
  the test setup.
- The state class stays a single source of truth, with sub-states owned per
  concern but mutated atomically.

## Decompose component state surface

Decompose components expose two kinds of "state" through two different types:

- **Navigation children**: `Value<ChildStack<…>>` (Decompose primitive). Drives
  the shell host's `Children(…)` render call.
- **Screen state**: `viewModel.state: StateFlow<T>`. Read by the routed screen
  via `collectAsState()`.

Don't wrap one in the other. The shell renders through descriptor types and the
screen reads the ViewModel directly. See [[decompose-navigation]] for component
wiring.

## What you don't do in this codebase

| Pattern | Why we don't | What to do instead |
|---|---|---|
| `MutableSharedFlow<Event>(replay = 0)` for one-shot events | None today; not needed | Add a nullable field to state and clear it on consumption, OR drive navigation through the Decompose component callback. If you genuinely need a SharedFlow, add it deliberately and document the rationale in the PR. |
| `Channel<Event>().receiveAsFlow()` | None today | Same as above. |
| `flow { … }.stateIn(scope, SharingStarted.WhileSubscribed(5_000), initial)` | None today | Hold a `MutableStateFlow` on the ViewModel. `WhileSubscribed` returns stale `.value` reads after disconnect — avoid unless you understand the trade-off. |
| `someFlow.map { … }.stateIn(…)` | None today | Map inside the ViewModel and `_state.update { }` when the source emits. |
| `sealed class Loading; object Empty; data class Loaded(...)` in StateFlow | None today | Nullable + boolean flags on a flat data class. |

If you find yourself needing one of these, that's a real design decision —
write it up in the PR; don't sneak it in.

## Related skills

- [[kotlin-coroutines]] — the structured concurrency primitives this layer sits on.
- [[decompose-navigation]] — `Value<T>` vs `StateFlow<T>` and component retention.
- [[building-kmp-features]] — where ViewModels live and how they're scanned by DI.
