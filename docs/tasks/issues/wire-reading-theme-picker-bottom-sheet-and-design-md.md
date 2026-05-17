---
title: Wire reading-theme picker into SummaryDetailScreen reading-settings sheet
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire reading-theme picker into SummaryDetailScreen reading-settings sheet #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `wire-reading-theme-picker-into-summary-detail-and-persist-via-userpreferences` (landed the persistence atom: moved the `ReadingTheme` enum from `core/ui/.../FrostColors.kt` to `core/common/.../domain/model/ReadingTheme.kt` so domain code can use it, added `readingTheme: ReadingTheme = MONO_LIGHT` to `ReadingPreferences`, extended `ReadingPreferencesRepository` with `updateReadingTheme(...)` plus a multiplatform-settings `KEY_READING_THEME` string column, and added 4 round-trip contract tests covering future-enum-value fallback and string-key stability).

## Objective

Surface the persisted reading-theme through the actual SummaryDetailScreen UX:

1. **`SummaryDetailViewModel`** already exposes `state.readingPreferences` — add a `setReadingTheme(theme)` handler that calls `readingPreferencesRepository.updateReadingTheme(theme)`.
2. **Reading-settings bottom sheet** on `SummaryDetailScreen` gains a Frost `BracketSelector` over the four `ReadingTheme` values. Localized labels in `values/strings.xml` + `values-ru/strings.xml`.
3. **Scope the palette swap**: wrap only the reading-detail surface in `CompositionLocalProvider(LocalFrostColors provides paletteFor(state.readingPreferences.readingTheme))` so the rest of the app shell (chrome, lists, settings) stays in canonical Frost MONO regardless of the user's reading choice.
4. **DESIGN.md update**: enumerate the new palette variants (`SEPIA`, `HIGH_CONTRAST`) once the picker ships so the canonical spec stays in sync.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New entry on the existing reading-settings sheet (or a new bottom sheet if the existing surface doesn't have room) — `BracketSelector` bound to `state.readingPreferences.readingTheme`.
- Localized option labels: "MONO Light", "MONO Dark", "Sepia", "High Contrast" (EN); equivalents in RU.
- DESIGN.md updated under the Reading palettes section.
- Screenshot tests (once screenshot infra lands) covering each variant.

## Constraints

- Theme override only affects the reading detail surface — the app shell stays in MONO.
- DESIGN.md is the spec; do not introduce a palette that contradicts the two-color ink/page rule (spark stays spark — already proved invariant by `FrostReadingPaletteTest`).

## Definition of done

- Reading-settings sheet lets users pick a theme; selection persists across app launches (via the `KEY_READING_THEME` settings key that already landed).
- DESIGN.md updated to enumerate the new palette variants.
- Screenshot tests cover each variant.
