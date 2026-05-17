---
title: Add haptic feedback on Frost micro-interactions
status: backlog
area: design
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add haptic feedback on Frost micro-interactions #repo/ratatoskr-client #area/design #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (cross-platform).

## Objective

Subtle tactile confirmation on submit / mark-read / pin / archive — consistent with Frost's restrained motion language. Avoid over-firing (no haptic on every recomposition).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `expect fun performHaptic(kind: HapticKind)` in `core/ui` (`Confirm`, `Reject`, `Selection`).
- Android `actual` uses `View.performHapticFeedback(HapticFeedbackConstants.CONFIRM)` (Android 14+, fallback to `KEYBOARD_TAP`).
- iOS `actual` uses `UIImpactFeedbackGenerator(style: .light)`.
- Frost atoms `BracketButton`, `BracketSwitch`, swipeable card fire haptics on success path.
- Settings toggle to disable.

## Constraints

- Honor `Settings.System.HAPTIC_FEEDBACK_ENABLED` and iOS reduce-motion.
- No haptic on background events — only direct user actions.

## Definition of done

- Confirm action fires one subtle haptic.
- Disable toggle suppresses all app haptics.
- Reduce-motion / OS haptic-off respected.
