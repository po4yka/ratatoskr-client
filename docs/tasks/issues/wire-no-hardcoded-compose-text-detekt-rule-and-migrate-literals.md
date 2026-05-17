---
title: Wire NoHardcodedComposeText detekt rule and migrate literal Compose text
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire NoHardcodedComposeText detekt rule and migrate literal Compose text #repo/ratatoskr-client #area/content #status/backlog 🔼

Follow-up to `add-hardcoded-string-detekt-rule-and-migrate-count-strings`
(landed the durable testable atom:
`core/common/.../util/lint/ComposeLiteralTextHeuristic.kt` — pure
`looksLikeUserText(literal): Boolean` predicate with the false-positive
bias toward letting code-shaped literals through. 12 commonTest cases
pin every branch: snake_case / kebab-case / camelCase / dot.notation
negatives, whitespace / format-placeholder / single-word-uppercase
positives, URL shapes, empty / single-char / numeric-only rejects.).

## Objective

Bring up the detekt rule plus the mechanical migration:

1. **Custom detekt rule `NoHardcodedComposeText`** in `build-logic/` (or a
   small Gradle convention) that walks Compose call sites and consumes the
   heuristic. Trigger on string literals passed as arguments to:
   - `Text`, `BasicText`
   - `BracketButton(label = …)`, `BracketIconButton(...)`,
     `BracketField(label = …)`, `BracketSwitch(label = …)`,
     `BracketSelector(...)`, `MultiSelectChip(...)`, `StatusBadge(...)`,
     etc.
   - `contentDescription = "…"`
   - `Modifier.semantics { contentDescription = "…" }`
   - `Toast(...)` body
2. **Allowlist by file path** — Frost Lab demo screens
   (`composeApp/.../ui/frost/FrostLab*Screen.kt`) are intentional literals.
   Tests are also allowlisted.
3. **Configure detekt** to consume the rule via `customRules` dependency in
   `detekt.gradle.kts`; toggle on under `ratatoskr-rules:` in `detekt.yml`.
4. **Mechanical migration** of the known ~18 `Text("literal")` call sites
   under `feature/*` and `composeApp/*`. Each call site adds a
   `Res.string.…` entry to `values/strings.xml` + `values-ru/strings.xml`.
5. **Plural-string scaffolding** — `core/ui/.../composeResources/values/plurals.xml`
   + `values-ru/plurals.xml` for the count-driven strings
   (`summary_count`, `collection_count`, `highlight_count`, `day_streak`,
   `minutes_per_day_target`, `items_failed_to_sync`). Migrate call sites
   to `pluralStringResource(Res.plurals.…, count, count)`.
6. **Russian native-speaker review** of the plural wording before shipping.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `build-logic/src/main/kotlin/.../NoHardcodedComposeText.kt` detekt Rule
  subclass consuming `ComposeLiteralTextHeuristic.looksLikeUserText`.
- `detekt.yml` updated.
- `core/ui/.../composeResources/values/plurals.xml` + `values-ru/plurals.xml`.
- Migrated call sites under `feature/` and `composeApp/`.

## Constraints

- Allowlist `composeApp/.../ui/frost/FrostLab*Screen.kt` and any tests.
- Russian plural wording confirmed by a native speaker before shipping.
- Heuristic provides the test-time contract — the Rule subclass is a thin
  AST walk that defers to the predicate.

## Definition of done

- `./gradlew detekt` fails on a synthetic `Text("hello")` in `feature/auth`.
- `rg -n 'Text\("[A-Za-z]' feature/ composeApp/ | rg -v 'Lab|test'`
  returns zero matches.
- Plural strings render correctly in EN and RU for counts {0, 1, 2, 5, 11,
  21, 22} (preview composable sanity check).
- No regression in existing localized screens.
