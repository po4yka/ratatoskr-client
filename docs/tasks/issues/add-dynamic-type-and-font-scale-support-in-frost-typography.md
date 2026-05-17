---
title: Add dynamic type and font-scale support in Frost typography
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add dynamic type and font-scale support in Frost typography #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Accessibility).

## Objective

Today Frost typography uses fixed `sp` literals scattered across atoms. Honor the OS text-size preference (Android Settings → Display → Font size; iOS Dynamic Type) so users with large text settings don't see truncation.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `FrostTypography` central object routing every text style through `LocalDensity.fontScale`, clamped to a sensible max (e.g., 1.5×).
- Frost preview suite with 0.85× / 1.0× / 1.3× / 1.7× variants.
- All atoms in `core/ui/.../components/frost/` migrate to the central typography.

## Constraints

- Strict Frost mono stack — no font substitution.
- Layout must not break at extreme scales (use `widthIn`, `softWrap`, `overflow = TextOverflow.Ellipsis`).

## Definition of done

- Android system font-size set to "Largest" renders correctly across all screens.
- iOS Dynamic Type accessibility sizes don't truncate critical UI.
- Screenshot tests at multiple font scales pass.
