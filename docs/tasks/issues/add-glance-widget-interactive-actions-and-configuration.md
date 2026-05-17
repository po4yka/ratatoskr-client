---
title: Add Glance widget interactive actions and configuration
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Glance widget interactive actions and configuration #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (Android).

## Objective

Today `androidApp/.../widget/RecentSummariesWidget` is read-only — taps deep-link into the app. Glance 1.1 supports inline buttons via `actionRunCallback`. Two complementary upgrades:

1. **Interactive actions** — Mark-Read and Archive buttons inline on each row.
2. **Configuration activity** — long-press → Reconfigure lets users pick a tag / collection filter; persisted via `GlanceStateDefinition`.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `MarkReadAction` / `ArchiveAction` `ActionCallback` classes carrying the summary id.
- Each row wraps content in `Button(onClick = actionRunCallback<MarkReadAction>(parameters))`.
- New `RecentSummariesWidgetConfigActivity.kt` (Compose UI) with Frost `BracketSelector` for tag/collection.
- Manifest `<appwidget-provider android:configure="…"/>` reference.

## Constraints

- Glance content must use hardcoded Frost INK/PAGE constants (no Material chrome, per `glance-widgets` skill).
- Action callbacks run on a worker dispatcher — never block.
- After action, call `RecentSummariesWidget().update(context, glanceId)` to redraw.

## Definition of done

- Tapping the Mark-Read button updates SQLDelight and the widget redraws within 500ms.
- Long-press → Reconfigure opens the config activity; saved filter persists.
- Widget honors filter (only summaries matching tag/collection shown).
