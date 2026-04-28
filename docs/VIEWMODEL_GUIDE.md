# ViewModel Development Guide

Quick reference for creating ViewModels following the MVI pattern in this project.

## Structure Template

```kotlin
// 1. State data class
data class MyState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: MyData? = null,
)

// 2. ViewModel with @Factory annotation
@Factory
class MyViewModel(
    private val myUseCase: MyUseCase,
) : BaseViewModel() {

    // 3. Private mutable state
    private val _state = MutableStateFlow(MyState())

    // 4. Public immutable state
    val state: StateFlow<MyState> = _state.asStateFlow()

    // 5. Initialize data if needed
    init {
        loadData()
    }

    // 6. Public actions (events/intents)
    fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            runCatching { myUseCase() }
                .onSuccess { data ->
                    _state.value = _state.value.copy(isLoading = false, data = data)
                }
                .onFailure { throwable ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = throwable.toAppError().userMessage()
                    )
                }
        }
    }
}
```

## Key Components

| Component | Purpose |
|-----------|---------|
| `BaseViewModel` | Provides `viewModelScope` with SupervisorJob |
| `@Factory` | Koin annotation - new instance per injection |
| `MutableStateFlow` | Private, mutable state holder |
| `StateFlow` | Public, read-only state exposure |
| `viewModelScope.launch` | Coroutine execution |
| `runCatching` | Error handling pattern |

## State Update Pattern

Always use `copy()` to update state immutably:

```kotlin
// Correct
_state.value = _state.value.copy(isLoading = true)

// Wrong - mutating directly
_state.value.isLoading = true  // Compile error anyway
```

## Observing External Flows

Use `launchIn` with `onEach` for reactive updates:

```kotlin
init {
    observeExternalData()
}

private fun observeExternalData() {
    someUseCase.dataFlow
        .onEach { data ->
            _state.value = _state.value.copy(data = data)
        }
        .launchIn(viewModelScope)
}
```

## Error Handling

Use the error utility for user-friendly messages:

```kotlin
import com.po4yka.ratatoskr.util.error.toAppError
import com.po4yka.ratatoskr.util.error.userMessage

.onFailure { throwable ->
    _state.value = _state.value.copy(
        error = throwable.toAppError().userMessage()
    )
}
```

## Cancellation Support

For cancellable operations, store and manage the Job:

```kotlin
private var operationJob: Job? = null

fun startOperation() {
    operationJob?.cancel()
    operationJob = viewModelScope.launch {
        // ... operation
    }
}

fun cancelOperation() {
    operationJob?.cancel()
}
```

## File Locations

| File | Location |
|------|----------|
| ViewModel | `feature/<name>/.../presentation/viewmodel/` |
| State class | Same file as ViewModel (or separate if complex) |
| BaseViewModel | `core/common/.../presentation/viewmodel/BaseViewModel.kt` |

## Example: SettingsViewModel

See `feature/settings/.../presentation/viewmodel/SettingsViewModel.kt` for a complete example with:
- Multiple use cases
- Flow observation (`observeSyncProgress`)
- Cancellation support (`cancelSync`)
- Loading state management

---

**Related**: [USE_CASE_GUIDE.md](USE_CASE_GUIDE.md) | [REPOSITORY_PATTERNS.md](REPOSITORY_PATTERNS.md)
