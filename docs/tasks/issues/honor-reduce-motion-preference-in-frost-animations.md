---
title: Honor reduce-motion preference in Frost animations
status: backlog
area: design
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Honor reduce-motion preference in Frost animations #repo/ratatoskr-client #area/design #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Accessibility).

## Objective

Frost is restrained but still animates transitions, swipe-reveals, and the spinner. When the OS reduce-motion flag is on, collapse durations to 0.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `expect fun rememberReduceMotion(): Boolean` in `core/ui`.
- Android `actual`: `Settings.Global.TRANSITION_ANIMATION_SCALE == 0f` or `AccessibilityManager.isEnabled` heuristic.
- iOS `actual`: `UIAccessibility.isReduceMotionEnabled`.
- `core/ui/.../theme/FrostMotion.kt` durations multiply by 0 when reduce-motion is on.

## Constraints

- Respect changes at runtime (recompose when accessibility setting changes).
- Spinner replaces motion with state-based indicator when reduce-motion is on.

## Definition of done

- Toggling Android reduce-motion or iOS Reduce Motion freezes Frost transitions.
- Loading states remain visible via static "[...]" indicator.
