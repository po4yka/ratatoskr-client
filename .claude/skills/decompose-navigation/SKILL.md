---
name: decompose-navigation
description:
  Use when adding, modifying, or wiring screens through the Ratatoskr
  navigation graph: Decompose components, child stacks, retained
  ViewModels, navigation callbacks, deep links, or shell rendering
  via MainChildDescriptor / RootChildDescriptor. Trigger on any work
  under presentation/navigation/, composeApp shell composition, or
  *Screen(component: *Component) factories.
user-invocable: false
---

# Decompose Navigation

The Ratatoskr client uses Decompose for navigation. Feature components
own routed-screen dependencies and retain ViewModels across config
changes; the `composeApp` shell renders them through descriptor types
without importing feature screens directly.

## Three-layer pattern

Every routed screen has three layers:

1. **Component interface** in
   `feature/<name>/.../presentation/navigation/<Name>Component.kt`.
   Exposes a `Value<State>` (or `StateFlow<State>`) and pure intent
   methods. Does **not** mutate navigation stacks directly.
2. **`Default<Name>Component`** in the same package. Holds the
   `ComponentContext`, retains the ViewModel via
   `retainedInstance { get<MyViewModel>() }`, and turns intents into
   navigation callbacks or ViewModel calls. Reference:
   `feature/settings/.../navigation/DefaultSettingsComponent.kt`.
3. **`<Name>Screen(component: <Name>Component, modifier: Modifier = Modifier)`**
   in `feature/<name>/.../ui/screens/`. Renders state and calls
   component methods — never `koinInject()`, never
   `navController.navigate(...)`.

## Hard rules

- **No `koinInject()` inside routed Composables.** Dependencies are
  provided by the component constructor or by an app-level provider
  in `composeApp/.../app/`.
- **No navigation stack mutation in screens.** Screens emit intents;
  components decide what happens next.
- **No importing feature route screens into shell hosts.** The shell
  renders through `MainChildDescriptor.render()` and
  `RootChildDescriptor.render()`. Feature modules expose route
  entries, not screen Composables.
- **ViewModels are retained on the component.** Use
  `retainedInstance { get<MyViewModel>() }` so config changes don't
  reinstantiate state.
- **Components own back / deep-link callbacks.** Pass them down from
  the parent component or `RootComponent`.

## Adding a new routed screen

1. Define the state class in
   `feature/<name>/.../presentation/state/<Name>State.kt`.
2. Add the ViewModel in
   `feature/<name>/.../presentation/viewmodel/`, extending
   `BaseViewModel` from `core/common`.
3. Add the component interface + `Default<Name>Component` in
   `feature/<name>/.../presentation/navigation/`.
4. Add the screen Composable in
   `feature/<name>/.../ui/screens/`, with signature
   `<Name>Screen(component: <Name>Component, modifier: Modifier = Modifier)`.
5. Expose a route entry from the feature module.
6. Wire it into the appropriate parent in
   `composeApp/.../presentation/navigation/` — usually
   `MainComponent.kt` (bottom-nav children) or `RootComponent.kt`
   (modal / overlay).
7. Render through the descriptor in the shell host
   (`MainScreen.kt` or root scaffold).

## ViewModel + delegate collaborators

For large screens, prefer breaking the ViewModel into delegates
(filter delegate, pagination delegate, etc.) injected into the
parent ViewModel rather than one bloated class. Reference
implementations:

- `feature/settings/.../SettingsViewModel`
- `feature/summary/.../SummaryDetailViewModel`

## Deep links

Deep links are dispatched through the root component's launch-action
handling in `composeApp/.../app/AppCompositionRoot.kt`. New deep-link
shapes need both:

- A route entry on the target feature module.
- A launch-action mapping at the shell layer (and, for iOS, a
  matching app-group / URL-scheme contract — see the `ios-bridge`
  skill).
