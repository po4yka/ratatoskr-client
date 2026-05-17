---
name: frost-design-system
description:
  Use when adding, modifying, or removing UI tokens, components,
  colors, shapes, motion, typography, or surfaces in the Ratatoskr
  client. Frost is editorial monospace minimalism with strict rules:
  two-color (ink/page), single critical accent (spark), 0 corner
  radius, 1px hairline, no shadows, no Material elevation. Trigger
  on any work under core/ui, composeApp, or feature/*/ui, and on
  references to Frost atoms (BrutalistCard, BracketButton,
  BracketField, FrostText, etc.).
user-invocable: false
---

# Frost — the project's design system

Frost is the live, project-owned design system. The canonical spec is
`DESIGN.md` at the repo root (DESIGN.md format,
https://github.com/google-labs-code/design.md). Read it before adding
or modifying any token, component, color, shape, or motion rule. The
mobile-platform projection there is the source of truth; the Figma
file `dvCkDlNR6CKgfekPgrWo87` exports as `frost-tokens.json` (DTCG v2.13.0).

## Hard rules (do not violate)

- **Two colors only**: ink (`#1C242C` light / `#E8ECF0` dark) and page
  (`#F0F2F5` light / `#12161C` dark).
- **Single accent**: `spark` (`#DC3545`) for critical state only. Does
  not flip between light and dark.
- **0 corner radius everywhere.** No `RoundedCornerShape(...)`,
  no `clip(CircleShape)`, no soft edges.
- **1px hairline** for dividers, borders, brackets.
- **No shadows, no Material elevation.** Frost surfaces are flat.
- **Monospace typography**: JetBrains Mono via Frost type tokens
  (`mono-xs`, `mono-sm`, `mono-body`, …).

Material 3 has been **removed from `commonMain`**. Do not reintroduce
`MaterialTheme`, `Surface { elevation = ... }`, `Card`, `Button`,
`OutlinedButton`, or `OutlinedTextField` in shared code.

## Where Frost lives

- **Atoms** —
  `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/components/frost/`:
  `BrutalistCard`, `BracketButton`, `BracketIconButton`, `BracketField`,
  `BracketSwitch`, `BracketSelector`, `BracketSlider`, `MultiSelectChip`,
  `StatusBadge`, `RowDigest`, `SectionHeading`, `IngestLine`, `PullQuote`,
  `AtomMark`, `InlineLink`, `Toast`, `FrostSpinner`, `FrostCheckbox`,
  `FrostRadio`.
- **Foundation primitives** — `core/ui/.../components/foundation/`:
  `FrostText`, `FrostIcon`, `FrostDialog`, `FrostScaffold`,
  `FrostSurface`, `FrostDivider`.
- **Theme entry**: `RatatoskrTheme` in `core/ui/.../theme/Theme.kt`.
- **Tokens**: `core/ui/.../theme/` — `Spacing`, `Dimensions`,
  `IconSizes`, `FrostSpacing`, plus Frost color and type tokens.

The `AppTheme` object in `core/ui/.../theme/AppTheme.kt` is the
legacy token shim carrying seed values from the previous Carbon
theme; migrating those tokens to Frost values is in progress.
`DESIGN.md` is the migration target — when in doubt, the spec wins.

## When extending Frost

1. Reach for an existing atom or foundation primitive first. Compose
   them before inventing a new one.
2. If a new atom is genuinely needed:
   - Add it under `core/ui/.../components/frost/`.
   - Honor the bracket-marker / monospace visual language.
   - Honor every hard rule above.
   - Update the canonical atom list in `CLAUDE.md`, `AGENTS.md`, and
     `composeApp/AGENTS.md` in the same change.
3. New tokens go in `core/ui/.../theme/` and **must be documented in
   `DESIGN.md`** in the same change.

## Documented exceptions

- **Glance widgets** (`androidApp/.../widget/`) are platform-specific
  and don't see `RatatoskrTheme`. They use hardcoded Frost INK/PAGE
  constants directly but must still follow Frost visual rules
  (0 radius, 1px hairline, no shadows, two-color, monospace).
  See the `glance-widgets` skill.
- **iOS share-sheet / widget** UI in `iosApp/` is SwiftUI and is not
  Frost-managed at the Compose layer — visual parity is enforced
  manually. See the `ios-bridge` skill.

## Localization

User-facing text lives in
`core/ui/src/commonMain/composeResources/values/strings.xml` and
`values-ru/strings.xml`. Use `stringResource(Res.string...)` from
`ratatoskr.core.ui.generated.resources.*`. Never hardcode UI strings.
