---
title: Add reading width and typography controls
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add reading width and typography controls #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Reading Experience).

## Objective

On tablets and desktop the article body stretches edge-to-edge, fighting reading comfort. Expose a typographic preference for max line length (45–75ch) and text alignment (start vs justify).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `ReadingPreferences.maxLineWidthCh: Int` (default 65) and `textAlign: TextAlign` (default Start).
- Apply via `Modifier.widthIn(max = N.dp)` derived from `LocalDensity` + character width metric in the body composable of `SummaryDetailScreen.kt`.
- Settings sheet entry with a Frost `BracketSlider` for width.

## Constraints

- No new fonts. Mono stack stays JetBrains Mono.
- Defaults must look identical to today's layout on phone-sized screens.

## Definition of done

- Body width responds to slider on desktop + tablet, ignored on phone widths that are already narrower.
- Setting persists via `LayoutPreferencesManager`.
