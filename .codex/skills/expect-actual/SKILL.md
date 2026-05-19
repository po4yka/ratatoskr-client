---
name: expect-actual
description: Use when adding, modifying, or wiring an `expect` / `actual` declaration in core/* or feature/* — or when deciding whether a platform difference should be `expect`/`actual`, a common interface + Koin DSL binding, or a leaf Compose `expect fun`. Trigger on the `expect` or `actual` keywords, source-set changes under `iosMain` / `androidMain` / `desktopMain`, platform-bootstrap questions, or any "how do I share this across platforms" question.
user-invocable: false
---

# expect / actual Boundaries

Ratatoskr targets Android, iOS, and Desktop (Compose dev target only). Platform
differences are absorbed through three mechanisms — pick the right one before
writing `expect`. The current site catalogue lives at the bottom of this skill;
read it before adding a new boundary.

## Three boundary mechanisms — pick before writing `expect`

### 1. `expect` / `actual` — for pure platform-API translation

Use when the platform difference is a thin call into a system API with no DI,
no lifecycle ownership, and no product / domain state.

```kotlin
// commonMain
expect fun getPlatform(): Platform

// androidMain
actual fun getPlatform(): Platform = Platform("Android ${android.os.Build.VERSION.RELEASE}")

// iosMain
actual fun getPlatform(): Platform = Platform("iOS ${UIDevice.currentDevice.systemVersion}")
```

Examples in repo: `getPlatform()`, `FileSaver`, `AudioPlayer`,
`DatabaseDriverFactory`, Compose `rememberHaptic()` / `rememberReduceMotion()`,
`WebView()` composable.

### 2. Common interface + Koin DSL binding — for DI-shaped platform work

Use when the platform impl needs DI, runtime selection, multiple
implementations for testing, or lifecycle ownership.

```kotlin
// commonMain (no expect at all)
interface SecureStorage {
    suspend fun getAccessToken(): String?
    suspend fun setAccessToken(token: String?)
    // …
}

// androidMain — Tink + DataStore
internal class AndroidSecureStorage(private val context: Context) : SecureStorage { … }

// AndroidModule.kt (Koin DSL — KSP-scanned)
single<SecureStorage> { AndroidSecureStorage(get()) }

// iosMain — KeychainSettings
internal class IosSecureStorage(...) : SecureStorage { … }

// IosModule.kt (Koin DSL — KSP annotations are unreachable from iosMain)
single<SecureStorage> { IosSecureStorage() }
```

Examples in repo:

- `SecureStorage` — the model clean interface
  (`core/data/src/commonMain/.../data/local/SecureStorage.kt`).
- `HttpClientEngine` — no `expect`. Bound per platform in `AndroidModule.kt`
  (OkHttp), `IosModule.kt` (Darwin), `DesktopModule.kt` (OkHttp). The
  `HttpClientEngine` type already exists in Ktor — no need to redeclare it as
  `expect`.

Why interfaces beat `expect class` for DI work: testing. A fake `SecureStorage`
is just an interface impl. A fake `expect class` requires platform-specific
test source sets.

### 3. Compose `expect fun` at leaf level — for platform-only UI

Use when most of a screen is shared Compose, but one leaf needs platform-only
APIs (system accessibility, web rendering, native pickers).

```kotlin
// commonMain
@Composable
expect fun rememberReduceMotion(): Boolean
```

Always pass `Modifier` through expected composables so callers can size and
position them like any other Compose node.

## Common signatures stay semantic — actual constructor args may be platform

A common signature like `expect class FileSaver` is fine even though the
Android actual takes a `Context` constructor arg:

```kotlin
// commonMain
expect class FileSaver { suspend fun save(name: String, bytes: ByteArray): SaveResult }

// androidMain
actual class FileSaver(private val context: Context) {
    actual suspend fun save(...): SaveResult { … }
}

// iosMain
actual class FileSaver { actual suspend fun save(...): SaveResult { … } }
```

The constructor arg is DI scaffolding — Koin provides the `Context` to the
Android impl, and the iOS impl needs none. The common signature exposes only
domain types (`SaveResult`, `ByteArray`). That's the rule.

What's **not** allowed: platform types in the common signature.

```kotlin
// BAD — leaks Context into commonMain
expect fun currentRegionFromAndroidLocale(context: Context): Region

// GOOD — semantic
expect fun currentRegion(): Region
```

## Platform bootstrap: `AppConfig.Api.isReleaseBuild`

Some config is platform-mode-gated rather than data-shaped — release builds
must clamp API logging regardless of `local.properties`. Each platform's
bootstrap sets the flag once:

| Platform | Bootstrap file | Value |
|---|---|---|
| Android | `androidApp/src/main/kotlin/com/po4yka/ratatoskr/RatatoskrApp.kt` | `!BuildConfig.DEBUG` |
| iOS | `shared/sharedLogic/src/iosMain/kotlin/com/po4yka/ratatoskr/di/KoinInitializer.kt` | `!Platform.isDebugBinary` |
| Desktop | `shared/sharedLogic/src/desktopMain/kotlin/com/po4yka/ratatoskr/di/KoinInitializer.kt` | `System.getProperty("ratatoskr.release") == "true"` |

`AppConfig.Api.loggingEnabled` getter clamps to `false` when `isReleaseBuild`
is `true`, even if `local.properties` says otherwise. New platform-mode config
follows the same 3-platform shape — never trust the build classpath.

## Koin DSL exception for `iosMain` / `desktopMain`

KSP scans annotated `@Module @ComponentScan` types and generates `.module`
extensions into platform-specific output dirs (`iosArm64Main`, `desktopMain`,
etc.). Those generated extensions are **not visible from `iosMain` or
`desktopMain` source sets** — so platform-specific modules in those source sets
use Koin DSL instead of annotations.

Files:

- `core/data/src/iosMain/.../di/IosModule.kt`
- `core/data/src/desktopMain/.../di/DesktopModule.kt`

This is a permanent constraint of the source-set layout, not a TODO. See
[[building-kmp-features]] for the full DI exception list (including the
`*FeatureBindings.kt` files).

## Red flags during review

- `import android.content.Context` (or any `android.*`, `androidx.*`,
  `apple.*`, `platform.*`) inside a file under `src/commonMain/`.
- An `actual` that knows about product / domain state (a feature flag, a user
  ID, a route).
- Platform names baked into common function signatures
  (`refreshKeychainAndAndroidKeystore()`).
- A common `expect class` that turns around and requires a platform-specific
  lifecycle to construct (`Activity`, `UIViewController` in commonMain) — this
  is a sign the boundary should be a common interface, not `expect`.

## The current expect/actual catalogue (read before adding a new one)

| Expect | commonMain | Actuals |
|---|---|---|
| `fun getPlatform(): Platform` | `core/common/.../util/Platform.kt` | android / ios / desktop |
| `class FileSaver` | `core/common/.../FileSaver.kt` | android / ios / desktop |
| `class AudioPlayer()` | `core/common/.../AudioPlayer.kt` | android / ios / desktop |
| `class DatabaseDriverFactory` | `core/data/.../DatabaseDriverFactory.kt` | android / ios / desktop |
| `fun rememberReduceMotion(): Boolean` | `core/ui/.../ReduceMotion.kt` | android / ios / desktop |
| `fun rememberHaptic(): (HapticKind) -> Unit` | `core/ui/.../Haptic.kt` | android / ios / desktop |
| `fun WebView(...)` | `feature/auth/.../WebView.kt` | android / ios / desktop |
| `class PlatformConfiguration()` | `shared/sharedLogic/.../di/KoinInitializer.kt` | android / ios / desktop |
| `fun platformModules(config): List<Module>` | `shared/sharedLogic/.../di/KoinInitializer.kt` | android / ios / desktop |
| `fun KoinApplication.platformExtras(config)` | `shared/sharedLogic/.../di/KoinInitializer.kt` | android / ios / desktop |

If you add a new one, append it here in the same PR.

## Related skills

- [[building-kmp-features]] — DI rules, module layout, the `*FeatureBindings.kt`
  exception that lives next to this one.
- [[ios-bridge]] — the iOS side of the bridge, app-group / keychain-group / URL
  scheme sync.
- [[kotlin-coroutines]] — the `CancellationException` import rule that's
  source-set-sensitive in the same way `expect`/`actual` is.
