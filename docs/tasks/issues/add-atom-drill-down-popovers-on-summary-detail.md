---
title: Add atom drill-down popovers on summary detail
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add atom drill-down popovers on summary detail #repo/ratatoskr-client #area/content #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Reading Experience).

## Objective

`SummaryInsights.newFacts` is already in the domain model but rendered as a static list. Tap on an atom (key fact) should open a Frost dialog with: the originating excerpt from `fullContent`, the source/confidence, and a "scroll to in article" action — a "show your work" UX that builds trust in the summary.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `AtomDrillDownDialog` composable in `feature/summary/.../ui/components/`.
- Wire `AtomMark` clicks in `SummaryDetailScreen.kt` to open the dialog.
- Use existing `InsightFact.confidence` field.
- Fallback fuzzy-match within `fullContent` when backend doesn't provide offset.

## Constraints

- Pure Frost styling (no Material chrome, no shadows).
- Works offline once the summary is cached.
- If `newFacts` is empty, no clickable affordance.

## Definition of done

- Tapping a `newFact` atom opens the dialog with the matching excerpt highlighted.
- "Scroll to source" jumps to the excerpt within the article body.
- Performance: dialog opens within 100ms on mid-range Android.
