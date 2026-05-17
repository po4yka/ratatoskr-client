---
title: Add Android direct share targets from frequent collections
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Android direct share targets from frequent collections #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (Android).

## Objective

When the user invokes the Android share sheet on a URL, surface the user's most-used Ratatoskr collections as direct share shortcuts ("Save to Reading List", "Save to Work"). Skips the in-app collection picker entirely.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `androidApp/src/main/res/xml/shortcuts.xml` gains a `<share-target>` entry for `text/plain` URLs.
- New `DirectShareSeeder` periodically pushes top-4 frequent collections via `ShortcutManagerCompat.pushDynamicShortcut`.
- Each shortcut, when used, lands in `MainActivity` with the target collection id pre-attached.

## Constraints

- Re-seed shortcuts after each successful collection use (LRU).
- Cap at 4 shortcuts to respect Android limits.
- No PII in shortcut labels beyond user-chosen collection names.

## Definition of done

- Sharing a URL shows frequent collections as one-tap targets above the standard app row.
- Tapping a shortcut creates the summary in the right collection without opening the picker.
