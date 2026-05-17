---
title: Add per-app language picker cross-platform
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add per-app language picker cross-platform #repo/ratatoskr-client #area/content #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (i18n).

## Objective

A bilingual user might want Ratatoskr in English while running their phone in Russian (or vice versa). Android 13's `LocaleManager` and iOS's `CFBundleLocalizations` both support per-app language overrides without a system-language change.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New Settings → Appearance → Language entry (Frost `BracketSelector`).
- Android: `androidApp/src/main/res/xml/locales_config.xml` declared in manifest; settings calls `AppCompatDelegate.setApplicationLocales`.
- iOS: settings writes `UserDefaults.standard.set([code], forKey: "AppleLanguages")` and prompts a restart on change.
- Default = "System".

## Constraints

- Supported locales: en, ru (today). New locales added via Compose Resources auto-show up in the picker.
- Picker resets cleanly when the user picks "System".

## Definition of done

- Switching to RU on an EN-system device renders the app in Russian.
- iOS prompts a polite restart message on language change.
- Setting persists across reinstalls (via existing `UserPreferences` sync if applicable).
