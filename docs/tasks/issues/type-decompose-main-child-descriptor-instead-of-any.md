---
title: Type Decompose MainChildDescriptor.component instead of Any
status: backlog
area: kmp
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Type Decompose MainChildDescriptor.component instead of Any #repo/ratatoskr-client #area/kmp #status/backlog 🔼

Filed from the 2026-05-17 deep audit (architecture H4).

## Objective

`core/navigation/src/commonMain/.../MainNavigation.kt:23` declares `MainChildDescriptor(val component: Any, ...)`; `RootNavigation.kt:14` is the same (`RootChildDescriptor.component: Any`). Decompose's central guarantee — typed component composition — is erased to `Any` at every navigation seam, forcing `as?` casts and preventing host-level observation of component-derived state.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `ScreenComponent` marker interface in `core/navigation` extending `com.arkivanov.decompose.ComponentContext` (or composing it).
- Every feature's routed component implements `ScreenComponent`.
- Change `component: Any` → `component: ScreenComponent` in both descriptors.

## Constraints

- Lifecycle, BackHandler, `instanceKeeper` behavior unchanged.
- No new dependency on Decompose internals beyond what the public API exposes.

## Definition of done

- Zero `as? SomeComponent` casts in navigation routing.
- All feature components compile against the new type.
