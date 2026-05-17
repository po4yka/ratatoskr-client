---
title: Add hardcoded-string detekt rule and pluralization support
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add hardcoded-string detekt rule and pluralization support #repo/ratatoskr-client #area/content #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (i18n).

## Objective

i18n today is paired EN/RU at exactly 517 keys each, but ~18 Compose files still contain literal `Text("…")` calls. Plus there is zero `pluralStringResource` usage — Russian few/many forms don't translate cleanly. Two paired fixes:

1. Custom detekt rule `NoHardcodedComposeText` blocking new `Text("literal")` / `contentDescription = "literal"` in `feature/*` and `core/ui/components/*`.
2. Adopt `pluralStringResource` for count-driven strings (summaries, collections, reading-goal items).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New custom detekt rule in `build-logic` (or a small Gradle convention) implementing the rule.
- New `core/ui/src/commonMain/composeResources/values/plurals.xml` + `values-ru/plurals.xml`.
- Migrate the ~18 known literal sites to `stringResource()`.
- Migrate count-driven strings to `pluralStringResource`.

## Constraints

- Allowlist Frost Lab demos (where literal text is intentional).
- Russian plurals validated by a native speaker.

## Definition of done

- `./gradlew detekt` fails on a synthetic `Text("hello")` introduced in `feature/auth`.
- Plural strings render correctly in both EN and RU for counts {0, 1, 2, 5, 11, 21}.
- No regression in existing localized screens.
