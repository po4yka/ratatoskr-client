---
title: Wire in-app language picker into Settings → Appearance
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire in-app language picker into Settings → Appearance #repo/ratatoskr-client #area/content #status/backlog 🔼

Follow-up to `add-per-app-language-picker-cross-platform` (landed the OS-level affordance: `androidApp/src/main/res/xml/locales_config.xml` declared on `<application>` so Android 13+ shows the per-app language picker under Settings → System → Languages → App languages, and `CFBundleLocalizations` + `CFBundleDevelopmentRegion` in `iosApp/iosApp/Info.plist` so iOS treats the bundle as localized and exposes Settings → Ratatoskr → Language).

## Objective

Add the in-app picker so the user can switch language without leaving the app:

1. Settings → Appearance → Language entry rendered with Frost `BracketSelector`.
2. Options: "System", "English", "Русский" (auto-populated from declared locales).
3. Android: write the choice through `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))` (or `LocaleManager.setApplicationLocales` on API 33+).
4. iOS: write `["en"]` or `["ru"]` (or remove the key for "System") to `UserDefaults.standard.set(_:forKey: "AppleLanguages")`, then surface a polite Frost `Toast` / dialog explaining a restart is required for the change to take effect everywhere.
5. Persist the choice via existing `UserPreferences` so it survives reinstall when sync is enabled.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `feature/settings/.../presentation/ui/AppearanceSection.kt` (or equivalent) gains a `BracketSelector` for language.
- A new shared `LanguagePreference` use case in `feature/settings/.../domain/usecase/`, with an `expect`/`actual` platform applier under `core/common` or `core/ui` for the platform write.
- Default = "System" → no override written, OS locale resolution kicks in.

## Constraints

- Picker resets cleanly when the user picks "System" — Android must clear via `AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())`, iOS must remove the `AppleLanguages` key entirely.
- Supported locales discovered at runtime: today `en` + `ru`. New `values-*` directories in `core/ui/src/commonMain/composeResources/` should auto-extend the picker rather than require a hardcoded list.
- iOS change requires app restart — surface this politely; do not force-kill.

## Definition of done

- Switching to RU on an EN-system device renders the app in Russian after the change is applied (Android: instant; iOS: after restart).
- Setting persists across app restarts and (when sync is enabled) across reinstalls via `UserPreferences`.
- Picker correctly resets to OS resolution when user picks "System".
