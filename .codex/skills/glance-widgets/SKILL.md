---
name: glance-widgets
description:
  Use when adding, modifying, or theming Android Glance widgets in
  androidApp/src/main/kotlin/com/po4yka/ratatoskr/widget/. Glance has
  unique constraints: no RatatoskrTheme access, no Compose Resources,
  no Frost atoms, hardcoded INK/PAGE color constants. Trigger on any
  change under that widget package or to a widget manifest entry.
user-invocable: false
---

# Android Glance Widgets

Glance widgets render on the Android home screen via RemoteViews
under the hood. They do **not** share Compose-UI infrastructure with
the main app and must follow stricter rules.

Current widgets:

- `androidApp/.../widget/RecentSummariesWidget.kt`
- `androidApp/.../widget/RecentSummariesContent.kt`
- `androidApp/.../widget/RecentSummariesWidgetReceiver.kt`

## What Glance can't see

- **`RatatoskrTheme` and `AppTheme.colors.*` are inaccessible.**
  Glance has its own composable surface that doesn't run inside the
  app's Compose tree.
- **Frost atoms** (`BracketButton`, `FrostText`, etc.) are
  `commonMain` Compose components, **not** Glance composables, and
  cannot be reused inside `GlanceAppWidget`.
- **Compose Resources** (`Res.string.*`) are not available inside
  Glance. Use Android `R.string.*` from `androidApp/src/main/res/`.

## Hard rules

- **Hardcode Frost INK / PAGE constants** in widget composables —
  there is no theme provider. Mirror the values from `DESIGN.md`:
  - ink: `#1C242C` light / `#E8ECF0` dark
  - page: `#F0F2F5` light / `#12161C` dark
  - spark (critical only): `#DC3545`
- **Visuals must still match Frost**: 0 corner radius, 1px hairline,
  no shadows, monospace where Glance permits, single accent for
  critical state only. See the `frost-design-system` skill.
- **Glance-only composables** live under
  `androidApp/.../widget/`. Don't cross-import them from feature
  modules — they don't run on iOS or desktop.
- **Data access is read-only** through the same repositories the app
  uses. Mutating state from a widget should round-trip through a
  WorkManager job or a deep-link launch action.

## Adding a new widget

1. Subclass `GlanceAppWidget` in
   `androidApp/.../widget/<Name>Widget.kt`.
2. Add the `<Name>WidgetReceiver` extending
   `GlanceAppWidgetReceiver`.
3. Register the receiver + widget metadata XML in
   `androidApp/src/main/AndroidManifest.xml` and
   `androidApp/src/main/res/xml/`.
4. Inside the widget's `Content`, use Glance composables
   (`Column`, `Row`, `Text`, `Box`, `Button`) with `GlanceModifier`
   and the hardcoded Frost colors.
5. Hook deep links via `actionStartActivity(...)` with the same
   launch-action URIs the main app handles in
   `shared/sharedLogic/.../app/AppCompositionRoot.kt`.

## Refresh + sync

Widgets typically refresh from a WorkManager periodic job under
`androidApp/.../worker/`. Don't run long work inside
`GlanceAppWidget.provideGlance` — fetch from a repository that reads
the local cache populated by sync (see the `sync-orchestration`
skill).
