---
title: Migrate sp literals to AppTheme.frostType and add FontScaleBucket preview composables
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Migrate sp literals to AppTheme.frostType and add FontScaleBucket preview composables #repo/ratatoskr-client #area/design #status/backlog 🔼

Follow-up to `centralize-frost-typography-and-add-font-scale-preview-suite`
(landed the durable testable atom:
`core/ui/.../theme/FontScaleBucket.kt` — discrete bucketing of the
continuous OS font-scale into the four canonical preview points
{0.85x, 1.0x, 1.3x, 1.7x} with `clampedScale()` honoring the existing
`FROST_DEFAULT_MAX_FONT_SCALE = 1.5f` cap, `nearest()` integer-math
classifier with smaller-bucket tie preference, and
`previewScales` / `previewClampedScales` lists. 11 commonTest cases
pin the contract.).

## Objective

Bring up the two follow-on pieces of the original brief:

1. **sp literal migration** — `rg -n 'fontSize.*\.sp' feature/ composeApp/`
   then migrate each ad-hoc literal to read from `AppTheme.frostType` so
   the central type scale stays authoritative. Excludes test fixtures
   and preview-only sites.
2. **Preview suite composables** for the canonical screens at four
   font-scale steps using `FontScaleBucket.previewScales()`:
   - `SummaryListScreenPreviews.kt` — list-row layout, content
     description sanity check.
   - `SummaryDetailScreenPreviews.kt` — reading-detail layout, atom
     drill-down hover state.
   - `SettingsAppearancePreviews.kt` — Frost `BracketSelector` rows.
   Each emits `@Preview` per `FontScaleBucket.entries`.
3. **Layout sanity** — `widthIn`, `softWrap`, `overflow = TextOverflow.Ellipsis`
   where horizontal space is tight (any row that currently uses fixed
   width + `Text` that would clip at 1.5x).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Mechanical migration of remaining sp literals.
- Three preview files using `FontScaleBucket.previewScales()`.
- Targeted `widthIn` / `softWrap` / `TextOverflow.Ellipsis` patches.

## Constraints

- Strict Frost mono stack — no font substitution.
- Migrations are pure mechanical text replacements; do not change the
  visual size at scale 1.0x.
- Screenshot golden file names are tied to bucket iteration order
  (`FontScaleBucket.entries`) — do not reorder.

## Definition of done

- Android system font-size set to "Largest" renders correctly across
  all screens (visual check + screenshots).
- iOS Dynamic Type accessibility sizes don't truncate critical UI.
- Screenshot tests at multiple font scales pass (once screenshot infra
  lands).
- `rg -n 'fontSize.*\.sp' feature/ composeApp/ | rg -v 'test|preview'`
  returns zero matches.
