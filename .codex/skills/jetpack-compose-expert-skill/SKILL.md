---
name: jetpack-compose-expert
description: Use when writing Compose or Compose Multiplatform code in this Ratatoskr KMP repo — state, layout, modifiers, side effects, lazy lists, navigation, animation, performance, or recomposition questions. Trigger on @Composable, remember, LaunchedEffect, Scaffold, LazyColumn, Modifier, recomposition, expect/actual, ComposeUIViewController, UIKitView, Res.string/drawable. For Frost atoms or design tokens, defer to `frost-design-system`. For Decompose components and routed-screen wiring, defer to `decompose-navigation`. Do NOT reintroduce Material 3 in commonMain — it has been removed in favor of Frost.
---

# Compose & Compose Multiplatform — Ratatoskr

Practical guidance for writing correct, performant Compose code in this repo.
This skill covers general Compose patterns. Project-specific concerns are
covered by sibling skills:

- **Design system, atoms, tokens, theming** → `frost-design-system`
- **Routed screens, components, navigation** → `decompose-navigation`
- **Android home-screen widgets** → `glance-widgets`

## Hard project rules

- **No Material 3 in `commonMain`.** `MaterialTheme`, `Surface { elevation }`,
  `Card`, `Button`, `OutlinedButton`, `OutlinedTextField` etc. have been
  removed. Use Frost atoms and foundation primitives — see
  `frost-design-system`.
- **No `koinInject()` inside routed Composables.** Routed-screen
  dependencies come from the Decompose component constructor or an
  app-level provider in `composeApp/.../app/`. See `decompose-navigation`.
- **Use Compose Resources for text.** `stringResource(Res.string.foo)` via
  `ratatoskr.core.ui.generated.resources.*` — never hardcode UI strings.
- **Cancellation discipline.** When using broad `catch (Throwable)` /
  `catch (Exception)` blocks inside Compose-launched coroutines, rethrow
  `kotlin.coroutines.cancellation.CancellationException` first.

## Key principles

1. **Three phases: Composition → Layout → Drawing.** A state read in each
   phase only invalidates that phase and later ones. Animating a color
   via `Modifier.drawBehind` is cheaper than reading the color in
   composition.
2. **Recomposition is frequent and cheap** — if you let the compiler skip
   unchanged scopes. Prefer stable types; avoid allocating lambdas or
   lists inline in composable bodies.
3. **Modifier order matters.** `Modifier.padding(16.dp).background(Red)` is
   visually different from `Modifier.background(Red).padding(16.dp)`.
4. **Hoist state only as high as it needs to go.** Don't push every piece of
   state into a ViewModel by default — page-local UI state belongs in
   `remember { … }`.
5. **Side effects bridge declarative to imperative.** `LaunchedEffect` for
   keyed coroutines, `DisposableEffect` for cleanup, `SideEffect` for
   sending current state to non-Compose systems. Misusing them causes
   bugs that are hard to trace.

## Compose Multiplatform notes

- UI code in `commonMain` is portable; platform-specific APIs
  (`LocalContext`, `BackHandler`, `Window`) require `expect`/`actual` or
  conditional source sets.
- iOS hosts the framework via `ComposeUIViewController` exported from
  `composeApp/` through CocoaPods. See `ios-bridge` for the SwiftUI side.
- The SKIE Swift-interop plugin is configured but currently disabled
  (Kotlin version is ahead of supported SKIE). Don't write Swift code
  that assumes SKIE-generated APIs.

## Reference files

For deeper Compose internals or specific topics, read the bundled refs:

| Topic | Reference File |
|-------|---------------|
| `remember`, `mutableStateOf`, state hoisting, `derivedStateOf`, `snapshotFlow` | `references/state-management.md` |
| Structuring composables, slots, extraction, preview | `references/view-composition.md` |
| Modifier ordering, custom modifiers, `Modifier.Node` | `references/modifiers.md` |
| `LaunchedEffect`, `DisposableEffect`, `SideEffect`, `rememberCoroutineScope` | `references/side-effects.md` |
| `CompositionLocal`, `LocalContext`, `LocalDensity`, custom locals | `references/composition-locals.md` |
| `LazyColumn`, `LazyRow`, `LazyGrid`, `Pager`, keys, content types | `references/lists-scrolling.md` |
| `NavHost`, type-safe routes, deep links, shared element transitions | `references/navigation.md` |
| `animate*AsState`, `AnimatedVisibility`, `Crossfade`, transitions | `references/animation.md` |
| Recomposition skipping, stability, baseline profiles, benchmarking | `references/performance.md` |
| Semantics, content descriptions, traversal order, testing | `references/accessibility.md` |
| Removed/replaced APIs, migration paths from older Compose versions | `references/deprecated-patterns.md` |
| Figma/screenshot decomposition, design tokens, spacing, modifier ordering | `references/design-to-compose.md` |
| Production crash patterns, defensive coding, state/performance rules | `references/production-crash-playbook.md` |
| Compose Multiplatform, `expect`/`actual`, resources (`Res.*`), migration | `references/multiplatform.md` |
| Desktop (Window, Tray, MenuBar), iOS (UIKitView) | `references/platform-specifics.md` |

### Source code refs (available)

| Module | File |
|--------|------|
| Runtime | `references/source-code/runtime-source.md` |
| UI | `references/source-code/ui-source.md` |
| Navigation | `references/source-code/navigation-source.md` |
| CMP | `references/source-code/cmp-source.md` |
