---
title: Audit screen semantics for TalkBack and VoiceOver
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Audit screen semantics for TalkBack and VoiceOver #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Accessibility).

## Objective

Today only 4 files reference `Modifier.semantics` (`MainScreen`, `SwipeableSummaryCard`, `CollectionViewScreen`, `SummaryListScreen`). The other ~20 screens and ~20 Frost atoms are silent to TalkBack / VoiceOver — a real accessibility hole.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Per-screen audit checklist tracked in this issue.
- `BracketIconButton`, `BracketSwitch`, `MultiSelectChip`, `BracketSelector` require explicit `contentDescription` parameter.
- Screen roots use `Modifier.semantics { heading(); liveRegion = ... }` where appropriate.
- New detekt rule blocking new `IconButton` / icon-only composables without `contentDescription`.

## Constraints

- Strings must be localized (no `"Submit"` literal — `stringResource(R.string.a11y_submit)`).
- Frost styling unaffected.

## Definition of done

- TalkBack reads every interactive control with a meaningful label.
- VoiceOver smoke test on iOS confirms the same for the major screens.
- Detekt rule prevents regression.
