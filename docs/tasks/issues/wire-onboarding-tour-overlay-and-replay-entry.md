---
title: Wire OnboardingTourOverlay composable and Settings replay entry
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire OnboardingTourOverlay composable and Settings replay entry #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Follow-up to `add-first-run-guided-tour` (landed the durable testable atom:
`core/common/.../util/onboarding/OnboardingTourState.kt` — pure 3-step state
reducer with `initial(savedCompleted, hasPendingDeepLink)` factory plus
`next()`, `skip()`, `replay()` transitions and a stale-tap-after-completion
no-op guard. 9 commonTest cases pin the visibility contract, fresh-install
vs deep-link defer semantics, traversal, replay, and the `STEP_COUNT = 3`
constant.).

## Objective

Bring up the user-facing parts that consume the persisted flag and reducer:

1. **`UserPreferences.tourCompleted: Boolean`** field with default `false` and a
   matching `multiplatform-settings` key in the existing UserPreferences
   repository. Migration: missing key → `false` (new install behavior).
2. **`OnboardingTourOverlay` composable** in `composeApp/.../ui/onboarding/`
   using `FrostScaffold` + `BracketButton` for `[ Next ]` / `[ Skip ]`, with
   the three step copies (`stringResource` keys
   `onboarding_step_submit_url`, `onboarding_step_share_into_app`,
   `onboarding_step_pin_widget`) and Frost-only chrome.
3. **Trigger point** — `composeApp/.../ui/AppContent.kt` (or the root
   `RootComponent` slot) calls `OnboardingTourState.initial(...)` once on
   first foreground after auth completes, threading the
   `hasPendingDeepLink` boolean from the `Intent`/`NSUserActivity` parser.
4. **Settings → Help → "Replay tour"** entry calls
   `state.replay()` and writes `tourCompleted = false`.
5. **Deep-link integration** — share-extension and `ratatoskr://` entry
   paths set `hasPendingDeepLink = true` so the tour defers to the next
   plain cold start.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/.../domain/UserPreferences.kt` — new `tourCompleted` field.
- `feature/settings/.../data/repository/UserPreferencesRepositoryImpl.kt` —
  read/write through the existing settings store.
- `composeApp/.../ui/onboarding/OnboardingTourOverlay.kt` Frost composable.
- `composeApp/.../ui/AppContent.kt` (or equivalent shell) initializes the
  reducer on first foreground.
- `feature/settings/.../ui/HelpSection.kt` — `BracketButton` "Replay tour".
- Compose Resources strings for the three step copies in `values/` +
  `values-ru/`.

## Constraints

- Frost-only chrome (no Material 3 modal coachmarks).
- Cannot block deep-link entry — the reducer already encodes the defer
  rule; the wire-up must thread `hasPendingDeepLink = true` on the
  share-extension / Universal-Link cold-start path.
- Replay must not re-run unrequested on subsequent launches — it sets
  `tourCompleted = false` exactly once, the next launch shows the tour,
  and the user's next finish/skip writes `true` again.

## Definition of done

- Fresh install + first sign-in shows the tour exactly once.
- Skipping hides it forever until manually re-triggered.
- Tour can be reset from Settings → Help → Replay tour.
- Cold-start via share intent defers the tour to the next plain launch.
