---
title: Add NoHardcodedComposeText detekt rule and migrate count-driven strings to pluralStringResource
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add NoHardcodedComposeText detekt rule and migrate count-driven strings to pluralStringResource #repo/ratatoskr-client #area/content #status/backlog 🔼

Follow-up to `add-hardcoded-string-detekt-rule-and-pluralization-support` (landed the plurals foundation: `core/ui/src/commonMain/composeResources/values/plurals.xml` with English forms — one/other — and `values-ru/plurals.xml` with full Russian CLDR set — one/few/many/other — for six count-driven strings: `summary_count`, `collection_count`, `highlight_count`, `day_streak`, `minutes_per_day_target`, `items_failed_to_sync`).

## Objective

Two remaining pieces from the original brief:

1. **Custom detekt rule `NoHardcodedComposeText`** in `build-logic/` (or a small Gradle convention) that blocks new literal text in Compose call sites — `Text("hello")`, `contentDescription = "literal"`, `BracketButton(label = "Save")` — anywhere under `feature/*` and `core/ui/components/*`. Allowlist Frost Lab demos where literal text is intentional. Configure detekt to consume the rule via `customRules` dependency in `detekt.gradle.kts`.
2. **Migrate the ~18 known `Text("literal")` call sites** under `feature/` and `composeApp/` to `stringResource(Res.string.…)`. Migrate count-driven call sites (currently using `%1$d items`, `%1$d this week`, `%1$d-day streak`, `Target: %1$d minutes per day`, `%1$d items failed to sync`, plus stats screens) to `pluralStringResource(Res.plurals.…, count, count)`.
3. **Russian native-speaker review** of the plural wording landed in `values-ru/plurals.xml` (current strings are placeholder translations following correct CLDR forms but should be confirmed before shipping).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `build-logic/src/main/kotlin/.../NoHardcodedComposeText.kt` — custom detekt rule implementing `Rule`.
- `detekt.yml` — entry under `ratatoskr-rules:` with the new rule active.
- Mechanical migration: `rg -n 'Text\("' feature/ composeApp/ | rg -v '/test/'` → replace each call site with `Text(stringResource(Res.string.…))`.
- New `Res.string.…` entries added to `values/strings.xml` + `values-ru/strings.xml` where missing.

## Constraints

- Allowlist `composeApp/.../ui/frost/FrostLab*Screen.kt` (component browser, intentional literals).
- Russian plural wording confirmed by a native speaker before shipping the picker entry.

## Definition of done

- `./gradlew detekt` fails on a synthetic `Text("hello")` introduced in `feature/auth`.
- `rg -n 'Text\("[A-Za-z]' feature/ composeApp/ | rg -v 'Lab\|test'` returns zero matches.
- Plural strings render correctly in both EN and RU for counts {0, 1, 2, 5, 11, 21, 22} (sanity-check via a preview composable).
- No regression in existing localized screens.
