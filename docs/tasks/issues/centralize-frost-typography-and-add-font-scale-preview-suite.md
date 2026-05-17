---
title: Centralize Frost typography and add font-scale preview suite
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Centralize Frost typography and add font-scale preview suite #repo/ratatoskr-client #area/design #status/backlog 🔼

Follow-up to `add-dynamic-type-and-font-scale-support-in-frost-typography` (landed the `ClampFontScale` + `FROST_DEFAULT_MAX_FONT_SCALE = 1.5f` cap wired into `RatatoskrTheme`, so every `sp` literal across the app is clamped at the OS-driven cap).

## Objective

Two remaining pieces of the original spec:

1. Migrate any ad-hoc `fontSize = N.sp` literals in `feature/*/ui` and `composeApp/ui` to read from `AppTheme.frostType` so the central type scale stays authoritative.
2. Add a screenshot/preview suite that renders representative screens at `0.85×`, `1.0×`, `1.3×`, and `1.7×` font scales (clamped value will be `1.5×`, but exercising `1.7×` proves the cap holds) so layout regressions surface during PR review.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `rg -n 'fontSize.*\.sp' feature/ composeApp/` → migrate remaining literals (excluding test/preview-only fixtures).
- Composable previews for `SummaryListScreen`, `SummaryDetailScreen`, and at least one Settings screen at four font-scale steps.
- Layout sanity: `widthIn`, `softWrap`, `overflow = TextOverflow.Ellipsis` where horizontal space is tight.

## Constraints

- Strict Frost mono stack — no font substitution.
- Migrations are pure mechanical text replacements; do not change the visual size at scale 1.0.

## Definition of done

- Android system font-size set to "Largest" renders correctly across all screens (visual check + screenshots).
- iOS Dynamic Type accessibility sizes don't truncate critical UI.
- Screenshot tests at multiple font scales pass (once screenshot infra lands).
