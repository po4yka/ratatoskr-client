---
version: alpha
name: Frost
description: >
  Editorial monospace minimalism for the Ratatoskr Kotlin Multiplatform client.
  Two-color rule (ink + page) with a single critical accent (spark), eight-step
  alpha ladder, signal ramp, and brutalist component architecture (1px hairline,
  0 corner radius, no shadow). This file is the mobile-platform projection of
  the canonical Frost design system maintained in Figma file
  `dvCkDlNR6CKgfekPgrWo87` and exported as `frost-tokens.json` (DTCG format,
  currently v2.13.0). The current `core/ui` token set carries seed values from
  the previous IBM Carbon theme; migrating those tokens to Frost values is
  in-progress and this file is the migration target.
colors:
  ink: "#1C242C"
  ink-dark: "#E8ECF0"
  page: "#F0F2F5"
  page-dark: "#12161C"
  spark: "#DC3545"
  ink-pure: "#000000"
  page-pure: "#FFFFFF"
typography:
  mono-xs:
    fontFamily: JetBrains Mono
    fontSize: 11px
    fontWeight: 500
    lineHeight: 130%
    letterSpacing: 1px
  mono-sm:
    fontFamily: JetBrains Mono
    fontSize: 12px
    fontWeight: 500
    lineHeight: 130%
    letterSpacing: 0.3px
  mono-body:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: 500
    lineHeight: 130%
    letterSpacing: 0.3px
  mono-emph:
    fontFamily: JetBrains Mono
    fontSize: 13px
    fontWeight: 800
    lineHeight: 130%
    letterSpacing: 1px
  serif-reader:
    fontFamily: Source Serif 4
    fontSize: 16px
    fontWeight: 500
    lineHeight: 155%
    letterSpacing: 0px
    fontFeature: italic
  serif-reader-zoom:
    fontFamily: Source Serif 4
    fontSize: 22px
    fontWeight: 500
    lineHeight: 155%
    letterSpacing: 0px
    fontFeature: italic
rounded:
  none: 0px
spacing:
  cell: 8px
  half-line: 8px
  line: 16px
  gap-ext: 10px
  gap-inline: 4px
  gap-row: 8px
  gap-section: 48px
  gap-page: 64px
  pad-page: 32px
components:
  brutalist-card:
    backgroundColor: "{colors.page}"
    textColor: "{colors.ink}"
    typography: "{typography.mono-body}"
    rounded: "{rounded.none}"
    padding: "{spacing.line}"
  brutalist-card-critical:
    backgroundColor: "{colors.page}"
    textColor: "{colors.ink}"
    typography: "{typography.mono-emph}"
    rounded: "{rounded.none}"
    padding: "{spacing.line}"
  pull-quote:
    backgroundColor: "{colors.page}"
    textColor: "{colors.ink}"
    typography: "{typography.serif-reader-zoom}"
    rounded: "{rounded.none}"
    padding: "{spacing.line}"
  bracket-button:
    backgroundColor: "{colors.page}"
    textColor: "{colors.ink}"
    typography: "{typography.mono-emph}"
    rounded: "{rounded.none}"
    padding: 8px 16px
  status-badge:
    backgroundColor: "{colors.page}"
    textColor: "{colors.ink}"
    typography: "{typography.mono-xs}"
    rounded: "{rounded.none}"
    padding: 4px 8px
  mark:
    backgroundColor: "{colors.ink}"
    textColor: "{colors.ink}"
    typography: "{typography.mono-body}"
    rounded: "{rounded.none}"
    padding: 0 2px
---

## Overview

Frost is the project-owned design system for the Ratatoskr KMP
client (Compose Multiplatform: Android + iOS, with desktop as a
development target). It is intentionally **post-Carbon**: the
`AppColors` and `AppType` token sets currently in
`core/ui/src/commonMain/kotlin/.../theme/` carry seed values from
the previous IBM Carbon-derived theme so the codemod removing the
old design system stayed mechanical. Those values (the
`#0F62FE` interactive blue, `#24A148` success green, `#DA1E28`
support error, IBM Plex-style scale) are migration debt. Frost is
the migration target.

Frost's three principles:

1. **Two-color rule.** Ink and page invert in dark mode; nothing
   else changes color. The only chromatic value is `spark`
   (`#DC3545`), reserved for critical-signal accents (a 4px leading
   hairline on mobile, 2px on web). Spark stays the same in both
   modes — it is a physical pigment, not a UI affordance.
2. **Brutalism.** 0 corner radius (`RoundedCornerShape(0.dp)` or
   no shape at all), 1px hairline borders only, no shadows, no
   gradients, no Material elevation. Cards are slabs. Buttons are
   bracketed monospace text. The medium is the message.
3. **Signal-driven hierarchy.** Every row, heading, and toast picks
   one of four signal levels (low / mid / high / critical). Signal
   maps to weight + alpha + case, not to color.

The Figma source-of-truth lives at file id `dvCkDlNR6CKgfekPgrWo87`.
Mobile artboards are on page 12 (M01–M23, 393×690 each). M23 ·
Highlights (added v2.13.0) is the canonical reference for composing
Brutalist-Card, Pull-Quote, Atom/Mark, and Row/Digest on a single
mobile screen.

## Colors

Light mode is `ink: #1C242C` on `page: #F0F2F5`. Dark mode flips
to `ink: #E8ECF0` on `page: #12161C`. Spark (`#DC3545`) never
flips. `ink-pure` and `page-pure` exist for print/PDF export only.

Color is **forbidden** as a hierarchy device. Vary the alpha applied
to ink instead (eight-step ladder):

| Token         | Value | Use                                                    |
|---------------|-------|--------------------------------------------------------|
| `quiet`       | 0.25  | Watermarks, decoration only — fails AA, never carry text |
| `dot`         | 0.40  | Separator dots, dotted/dashed borders                  |
| `inactive`    | 0.50  | Inactive filter buttons                                |
| `low-signal`  | 0.55  | Low-signal text (signal score < 0.3)                   |
| `meta`        | 0.60  | Timestamps, secondary meta                             |
| `secondary`   | 0.70  | Section headings, ingest line                          |
| `active-soft` | 0.85  | Hover-out, dense secondary, body in compact rows       |
| `active`      | 1.00  | Active text, mid/high/critical signal                  |

In Compose, apply the alpha at the call site (`AppTheme.colors.textPrimary.copy(alpha = 0.55f)`)
or — preferably, post-migration — bind to a Frost-aliased token
that returns the composed color/alpha pair.

The `supportSuccess`, `supportWarning`, `linkPrimary`, and
`interactive` slots in `AppColors.kt` are migration debt. New
Compose code must not bind to them.

## Typography

Two families:

- **JetBrains Mono** — every UI surface (labels, body, meta,
  headings, status, code). Three weights: thin 400, body 500
  (default), emph 800.
- **Source Serif 4 italic** — reader body only. Used for the
  article reading view and pull-quotes. Never for UI chrome.

Tracking: `tight` 0.3px for mobile body (the default on this
client), `label` 1px for UPPERCASE labels and nav, `wordmark`
2px for the wordmark.

Line-height: `body` 130% default, `reader` 155% for Source Serif
italic.

The current `AppType.kt` declares slots (`body01`, `bodyCompact01`,
`heading02..04`, `headingCompact01`, `label01`) using
`FontFamily.SansSerif`. After migration, these should resolve to
JetBrains Mono / Source Serif via Compose Resources fonts. Until
then, treat the slot names as stable seams; the bodies will swap.

## Layout

The grid is **cellular**, not column-based. The cell is `8.dp`.
A mobile artboard is `393.dp` wide (~48 cells) and `690.dp` tall
in Frost's reference frame. Below 768.dp the mobile artboard owns
the layout; the 768–1199.dp tablet range uses web tokens.

Vertical rhythm is `line: 16.dp` (2 cells). Section gaps are
`gap-section: 48.dp` (6 cells). Page-level rhythm is
`gap-page: 64.dp` (8 cells). Page horizontal padding is
`pad-page: 32.dp` (4 cells).

Compose practice:

- Lay out screens with `Column { ... }` + `verticalArrangement = Arrangement.spacedBy(16.dp)`.
- Insert section dividers using `HorizontalDivider(color = AppTheme.colors.textPrimary.copy(alpha = 0.5f))`.
- Avoid `padding(20.dp)`, `padding(24.dp)`, etc. — snap to multiples of `cell` (8.dp).

Status bar height (mobile artboard) is 42.dp; home indicator is
21.dp. Header is 54.dp (one wordmark row + one tab-bar row). The
remaining ~573.dp is content.

## Shapes

`rounded.none = 0.dp` is the only radius token. There is no `sm`,
`md`, `lg`. In Compose, that means `RectangleShape` or
`RoundedCornerShape(0.dp)`. `Material3.Shapes` defaults
(`RoundedCornerShape(12.dp)` for medium, etc.) are wrong for this
codebase and must be overridden.

Borders are 1.dp hairline ink-bound at alpha 0.40 for separators,
0.50 for row dividers, 1.00 for keyboard focus. The single
exception is the **spark bar** — a 4.dp leading edge painted in
`spark`, signalling critical state.

No shadows. No `Modifier.shadow()`. No `elevation` parameter on
`Card` (use a `Box` with a hairline border instead, or migrate to
a `BrutalistCard` composable that wraps the canon).

## Components

The mobile canon set lives in Figma page 09 (Molecules), refined on
page 12 (Mobile Views). After migration, these should land as
Compose components under `core/ui/src/commonMain/kotlin/.../components/`.

Selected anchors:

- **Brutalist-Card** — slab card with optional 4.dp leading spark
  hairline. State enum: `default | critical`. The whole article
  list is built from these.
- **Pull-Quote** — Source Serif 4 italic body, mono uppercase
  attribution. Used in the reader and on M23 · Highlights.
- **Bracket-Button** — `[ LABEL ]` literal brackets, mono ExtraBold
  uppercase. The brackets are characters in a `Text` composable,
  not a `Surface` background.
- **Status-Badge** — pill-shaped mono uppercase, severity enum
  `info | warn | alarm`. Alarm adds the spark bar; alarm never
  paints text red. Maps to summary-pipeline state in the bot
  (fetching / processing / brief-ready / fail).
- **Atom / Mark** — inline text-highlight (v2.13.0). For Compose,
  implement via `AnnotatedString` with a `SpanStyle` carrying
  `background = ink.copy(alpha = 0.08f)` and
  `textDecoration = TextDecoration.Underline`. Variants:
  `style=match | passage`.
- **Row / Digest** — single-row card of mono cells. Used in the
  summary list.

When adding a screen, prefer composing existing components over
inventing new ones. New patterns belong in Figma first; the design
system is the contract.

## Do's and Don'ts

**Do**

- Use `AppTheme.colors.textPrimary` + a literal alpha for
  hierarchy. After migration this becomes the Frost alpha tokens.
- Reserve spark for fatal/critical states only.
- Pick a signal level (`low | mid | high | critical`) for every
  text node. Picking is mandatory; defaulting is a defect.
- Snap dimensions to cell multiples (8.dp, 16.dp, 24.dp, 32.dp,
  48.dp, 64.dp). Arbitrary `.dp` values break the cell grid.
- Use `RoundedCornerShape(0.dp)` (or `RectangleShape`) on every
  surface that takes a shape parameter.

**Don't**

- Don't use color for hierarchy. Blues, greens, ambers do not
  exist in Frost. `Color(0xFF0F62FE)`, `Color(0xFF24A148)`, and
  `Color(0xFFF1C21B)` from `AppColors.kt` are legacy seed values
  pending migration; new Compose code must not bind to them.
- Don't use Material 3 elevation. `CardDefaults.elevatedCardElevation()`,
  `Modifier.shadow(...)`, `tonalElevation` — all defects.
- Don't add corner radius. The repo currently uses
  `RoundedCornerShape` with non-zero values in legacy screens;
  those are migration debt.
- Don't paint text in spark. Critical state is a 4.dp leading bar,
  not red text.
- Don't use `FontFamily.SansSerif` for new code. Mono is JetBrains
  Mono via Compose Resources; serif is Source Serif 4 italic.
- Don't introduce new motion or transitions. Frost permits seven
  animations (`blinker`, `pulse`, `toast`, `click-press`,
  `select-pulse`, `drag-lift`, `undo-fade`); all collapse to 1ms
  under `prefers-reduced-motion`. `AnimatedVisibility` with
  default specs is generally too soft.
- Don't bind Material 3 surfaces to non-Frost colors. The
  `MaterialTheme(colorScheme = appColors.toMaterialColorScheme(...))`
  bridge in `core/ui/.../theme/Theme.kt` exists so the in-flight
  Material UI keeps working during migration; new components
  should consume `AppTheme.colors` directly and skip the bridge.

## References

- Canonical tokens: `frost-tokens.json` (DTCG, tracked with the
  Frost project separately from this repo).
- Figma source: file id `dvCkDlNR6CKgfekPgrWo87`. Mobile views on
  page 12. M23 · Highlights (page 12, 393×690 at x=946 y=2310) is
  the v2.13.0 reference composition.
- Compose token shim (legacy, pending migration):
  `core/ui/src/commonMain/kotlin/com/po4yka/ratatoskr/core/ui/theme/`
  (`AppColors.kt`, `AppType.kt`, `AppTheme.kt`, `Theme.kt`).
- Architecture entrypoint for UI: `composeApp/CLAUDE.md` (UI rules)
  and `docs/ARCHITECTURE.md` (module dependency rules).
- Where this file and the code disagree, this file is the target.
