# AGENTS.md

Project guidance for Codex when working in this repository.

## Project Snapshot

- Kotlin Multiplatform app with shared infrastructure in `core/`, feature modules under `feature/*`, a Compose shell in `composeApp/`, an Android application host in `androidApp/`, and a SwiftUI host app in `iosApp/`.
- Active Kotlin modules are `core/common`, `core/data`, `core/navigation`, `core/ui`, `feature/auth`, `feature/collections`, `feature/digest`, `feature/settings`, `feature/summary`, and `feature/sync`.
- Desktop exists as a development target for Compose work and hot reload, not as a production app.

## Build And Run

```bash
# Full build
./gradlew build

# Android app
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:installDebug

# Desktop Compose development
./gradlew :composeApp:hotRunDesktop

# Module tests
./gradlew :core:common:allTests :core:data:allTests
./gradlew :feature:summary:allTests :feature:settings:allTests

# composeApp tests (KMP — covers Android, iOS sim, JVM/desktop)
./gradlew :composeApp:allTests

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
  - `composeApp/` for shared Compose UI, navigation shell composition, CocoaPods export, and the desktop dev target
  - `androidApp/` for Android activity/app/widget/worker entrypoints
- Navigation is Decompose-based. Feature components own routed-screen dependencies and retained ViewModel creation.
- Compose UI lives in `composeApp/src/commonMain/kotlin/.../ui`, with screens consuming a `*Component` or app-level provider instead of resolving Koin directly.
- Domain contracts and UI code must not import `data.remote` APIs or DTOs.
- One feature may depend on another feature's public contracts only. Do not import another feature's `data` or `presentation` packages.
- Current public feature edges include `summary -> auth/collections/sync`, `collections -> sync`, `digest -> summary`, and `settings -> auth/summary/sync`.

See `composeApp/AGENTS.md` for UI rules. See `docs/ARCHITECTURE.md` for the current dependency rules. See `CLAUDE.md` for Claude-Code-specific guidance (the two root files share most content).

## Dependency Injection

Default rule in `core/` and `feature/*` code:

- `data/remote/` and `data/repository/`: `@Single`
- `domain/usecase/`: `@Factory`
- `presentation/viewmodel/` and delegates: `@Factory`
- module scanners: `@Module` + `@ComponentScan`

Important exceptions already exist and are valid:

- `core/data/src/iosMain/.../di/IosModule.kt` uses Koin DSL because the generated `.module` extensions are not visible from `iosMain`.
- `composeApp/src/commonMain/.../di/ImageLoaderModule.kt` uses DSL for UI-only wiring.
- tests may use DSL to provide fakes and overrides.

Do not "fix" those exceptions by force-converting them to annotations without understanding the source-set limitations.

`composeApp/.../di/KoinInitializer.kt` is the active bootstrap entry point. Platform actuals expose `appModules()` plus `platformModules()`.

## Platform Notes

### Android

- `androidApp/` is the Android application host module.
- Secure storage uses Tink AEAD + DataStore.
- Networking uses OkHttp.
- Background work uses WorkManager.
- Widgets use Glance with hardcoded Frost INK/PAGE color constants (exempt from AppTheme, but must follow Frost visual rules).

### iOS

- `iosApp/` is the SwiftUI host around the `ComposeApp` framework exported from `composeApp/`.
- Secure storage uses `KeychainSettings`.
- Networking uses the Darwin Ktor engine.
- App startup and background sync live in `iosApp/iosApp/iOSApp.swift`.
- Share and widget source lives under `iosApp/ShareExtension` and `iosApp/RecentSummariesWidget`; keep their app-group and deep-link contracts aligned with the main app.

### Swift Interop

The SKIE plugin is configured in Gradle but currently disabled in `composeApp/build.gradle.kts` because the active Kotlin version is ahead of supported SKIE versions. Do not assume new SKIE-generated APIs are available until that flag is re-enabled.

## OpenAPI Generation

The backend's `docs/openapi/mobile_api.yaml` is the source of truth for the
mobile API contract. `core/api-generated` holds the Kotlin client generated
from it via [openapi-kmp-gen](https://github.com/kroegerama/openapi-kmp-gen).

- The upstream pin lives at `tools/openapi.lock` (repo + commit SHA + path).
- The fetched YAML is checked in at
  `core/api-generated/src/commonMain/openapi/mobile_api.yaml` so the build
  works offline.
- Generated Kotlin sources are checked in at
  `core/api-generated/src/commonMain/generated/` (4 files, ~9k lines).

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
behavior currently lives in `core/data/.../ApiClient.kt` for hand-written
call sites. While consumer migration is in progress, the two clients
coexist:

- Hand-written `feature/<name>/data/remote/<Name>Api.kt` keeps using
  `core/data`'s `ApiClient`.
- New code calling generated `<Name>Api` objects uses the bootstrapped
  `Api.client` and handles 401 explicitly via the
  `Either<CallException, HttpCallResponse<T>>` return type.

When the last hand-written API call site is removed, the existing
`ApiClient` and `ApiResponseDto<T>` envelope can be deleted in a single
follow-up commit.

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
4. Register the route entry or binding in the owning feature module, then connect it through the `composeApp` shell if needed.
5. Add the Compose screen in the owning feature module under `feature/.../feature/<name>/ui/screens/`.
6. Add shared strings and assets under `core/ui/src/commonMain/composeResources/`.

### Add A Repository Flow

1. Define the contract in `domain/repository/`.
2. Add DTOs under the owning feature module `data/remote/dto/` if the backend shape changes.
3. Add feature-owned mappers in the owning feature `data/mappers/`.
4. Add the implementation in the owning feature `data/repository/` with `@Single(binds = [...])`.
5. Keep API details in the owning feature `data/remote/`; keep DTOs and API types out of domain and UI layers.

### Add Shared UI Behavior

- Prefer Frost atoms (`BrutalistCard`, `BracketButton`, `BracketIconButton`, `BracketField`, `BracketSwitch`, `BracketSelector`, `BracketSlider`, `MultiSelectChip`, `StatusBadge`, `RowDigest`, `SectionHeading`, `IngestLine`, `PullQuote`, `AtomMark`, `InlineLink`, `Toast`, `FrostText`, `FrostIcon`, `FrostSpinner`, `FrostDialog`, `FrostScaffold`, `FrostSurface`, `FrostDivider`, `FrostCheckbox`, `FrostRadio`) in `core/ui/.../components/frost/` or `core/ui/.../components/foundation/` before inventing new patterns.
- Use Compose Resources instead of hardcoded UI text.
- Keep accessibility semantics in mind; the repo already uses headings and live regions in screens such as `SummaryListScreen`.

---

## Task Board

This repository uses Obsidian Tasks-compatible Markdown task lines as the canonical task system.
Before changing task-related files, use the `repo-task-board` skill if available.

**Source of truth:** `docs/tasks/issues/<slug>.md` — one note per task (kebab-case slug, YAML frontmatter + canonical `- [ ]` line + spec body).

**Query views** (do not add task lines here — they would double-count):
`docs/tasks/active.md` · `docs/tasks/backlog.md` · `docs/tasks/blocked.md` · `docs/tasks/dashboard.md`

**Other vault files:** `docs/tasks/board.md` (Kanban `[[slug]]` wikilinks) · `docs/tasks/templates/new-task.md` (Templater) · `docs/tasks/views/{all-tasks,by-area,by-priority}.base` (Obsidian Bases) · `docs/tasks/README.md` (structure + lifecycle reference) · `docs/tasks/.markdownlint.yaml` (MD013/MD033 relaxed in tasks folder).

Canonical syntax (lives inside `issues/<slug>.md`):

```md
- [ ] #task <imperative title> #repo/ratatoskr-client #area/<area> #status/<status> <priority> [paperclip:POY-NNN]
```

The `[paperclip:POY-NNN]` suffix is optional; include it when a task corresponds to a Paperclip (Jira) issue. Mirror the same ID in the frontmatter as `paperclip: POY-NNN`.

Allowed areas: `auth` · `api` · `kmp` · `sync` · `ci` · `frontend` · `observability` · `testing` · `content` · `scraper` · `llm` · `db` · `docs` · `ops` · `search` · `design`

Allowed statuses: `#status/backlog` · `#status/todo` · `#status/doing` · `#status/review` · `#status/blocked` · `#status/done` · `#status/dropped`

Rules: one `- [ ]` line per per-task note · update `status:` frontmatter AND `#status/*` tag together · update the `updated:` frontmatter on every transition · delete the `issues/<slug>.md` file when done (git history is the audit trail: `git log -- docs/tasks/issues/<slug>.md`) · never add task lines to the query view files.

Invoke the `repo-task-board` skill when the user mentions: roadmap, TODO, backlog, Kanban, task board, sprint, blocked work, or agent-ready work.
