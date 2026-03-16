# shared/ Module

## Dependency Injection (Koin Annotations)

Uses **Koin 4.x with Koin Annotations** + KSP for compile-time safety. Constructor injection is automatic.

### Annotation Rules

1. **Use annotations for all new classes** — never use `module { }` DSL for new code:
   ```kotlin
   @Single class MyRepository(private val api: MyApi) : MyRepositoryInterface
   @Factory class MyUseCase(private val repository: MyRepository)
   ```

2. **Annotation by layer**:
   - `data/remote/` API implementations: `@Single`
   - `data/repository/` repositories: `@Single`
   - `domain/usecase/` use cases: `@Factory`
   - `presentation/viewmodel/` ViewModels: `@Factory` (or `@Single` for shared state like AuthViewModel)

3. **Module classes** use `@Module` + `@ComponentScan`:
   ```kotlin
   @Module
   @ComponentScan("com.po4yka.bitesizereader.data.repository")
   class RepositoryModule
   ```

4. **Import generated modules** via KSP-generated `.module` extension:
   ```kotlin
   import org.koin.ksp.generated.module
   fun commonModules() = listOf(NetworkModule().module, DatabaseModule().module, ...)
   ```

5. **Bind to interfaces** with the `binds` parameter:
   ```kotlin
   @Single(binds = [MyInterface::class])
   class MyImplementation : MyInterface
   ```

### Rules to Follow

- Use annotations (`@Single`, `@Factory`), not `module { }` DSL — DSL is legacy in this project
- Use constructor injection; `get()` is only needed in rare DSL modules
- KSP catches circular dependencies at compile time — if the build fails, check your dependency graph
- For complex initialization (e.g. `DatabaseModule`), use `@Single` on a provider function inside a `@Module` class

## Sync Architecture

Session-based sync with progress tracking and cancellation support.

**Key classes**:
- `SyncProgress`: Tracks phase, batch info, progress fraction, error count
- `SyncResult`: Contains created/updated/deleted counts, pagination cursor
- `SyncRepository`: Interface with `sync()`, `fullSync()`, `deltaSync()`, `cancelSync()`

**Workflow**:
1. Create session via `createSyncSession()`
2. Execute `fullSync()` or `deltaSync()` with cursor-based pagination
3. Process batches (25-500 items, adaptive sizing)
4. Save checkpoints after each batch
5. Validate integrity on completion

See `SyncPhase` enum for all phase definitions (has KDoc on each variant).

## Authentication Flow

Bearer token auth via Ktor's `Auth` plugin.

- **Token refresh**: `POST /v1/auth/refresh` with `{ "refresh_token": "<token>" }`
- **On success**: Update stored tokens via `SecureStorage`
- **On failure**: Clear tokens, redirect to login screen

See `ApiClient.kt` for the `refreshTokens` implementation.
