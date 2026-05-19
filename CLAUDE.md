# CLAUDE.md

Guidance for Claude Code when working in this repository.

## Project Snapshot

- Kotlin Multiplatform app with shared infrastructure in `core/`, feature modules under `feature/*`, shared KMP libraries under `shared/`, an Android application host in `androidApp/`, a desktop development app in `desktopApp/`, and a SwiftUI host app in `iosApp/`.
- Active Kotlin modules are `core/api-generated`, `core/common`, `core/data`, `core/navigation`, `core/ui`, `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, `feature/sync`, `shared/sharedLogic`, and `shared/sharedUI`.
- Desktop exists as a development target for Compose work and hot reload, not as a production app.

## Build And Run

```bash
# Full build
./gradlew build

# Android app
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# Desktop Compose development
./gradlew :desktopApp:hotRunDesktop

# Module tests
./gradlew :core:common:allTests :core:data:allTests
./gradlew :feature:summary:allTests :feature:settings:allTests

# shared module tests (KMP — covers Android, iOS sim, JVM/desktop)
./gradlew :shared:sharedLogic:allTests :shared:sharedUI:allTests

# Code quality
./gradlew ktlintCheck detekt
./gradlew ktlintFormat

# Coverage
./gradlew :core:common:koverHtmlReportDebug :core:data:koverHtmlReportDebug

# iOS workspace
open iosApp/iosApp.xcworkspace
```

Use the workspace, not just `iosApp.xcodeproj`, when CocoaPods integration matters.

## Configuration

`local.properties` is gitignored. Common overrides:

```properties
api.base.url=https://api.ratatoskr.po4yka.com
# Optional Android release override. Must use HTTPS when set.
api.release.base.url=https://api.ratatoskr.po4yka.com
api.logging.enabled=false
telegram.bot.username=ratatoskr_client_bot
telegram.bot.id=
client.id=ratatoskr-android-v1.0
api.timeout.seconds=30
```

Shared runtime config is centralized in `core/common/src/commonMain/kotlin/com/po4yka/ratatoskr/util/config/AppConfig.kt`.

**Release hardening for API logging:** `AppConfig.Api.loggingEnabled` is a clamped
property. Each platform's bootstrap must set `AppConfig.Api.isReleaseBuild`
(Android: `!BuildConfig.DEBUG`; iOS: `!kotlin.native.Platform.isDebugBinary`;
desktop: `-Dratatoskr.release=true` system property). When `isReleaseBuild`
is `true`, the getter always returns `false` — even if a misconfigured
`local.properties`, `Config.xcconfig`, or future Swift→Kotlin bridge forced
`api.logging.enabled=true`. Never bypass this clamp.

## Design System

Frost is the project-owned design system and is now live across all screens,
components, and platform widgets. Editorial monospace minimalism: two-color rule
(ink `#1C242C` / `#E8ECF0` dark + page `#F0F2F5` / `#12161C` dark), single
critical accent (spark `#DC3545`, never flips), 0 corner radius, no shadows,
no Material elevation. Canonical spec lives in `DESIGN.md` (DESIGN.md format,
https://github.com/google-labs-code/design.md) at the repo root — read it
before adding tokens, components, colors, shapes, or motion.

Frost primitives live in:
- `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost/`
- `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/foundation/`

## Architecture

- Modules are split by responsibility:
  - `core/common` for cross-feature domain models, config, errors, base ViewModel primitives, and platform abstractions
  - `core/data` for shared networking/bootstrap, persistence, SQLDelight, secure storage, generic API wrappers, and platform data bindings
  - `core/navigation` for route contracts and navigation-facing interfaces
  - `core/ui` for shared non-feature UI primitives
  - `feature/*` for feature-owned repositories, use cases, route factories, transport APIs/DTOs/mappers, state, ViewModels, and Decompose components
  - `shared/sharedLogic/` for pure-logic KMP library: DI bootstrap, Koin initializers, app composition root, navigation components (no Compose deps)
  - `shared/sharedUI/` for Compose Multiplatform KMP library: `App.kt`, screen composables, image loader DI, CocoaPods framework export (framework basename `ComposeApp`)
  - `desktopApp/` for the pure-JVM Compose Desktop application (hot-reload dev target)
  - `androidApp/` for Android activity/app/widget/worker entrypoints
- Navigation is Decompose-based. Feature components own routed-screen dependencies and retained ViewModel creation.
- Compose UI lives in `shared/sharedUI/src/commonMain/kotlin/.../ui`, with screens consuming a `*Component` or app-level provider instead of resolving Koin directly.
- Domain contracts and UI code must not import `data.remote` APIs or DTOs.
- One feature may depend on another feature's public contracts only. Do not import another feature's `data` or `presentation` packages.
- Current public feature edges include `summary -> auth/collections/sync`, `collections -> sync`, `digest -> summary`, and `settings -> auth/summary/sync`.

See `shared/sharedUI/AGENTS.md` for UI rules. See `docs/ARCHITECTURE.md` for the current dependency rules.

## Dependency Injection

Default rule in `core/` and `feature/*` code:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Important exceptions already exist and are valid:

- `core/data/src/iosMain/.../di/IosModule.kt` uses Koin DSL because the generated `.module` extensions are not visible from `iosMain`.
- `core/data/src/desktopMain/.../di/DesktopModule.kt` uses DSL for desktop-only wiring that doesn't go through KSP-scanned annotations.
- `shared/sharedUI/src/commonMain/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- `feature/auth/.../di/AuthFeatureBindings.kt` plus the matching `*FeatureBindings.kt` in `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, `feature/sync` use DSL because **ViewModels are wired manually to avoid duplicate `BaseViewModel` KSP symbols in native frameworks** — Koin's annotation scanner emits a synthetic ViewModel factory per module, and when several modules export `BaseViewModel` subclasses through the same iOS framework the duplicates fail link. See the comment at the top of `AuthFeatureBindings.kt` for the canonical explanation.
- tests may use DSL to provide fakes and overrides.

Do not "fix" those exceptions by force-converting them to annotations without understanding the source-set limitations.

`shared/sharedLogic/.../di/KoinInitializer.kt` is the active bootstrap entry point. Platform actuals expose `appModules()` plus `platformModules()`.

## Coroutines

- Always rethrow `CancellationException` as the **first** `catch` clause when using broad
  `catch (Exception)` / `catch (Throwable)` blocks. Swallowing cancellation breaks
  structured concurrency — child coroutines stop honoring parent cancellation and tests
  can hang. Use `kotlin.coroutines.cancellation.CancellationException` in shared code.
- See the `kotlin-coroutines` skill for the full anti-pattern catalogue, the
  `runCatchingDomain` wrapper, and the `@Volatile + Mutex` one-active-job pattern.

## Flow and State

- Feature ViewModels expose state via a single `MutableStateFlow<T>` backed
  `StateFlow<T>` (`_state.asStateFlow()`); mutate with `_state.update { it.copy(...) }`.
  Zero `SharedFlow`/`Channel` usage today by deliberate choice.
- For ViewModels that need to split responsibilities, use the
  `StateAccessor<T>` delegate-collaborator pattern in `core/common` —
  delegates mutate parent state without holding their own `MutableStateFlow`.
- See the `flow-state-events` skill for the full pattern catalogue, including
  what NOT to do (`stateIn` inside functions, sentinel placeholders, sealed
  Loading/Empty states).

## Platform Notes

### Android

- `androidApp/` is the Android application host module.
- Secure storage uses Tink AEAD + DataStore.
- Networking uses OkHttp.
- Background work uses WorkManager.
- Widgets use Glance with hardcoded Frost INK/PAGE color constants (exempt from AppTheme, but must follow Frost visual rules).

### iOS

- `iosApp/` is the SwiftUI host around the `ComposeApp` framework exported from `shared/sharedUI/`.
- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share and widget source lives under `iosApp/ShareExtension` and `iosApp/RecentSummariesWidget`; keep their app-group and deep-link contracts aligned with the main app.

### Swift Interop

The SKIE plugin is configured in Gradle but currently disabled in `shared/sharedUI/build.gradle.kts` because the active Kotlin version is ahead of supported SKIE versions. Do not assume new SKIE-generated APIs are available until that flag is re-enabled.

### Expect / Actual

See the `expect-actual` skill for the boundary-choice rules (when to use
`expect`/`actual`, when to use a common interface + Koin DSL binding, when to
use a leaf Compose `expect fun`) and the catalogue of current expect sites.

## OpenAPI Generation

The backend's `docs/openapi/mobile_api.yaml` is the source of truth for the
mobile API contract. `core/api-generated` holds the Kotlin client generated
from it via [openapi-kmp-gen](https://github.com/kroegerama/openapi-kmp-gen).

- The upstream pin lives at `tools/openapi.lock` (repo + commit SHA + path).
- The fetched YAML is checked in at
  `core/api-generated/src/commonMain/openapi/mobile_api.yaml` so the build
  works offline.
- Generated Kotlin sources are checked in at
  `core/api-generated/src/commonMain/generated/` (4 files, ~10.6k lines).

### Bump the pinned spec

```bash
# 1. Edit tools/openapi.lock with the new backend SHA.
# 2. Regenerate the YAML, Kotlin sources, and apply post-gen patches:
./gradlew :core:api-generated:regenerateOpenApi
# 3. git diff to review the contract changes, then commit.
```

### Drift detection

CI runs `./gradlew :core:api-generated:checkOpenApiDrift` (also wired into
`./gradlew check`). It fetches the pinned upstream YAML, runs it through the
normalizer, and byte-compares against the checked-in copy — the build fails
if the two disagree.

### Why the normalizer + patches exist

openapi-kmp-gen 1.3.0 has three classes of bugs against our spec:

1. OAS 3.1 `anyOf` unions with `{type: "null"}` produce empty data classes.
   `tools/openapi/normalize_openapi.py` rewrites them to OAS 3.0
   `nullable: true` form before generation.
2. Polymorphic `anyOf` without null (e.g. `int | string`) also produce empty
   classes. The normalizer flattens the two known sites to `string`.
3. Generated function parameters named `url` shadow the Ktor request
   builder's `url` property, and a non-deterministic timestamp appears in
   `Api.kt` every regen. `tools/openapi/patch_generated.py` renames the
   parameter to `reqUrl` and strips the timestamp.

If new spec patterns trip the generator, prefer extending the normalizer or
post-gen patch over hand-editing `src/commonMain/generated/`.

### Do not hand-edit generated files

Files under `core/api-generated/src/commonMain/generated/` and the YAML at
`src/commonMain/openapi/mobile_api.yaml` are derived artifacts. The drift
check will reject any local edit.

### Bootstrapping the generated client

The generated `Api` is a singleton (`object Api : ApiHolder()`) that owns
its own `HttpClient`. Call `bootstrapGeneratedApi(...)` from
`core/api-generated/.../bootstrap/GeneratedApiBootstrap.kt` once during app
startup, before any generated API method is invoked:

```kotlin
bootstrapGeneratedApi(
    baseUrl = AppConfig.Api.baseUrl,
    engine = platformHttpEngine,            // OkHttp on Android, Darwin on iOS
    bearerTokenProvider = { secureStorage.getAccessToken() },
    withLogging = AppConfig.Api.loggingEnabled,
)
```

The companion library's `AuthPlugin` does not auto-refresh on 401 — that
behavior lives in `core/data/.../ApiClient.kt`. The feature modules no
longer import `ApiClient` directly (`rg -l "data\.remote\.ApiClient" feature/`
returns nothing); the legacy client is now only referenced by
`core/data/.../di/NetworkModule.kt` for the dwindling set of call sites
inside `core/data` itself. New code should use the bootstrapped
generated `Api.client` and handle 401 explicitly via the
`Either<CallException, HttpCallResponse<T>>` return type.

When the last call site is removed, the existing `ApiClient` and `ApiResponseDto<T>` envelope can be deleted in a single follow-up commit.

## Auth And Sync

- HTTP auth is handled by Ktor `Auth` bearer refresh in `core/data/.../data/remote/ApiClient.kt`.
- Token refresh calls `POST v1/auth/refresh` and updates `SecureStorage`.
- Sync is session-based and implemented in `feature/sync/.../data/repository/SyncRepositoryImpl.kt`.
- `feature/sync` owns orchestration only; feature-owned sync item appliers and pending-operation handlers are injected into it from the owning modules.

## Common Change Patterns

### Add A Screen

1. Add or extend state in the owning `feature/.../presentation/state/` package, or `core/common` only if the state is intentionally shared across features.
2. Add a ViewModel in the owning `feature/.../presentation/viewmodel/` module extending `BaseViewModel`.
3. Add a Decompose component in the owning `feature/.../presentation/navigation/` module.
4. Register the route entry or binding in the owning feature module, then connect it through the `shared/sharedLogic` navigation shell if needed.
5. Add the Compose screen in the owning feature module under `feature/.../feature/<name>/ui/screens/`.
6. Add shared strings and assets under `core/ui/src/commonMain/composeResources/`.

### Add A Repository Flow

1. Define the contract in `domain/repository/`.
2. Add DTOs under the owning feature module `data/remote/dto/` if the backend shape changes.
3. Add feature-owned mappers in the owning feature `data/mappers/`.
4. Add the implementation in the owning feature `data/repository/` with `@Single(binds = [...])`.
5. Keep API details in the owning feature `data/remote/`; keep DTOs and API types out of domain and UI layers.

### Add Shared UI Behavior

- Prefer Frost atoms in `core/ui/.../components/frost/`: `AtomMark`, `BracketButton`, `BracketField`, `BracketIconButton`, `BracketSelector`, `BracketSlider`, `BracketSwitch`, `BrutalistCard`, `FrostCheckbox`, `FrostRadio`, `FrostSpinner`, `IngestLine`, `InlineLink`, `MultiSelectChip`, `PullQuote`, `RowDigest`, `SectionHeading`, `StatusBadge`, `Toast`.
- Frost foundation primitives in `core/ui/.../components/foundation/`: `FrostDialog`, `FrostDivider`, `FrostIcon`, `FrostIndication`, `FrostScaffold`, `FrostSurface`, `FrostText`.
- Compose these before inventing new patterns.
- Use Compose Resources instead of hardcoded UI text.
- Keep accessibility semantics in mind; the repo already uses headings and live regions in screens such as `SummaryListScreen`.

---

## Task Board

This repository uses Obsidian Tasks-compatible Markdown task lines as the canonical task system.
Use the `repo-task-board` skill for all task-related operations.

Canonical files:

- `docs/tasks/README.md` — structure overview, lifecycle, frontmatter schema
- `docs/tasks/issues/<slug>.md` — **source of truth** — one note per task (YAML frontmatter + canonical `- [ ]` line + spec body)
- `docs/tasks/templates/new-task.md` — Templater template for creating new task notes
- `docs/tasks/views/{all-tasks,by-area,by-priority}.base` — Obsidian Bases structured views (table + cards)
- `docs/tasks/active.md` — Obsidian Tasks query view (`#status/doing`, `#status/review`)
- `docs/tasks/backlog.md` — Obsidian Tasks query view (`#status/backlog`)
- `docs/tasks/blocked.md` — Obsidian Tasks query view (`#status/blocked`)
- `docs/tasks/dashboard.md` — full Obsidian Tasks query hub + Bases-view links
- `docs/tasks/board.md` — Kanban swim-lane view (`[[slug]]` wikilinks; source of truth remains `issues/`)
- `docs/tasks/.markdownlint.yaml` — relaxes MD013/MD033 inside the tasks folder

Canonical task syntax (lives inside `docs/tasks/issues/<slug>.md`):

```md
- [ ] #task <imperative title> #repo/ratatoskr-client #area/<area> #status/<status> <priority> [paperclip:POY-NNN]
```

The `[paperclip:POY-NNN]` suffix is optional; include it when a task corresponds to a Paperclip (Jira) issue.

Per-task note YAML frontmatter:

```yaml
---
title: Imperative task title
status: doing          # backlog | todo | doing | review | blocked | done | dropped
area: sync             # auth | api | kmp | sync | ci | frontend | observability | testing | content | scraper | llm | db | docs | ops | search | design
priority: high         # critical | high | medium | low
owner: Role name
paperclip: POY-NNN     # optional
blocks: []
blocked_by: []
created: YYYY-MM-DD
updated: YYYY-MM-DD
---
```

Allowed statuses: `#status/backlog` · `#status/todo` · `#status/doing` · `#status/review` · `#status/blocked` · `#status/done` · `#status/dropped`

Lifecycle: create via Templater template (`templates/new-task.md`) → file lands in `issues/<slug>.md` with kebab-case filename → transitions update `status:` frontmatter AND the `#status/*` tag in the canonical `- [ ]` line together → delete the issue file on close (git history is the audit trail: `git log -- docs/tasks/issues/<slug>.md`). Do NOT add task lines to `active.md`, `backlog.md`, `blocked.md`, or `dashboard.md` — those are query-only views that would double-count.

Open the **repo root** as your Obsidian vault (not `docs/tasks/`) so the Tasks plugin and Bases views resolve correctly.

**AI assistants must delete the issue file after implementing a task.**

Invoke the `repo-task-board` skill when the user mentions: roadmap, TODO, backlog, Kanban, task board, sprint, blocked work, or agent-ready work.
