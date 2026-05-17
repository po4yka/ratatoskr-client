---
title: Wire reading-theme picker into SummaryDetailScreen and persist via UserPreferences
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire reading-theme picker into SummaryDetailScreen and persist via UserPreferences #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `add-reading-themes-sepia-and-high-contrast-frost-variants` (landed the `ReadingTheme` enum, `frostSepia`, `frostHighContrast` palettes, and the `paletteFor` resolver in `core/ui` with palette-invariant tests).

## Objective

Hook the reading-time palette swap into the actual SummaryDetailScreen surface so users can pick between MONO / Sepia / High Contrast and have the selection persist.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Extend `UserPreferences` (or the existing `LayoutPreferencesManager`) with `readingTheme: ReadingTheme` defaulting to `MONO_LIGHT` (or follow current dark-mode value at first launch).
- New reading-settings bottom sheet entry on `SummaryDetailScreen` with a Frost `BracketSelector` over the four `ReadingTheme` values.
- Scope the palette swap: wrap the reading surface in `CompositionLocalProvider(LocalFrostColors provides paletteFor(theme))` so only the detail content uses the override; rest of the app stays in canonical Frost MONO.
- Persist the selection across app launches.

## Constraints

- Theme override only affects reading detail — the app shell / list / settings stay in MONO.
- DESIGN.md updated to enumerate the new palette variants once the picker ships.

## Definition of done

- Reading-settings sheet lets users pick a theme; selection persists across app launches.
- DESIGN.md updated to enumerate the new palette variants.
- Screenshot tests cover each variant (once screenshot infra lands).
