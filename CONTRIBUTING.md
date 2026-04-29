# Contributing to Ratatoskr Client

Thanks for your interest. This is a single-maintainer project, so
turnaround on PRs and issues may vary — but contributions are
welcome and read carefully.

## Quickstart

1. Read [`README.md`](README.md) and [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md)
   for prerequisites, environment setup, and per-platform build
   commands.
2. Read [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the module
   layout (`core/*` + `feature/*`), dependency direction, and DI rules.
3. For day-to-day feature work, the AI-agent skill at
   [`.claude/skills/building-kmp-features/SKILL.md`](.claude/skills/building-kmp-features/SKILL.md)
   ([`.codex/`](.codex/skills/building-kmp-features/SKILL.md) mirror)
   describes the canonical layout for adding a screen, repository,
   or use case.

## Workflow

1. **Fork** and create a feature branch off `main`.
   Keep branch names short and descriptive
   (`fix/sync-retry-loop`, `feature/highlight-export`,
   `docs/api-clarifications`).
2. **Commit** in conventional-style prefixes mirroring `git log`
   in this repo: `fix:`, `chore:`, `deps(deps):`, `docs:`,
   `feature:`, `rename:`, `perf:`. Subject in imperative present
   tense, ≤72 chars; body wrapped at ~72 chars when needed.
   Mention the why, not the what — the diff already shows what.
3. **Verify locally** before pushing:
   ```bash
   ./gradlew detekt ktlintCheck
   ./gradlew :composeApp:compileDebugKotlinAndroid \
             :composeApp:linkDebugFrameworkIosSimulatorArm64 \
             :composeApp:compileKotlinDesktop
   ./gradlew :androidApp:assembleDebug
   ```
   For iOS-impacting changes:
   ```bash
   cd iosApp && pod install && cd ..
   xcodebuild -workspace iosApp/iosApp.xcworkspace -scheme iosApp \
     -configuration Debug -sdk iphonesimulator
   ```
4. **Open a PR**. The
   [`pull_request_template.md`](.github/pull_request_template.md)
   covers test coverage, platform-tested checkboxes, accessibility,
   and the architecture-rule checklist. Fill in everything that
   applies.

## Architecture rules (non-negotiable)

These are enforced by the `verifyArchitectureBoundaries` Gradle task
that runs on every `check` invocation:

- A feature module may depend on another feature's **public domain
  contracts only**. Never import another feature's `data/` or
  `presentation/` package.
- Shell code in `composeApp/` must not import feature implementation
  types from `data/` or `presentation/`.
- `core/*` modules must not depend on feature modules.
- Routed Composables must not call `koinInject()` directly — read
  routed-screen dependencies from the Decompose component.
- Domain types must not import `data.remote` APIs or DTOs.

If the boundary task fails, fix the import — don't allow-list around
it. The allowed-edges map in
[`build-logic/.../ArchitectureBoundaryRules.kt`](build-logic/src/main/kotlin/com/po4yka/ratatoskr/buildlogic/ArchitectureBoundaryRules.kt)
is the source of truth for cross-feature exceptions.

## UI rules

- Use `RatatoskrTheme`, `AppTheme.colors`/`AppTheme.type`, and components from `core/ui/`.
- Add user-facing strings to `core/ui/src/commonMain/composeResources/values{,-ru}/strings.xml`,
  not to `androidApp/` resources.
- Reuse existing components from `core/ui/.../components/` before
  introducing new ones.
- Maintain accessibility semantics (headings, content descriptions,
  live regions for status changes).

## Tests

- Unit tests live in each module's `src/commonTest/`.
- Per-module test runs:
  `./gradlew :feature:<name>:allTests` and `./gradlew :core:<name>:allTests`.
- Android-only unit tests: `./gradlew :composeApp:testDebugUnitTest`.
- The full suite runs in CI on every PR.
- See [`docs/TESTING.md`](docs/TESTING.md) for testing patterns and
  fakes/mocks conventions.

## Documentation

If your change alters behavior described in `docs/`, update the
relevant doc in the same PR. The most commonly affected files are
listed in [`docs/INDEX.md`](docs/INDEX.md).

If you change patterns that the AI-agent skill describes (DI rules,
file map, screen pattern), update both
`.claude/skills/building-kmp-features/SKILL.md` and the `.codex/`
mirror. They must stay byte-identical.

## Questions?

For general questions, use [GitHub Discussions](https://github.com/po4yka/ratatoskr-client/discussions/new?category=q-a).

For bug reports and feature requests, use the
[issue templates](.github/ISSUE_TEMPLATE/).

For security issues, see [`SECURITY.md`](SECURITY.md) — do not open
a public issue.
