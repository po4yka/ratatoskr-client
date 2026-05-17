---
title: Wire LanguagePreference platform applier and Settings → Appearance picker UI
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire LanguagePreference platform applier and Settings → Appearance picker UI #repo/ratatoskr-client #area/content #status/backlog 🔼

Follow-up to `wire-in-app-language-picker-settings-screen` (landed the persistence atom: `core/common/.../domain/model/LanguagePreference.kt` enum — `System | English | Russian` — with round-trip helpers, `feature/settings/.../domain/repository/LanguagePreferenceRepository.kt` contract, `feature/settings/.../data/repository/LanguagePreferenceRepositoryImpl.kt` backing the choice via a `multiplatform-settings` `in_app_language_preference` string key, and 5 commonTest cases pinning the storage format using `MapSettings`).

## Objective

Bring up the user-facing parts that consume the persisted preference:

1. **Platform applier** — `expect class LanguagePreferenceApplier { fun apply(preference: LanguagePreference) }` in `core/common/.../util/locale/`:
   - Android: `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))`, or `getEmptyLocaleList()` for `System`.
   - iOS: writes `["<tag>"]` to `UserDefaults.standard.set(_:forKey: "AppleLanguages")`, or removes the key for `System`. Surface a polite Frost `Toast` / dialog explaining a restart is required.
   - Desktop: no-op (desktop is a dev target only — no per-app locale story).
2. **Settings → Appearance → Language** entry on `AppearanceSection.kt` with a Frost `BracketSelector` over the three enum values, bound to `LanguagePreferenceRepository.getLanguagePreference()`. On change, call `repository.updateLanguagePreference(...)` then `applier.apply(...)`.
3. **DI bindings** for `LanguagePreferenceApplier` actuals in `AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt`.
4. **Auto-population** — the picker should iterate `LanguagePreference.entries`, not hardcode a list, so adding a fourth locale is a one-line change in the enum plus a Compose Resources `values-*` directory.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/src/commonMain/.../util/locale/LanguagePreferenceApplier.kt` — `expect class`.
- `core/common/src/{androidMain,iosMain,desktopMain}/.../util/locale/LanguagePreferenceApplier.kt` — actuals.
- `feature/settings/.../presentation/ui/AppearanceSection.kt` updated.
- iOS restart-required Frost toast in `core/ui/.../components/foundation/` (or use the existing `Toast` atom).

## Constraints

- iOS change requires app restart for full effect — surface politely, never force-kill.
- Auto-populate the picker from `LanguagePreference.entries` so adding a new locale is one line.
- Desktop applier stays a no-op (development target only).

## Definition of done

- Switching to Russian on an EN-system device renders the app in Russian after the change is applied (Android: instant; iOS: after restart).
- Setting persists across app restarts (verified locally — the persistence atom already round-trips).
- Picker correctly resets to OS resolution when the user picks System.
