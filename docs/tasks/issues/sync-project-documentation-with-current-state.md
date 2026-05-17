---
title: Sync project documentation with current state
status: backlog
area: docs
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Sync project documentation with current state #repo/ratatoskr-client #area/docs #status/backlog ⏫

Filed from the 2026-05-17 deep audit (CC3 + drift findings).

## Objective

Multiple docs are materially out of date and will mislead agents:

- `CLAUDE.md:8` / `AGENTS.md:8` "Active Kotlin modules" line omits `core/api-generated` (it is in `settings.gradle.kts:35`).
- `CLAUDE.md:102-106` lists 2 valid Koin DSL exceptions; the actual count is **8** — `core/data/desktopMain/DesktopModule.kt` plus `feature/{auth,collections,digest,settings,summary,sync}/di/*FeatureBindings.kt`. `AuthFeatureBindings.kt:12` explains the rationale ("ViewModels are wired manually to avoid duplicate BaseViewModel KSP symbols in native frameworks") — that rationale appears in zero docs.
- `CLAUDE.md:202-215` describes the two-HTTP-client migration as "in progress"; zero feature modules still import `ApiClient`.
- `CLAUDE.md:145` says generated client is "4 files, ~9k lines"; actual is 10,604 lines.
- `README.md:70,75` advertises "Material 3"; `composeApp/AGENTS.md:31` says "Material 3 is removed from commonMain."
- `DESIGN.md:73-109,236-266` enumerates 6 Frost atoms; code ships 26 (incl. `FrostIndication` mentioned nowhere).
- `README.md:138-152` Project Structure omits `core/api-generated`.
- `docs/ARCHITECTURE.md` not freshness-banner aligned with recent changes.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Edits to `CLAUDE.md`, `AGENTS.md` (mirror), `docs/ARCHITECTURE.md`, `README.md`, `DESIGN.md`, `composeApp/AGENTS.md` reflecting the audit findings above.
- New "DI exceptions" subsection explaining the native-framework KSP rationale and listing all 8 valid DSL bindings.
- `DESIGN.md` component canon updated to the 19 frost atoms + 7 foundations actually shipped.

## Constraints

- Must not invent rules — only mirror current code reality.
- Keep CLAUDE.md and AGENTS.md content-identical (they are mirrors).

## Definition of done

- A spot-check of every "claim" the audit flagged passes against current code.
- No agent following the docs would be misled about (a) which HTTP client to use, (b) which Koin DSL bindings are valid, (c) which Frost components exist.
