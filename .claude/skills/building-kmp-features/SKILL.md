---
name: building-kmp-features
description:
  Guides creation of new screens, use cases, repositories, and UI components
  in this Kotlin Multiplatform + Compose Multiplatform project. Covers MVI
  state classes, Koin DI annotations, Decompose navigation, and Carbon
  Design System component usage.
user-invocable: false
---

# Building KMP Features

Quick reference for adding features to this Bite-Size Reader project.

## Add a New Screen

1. **State class** in `shared/.../presentation/state/MyState.kt`
2. **ViewModel** in `shared/.../presentation/viewmodel/MyViewModel.kt` with `@Factory`
3. **Navigation component** in `shared/.../presentation/navigation/MyComponent.kt`
4. **Screen composable** in `composeApp/.../ui/screens/MyScreen.kt`
5. **Register** in `RootComponent.kt` navigation graph

## Add a New Use Case

1. Create in `shared/.../domain/usecase/MyUseCase.kt`
2. Add `@Factory` annotation
3. Inject repository via constructor
4. Return `Result<T>` or `Flow<T>`

## Add a New Repository

1. Define interface in `shared/.../domain/repository/MyRepository.kt`
2. Create implementation in `shared/.../data/repository/MyRepositoryImpl.kt`
3. Add `@Single(binds = [MyRepository::class])` annotation
4. Create DTOs in `data/remote/dto/` if needed
5. Create mappers in `data/mappers/` if needed

## Koin DI Annotations

| Layer | Annotation | Example |
|-------|-----------|---------|
| API implementations (`data/remote/`) | `@Single` | `@Single class ArticleApi(client: HttpClient)` |
| Repository implementations (`data/repository/`) | `@Single` | `@Single(binds = [ArticleRepo::class])` |
| Use cases (`domain/usecase/`) | `@Factory` | `@Factory class GetArticleUseCase(repo: ArticleRepo)` |
| ViewModels (`presentation/viewmodel/`) | `@Factory` | `@Factory class ArticleViewModel(useCase: GetArticleUseCase)` |
| Shared state ViewModels | `@Single` | `@Single class AuthViewModel(...)` |

**Rules:**
- Never use `module { }` DSL -- always annotations
- Constructor injection is automatic (no `get()` needed)
- Import generated modules: `import org.koin.ksp.generated.module`

## MVI Pattern

ViewModels expose `StateFlow<State>` and accept events:

```kotlin
@Factory
class MyViewModel(private val useCase: MyUseCase) : ViewModel() {
    private val _state = MutableStateFlow(MyState())
    val state: StateFlow<MyState> = _state.asStateFlow()

    fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.Load -> loadData()
            // ...
        }
    }
}
```

## UI Components

For Carbon Design System component details, see [references/carbon-components.md](references/carbon-components.md).

**Key rules:**
- Always use Carbon components for new UI
- Use `Carbon.theme.*` for colors, `Carbon.typography.*` for text styles
- Use `Material3.Text` (not Carbon's internal Text) with Carbon styles
- Icons from `CarbonIcons` object with `Carbon.theme.iconPrimary` tint

## Module Structure

```
shared/src/commonMain/kotlin/
  data/local/          # SQLDelight, SecureStorage
  data/remote/         # Ktor API, DTOs
  data/repository/     # Repository impls
  data/mappers/        # DTO <-> Domain
  domain/model/        # Domain entities
  domain/repository/   # Repository interfaces
  domain/usecase/      # Business logic
  presentation/
    navigation/        # Decompose components
    viewmodel/         # MVI ViewModels
    state/             # State data classes
  di/                  # Koin modules

composeApp/src/commonMain/kotlin/
  ui/screens/          # Full screens
  ui/components/       # Reusable UI
  ui/theme/            # Material 3 + Carbon
  ui/icons/            # CarbonIcons
```
