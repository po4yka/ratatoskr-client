---
title: Wire LanguagePreferenceApplier using LocaleTagParser + Settings → Appearance picker UI
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire LanguagePreferenceApplier using LocaleTagParser + Settings → Appearance picker UI #repo/ratatoskr-client #area/content #status/backlog 🔼

Follow-up to `wire-language-picker-platform-applier-and-settings-ui` (landed
the durable testable atom:
`core/common/.../util/locale/LocaleTagParser.kt` — pure BCP47 canonicalizer
+ `resolve(rawTag): LanguagePreference` collapse-to-base-language helper
+ Java-underscore-to-dash normalization + script/region/variant casing
rules. 12 commonTest cases pin the contract: language lowercase, region
uppercase, script title case, variant lowercase, underscore replacement,
trim, null/blank → null, regional collapse to base language, unknown
locales → System, case-insensitive resolve, idempotence.).

## Objective

Bring up the platform applier + Settings picker UI that consume the parser:

1. **`expect class LanguagePreferenceApplier`** in
   `core/common/.../util/locale/`:
   - Android actual: `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(preference.tag))`,
     or `getEmptyLocaleList()` for [LanguagePreference.System].
   - iOS actual: writes `["<tag>"]` to
     `UserDefaults.standard.set(_:forKey: "AppleLanguages")`, or removes
     the key for `System`. Surface a polite Frost `Toast` / dialog
     explaining a restart is required.
   - Desktop actual: no-op (dev target only).
2. **Settings → Appearance → Language** entry on `AppearanceSection.kt`
   with a Frost `BracketSelector` over `LanguagePreference.entries`,
   bound to `LanguagePreferenceRepository.getLanguagePreference()`. On
   change, call `repository.updateLanguagePreference(...)` then
   `applier.apply(...)`.
3. **DI bindings** for `LanguagePreferenceApplier` actuals in
   `AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt`.
4. **Auto-populate the picker** from `LanguagePreference.entries` so
   adding a fourth locale is a one-line change in the enum plus a
   `values-*` Compose Resources directory.
5. **Inbound parsing** — for any inbound tag from `AppleLanguages` /
   OS callbacks, route through `LocaleTagParser.resolve(rawTag)` before
   matching to the enum. This is what handles "en-US" → English etc.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/src/commonMain/.../util/locale/LanguagePreferenceApplier.kt`
  — `expect class`.
- `core/common/src/{androidMain,iosMain,desktopMain}/.../util/locale/LanguagePreferenceApplier.kt`
  — actuals.
- `feature/settings/.../presentation/ui/AppearanceSection.kt` updated.
- iOS restart-required Frost toast (or reuse the existing `Toast` atom).

## Constraints

- iOS change requires app restart for full effect — surface politely,
  never force-kill.
- Auto-populate the picker from `LanguagePreference.entries`.
- Desktop applier stays a no-op.

## Definition of done

- Switching to Russian on an EN-system device renders the app in
  Russian after the change is applied (Android: instant; iOS: after
  restart).
- Setting persists across app restarts (already round-trips through
  the persistence atom).
- Picker correctly resets to OS resolution when the user picks System.
- Inbound `AppleLanguages` value "en-US" still resolves to English in
  the picker UI (via `LocaleTagParser.resolve`).
