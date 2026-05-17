---
title: Reduce composeApp iOS framework export surface
status: backlog
area: kmp
priority: high
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Reduce composeApp iOS framework export surface #repo/ratatoskr-client #area/kmp #status/backlog ⏫

Filed from the 2026-05-17 deep audit (build H, security).

## Objective

`composeApp/build.gradle.kts:23-32` exports `core:apiGenerated`, `core:common`, `core:data`, `core:navigation`, every `feature/*`, plus `decompose.core` and `koin.core` into the `ComposeApp.framework` ObjC interface. Exporting `core:data` makes Ktor, SQLDelight native driver, Wire runtime, and Multiplatform Settings part of the public ObjC ABI of the framework — bloats the binary, slows linking, and contradicts the "Domain contracts and UI code must not import data.remote APIs or DTOs" rule by surfacing them via Swift autocomplete.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Audit which symbols the iOS host actually consumes (`iosApp/iosApp/iOSApp.swift`, `ShareExtension/`, `RecentSummariesWidget/`).
- Keep `export(...)` for only the modules whose symbols the Swift side actually references — likely `core:common`, `core:navigation`, and the specific feature components instantiated from Swift. Drop `core:data` and unused features.

## Constraints

- Must not regress ShareExtension or RecentSummariesWidget compile.
- Verify framework still satisfies SKIE if/when re-enabled later.

## Definition of done

- `ComposeApp.framework` ObjC header surface trimmed; binary size reduced (record before/after).
- iOS build green; share extension + widget functional.
