---
title: Add reading themes (sepia and high-contrast Frost variants)
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add reading themes (sepia and high-contrast Frost variants) #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Reading Experience).

## Objective

Long-form reading is the core use case but `ReadingPreferences` only exposes font size + line height. Two new themes within Frost rules (no rainbow accents, still mono-palette):

- `SEPIA` — warm INK on cream PAGE for low-light reading.
- `HIGH_CONTRAST` — pure black ink on pure white page; respects OS accessibility setting if available.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Extend `ReadingPreferences` (`core/common`) with `readingTheme: ReadingTheme { MONO_LIGHT, MONO_DARK, SEPIA, HIGH_CONTRAST }`.
- `LocalFrostPalette` override scoped to `feature/summary/.../SummaryDetailScreen.kt` reading-settings sheet.
- Persist via existing `LayoutPreferencesManager` / `UserPreferences`.
- Frost atom previews include each new theme.

## Constraints

- Strictly two-color per theme. No accent shift beyond the canonical Spark red (which never flips).
- Theme override only affects reading detail — the rest of the app stays in MONO.

## Definition of done

- Reading-settings sheet lets users pick a theme; selection persists across app launches.
- DESIGN.md updated to enumerate the new palette variants.
- Screenshot tests cover each variant (once screenshot infra lands).
