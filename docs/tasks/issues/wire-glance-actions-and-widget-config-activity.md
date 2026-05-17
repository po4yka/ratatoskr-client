---
title: Wire Glance ActionCallbacks and RecentSummariesWidgetConfigActivity
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire Glance ActionCallbacks and RecentSummariesWidgetConfigActivity #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `add-glance-widget-interactive-actions-and-configuration` (landed
the durable testable atom: `core/common/.../util/widget/WidgetRowAction.kt`
with a sealed `WidgetRowAction.MarkRead | Archive` hierarchy plus
`WidgetRowActionCodec.encode/decode` over a string-keyed map. 9 commonTest
cases pin round-trips, wire-format uppercase, missing/blank/unknown defenses,
and the namespaced key strings.).

## Objective

Bring up the Android-specific callback plumbing + configuration activity that
consume the atom:

1. **`MarkReadAction` / `ArchiveAction`** in `androidApp/.../widget/actions/`:
   each implements `ActionCallback`, reads the `ActionParameters` into a
   `Map<String, String>`, hands the map to `WidgetRowActionCodec.decode`, and
   on a non-null result routes through `SummaryRepository.markRead(id)` /
   `archive(id)`, then calls
   `RecentSummariesWidget().update(context, glanceId)` to redraw.
2. **Row composables** wrap content in
   `Button(onClick = actionRunCallback<MarkReadAction>(parameters))`. Parameter
   keys come from `WidgetRowActionCodec.ACTION_KEY` / `SUMMARY_ID_KEY` so the
   encoder/decoder share constants.
3. **`RecentSummariesWidgetConfigActivity.kt`** — Compose UI with Frost
   `BracketSelector` for tag / collection. Persists the selected filter via
   `GlanceStateDefinition` so a long-press → Reconfigure picks up where the
   user left off.
4. **Manifest** — `<appwidget-provider android:configure="…ConfigActivity"/>`.
5. **Filter integration** — `RecentSummariesWidget` reads the persisted
   `GlanceState` filter at render time, hands it to the existing
   `SummaryRepository` query.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `androidApp/.../widget/actions/MarkReadAction.kt` +
  `ArchiveAction.kt`.
- `androidApp/.../widget/RecentSummariesWidgetConfigActivity.kt`.
- Manifest update.
- `RecentSummariesWidget` reads filter from GlanceState.

## Constraints

- Glance content uses hardcoded Frost INK/PAGE constants (per `glance-widgets`
  skill in CLAUDE.md, widgets are exempt from AppTheme but must follow Frost
  visual rules).
- Action callbacks run on a worker dispatcher — never block the main thread.
- After action, call `RecentSummariesWidget().update(context, glanceId)` to
  redraw.

## Definition of done

- Tapping Mark-Read updates SQLDelight and the widget redraws within 500 ms.
- Long-press → Reconfigure opens the config activity; saved filter persists.
- Widget honors filter (only summaries matching tag / collection shown).
