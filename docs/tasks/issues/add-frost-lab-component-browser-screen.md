---
title: Add Frost Lab component browser screen
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Frost Lab component browser screen #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (DX).

## Objective

A Storybook-equivalent for the design system: single screen rendering every Frost atom with controls. Doubles as the source for screenshot tests and as documentation for new contributors.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `composeApp/src/commonMain/.../ui/screens/FrostLabScreen.kt`.
- Route added to `RootComponent` behind a debug-build flag (also accessible from Settings → Diagnostics in release).
- Each Frost atom (`BracketButton`, `BracketField`, `MultiSelectChip`, etc.) gets a section with `BracketSelector` for state variants (disabled, error, focused).
- Theme variants (light / dark / sepia / high-contrast) selectable.

## Constraints

- Frost Lab itself must follow Frost rules — no Material 3 chrome.
- Adding a new Frost atom requires adding a Lab entry (enforced by `DESIGN.md ↔ Frost atom consistency` check).

## Definition of done

- All 26 currently-shipped Frost atoms (per DESIGN.md sync task) appear in the Lab.
- Theme picker re-renders the entire screen instantly.
- Screenshot tests can target the Lab.
