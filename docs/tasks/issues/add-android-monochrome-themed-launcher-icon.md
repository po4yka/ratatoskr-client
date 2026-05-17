---
title: Add Android monochrome themed launcher icon
status: backlog
area: design
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android monochrome themed launcher icon #repo/ratatoskr-client #area/design #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (Android).

## Objective

On Android 13+ with Material You "Themed icons" enabled, the launcher tints icons with the wallpaper palette. Today the Ratatoskr icon is the standard adaptive icon — themed mode would render the foreground silhouette as ink. Add a `<monochrome/>` layer for proper themed-icon support.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `androidApp/src/main/res/drawable/ic_launcher_monochrome.xml` — vector silhouette of the Ratatoskr mark in INK on transparent.
- Update `androidApp/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` to add `<monochrome android:drawable="@drawable/ic_launcher_monochrome"/>`.

## Constraints

- Foreground silhouette must read well at 24dp tint preview.
- Pure mono — no gradients, no inner shadows.

## Definition of done

- "Themed icons" toggle in Android 13+ launcher applies a tinted Ratatoskr icon.
- Standard (non-themed) icon unchanged.
