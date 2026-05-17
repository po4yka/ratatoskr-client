---
title: Add Frost accent color guard and DESIGN.md validator
status: backlog
area: design
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by:
  - sync-project-documentation-with-current-state
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add Frost accent color guard and DESIGN.md validator #repo/ratatoskr-client #area/design #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have engineering brainstorm (Design-System Tooling).

## Objective

DESIGN.md is the contract for Frost, but nothing enforces it. Two automated guards:

1. **Accent-color guard:** the canonical palette is INK / PAGE / SPARK. Any `Color(0x…)` literal outside the Frost palette in feature/composeApp code fails the build.
2. **DESIGN.md ↔ atom directory validator:** the `components:` frontmatter list in DESIGN.md must match `ls core/ui/.../components/frost/`. Drift fails the build.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Gradle task `verifyFrostPalette` scanning `*.kt` under `feature/` and `composeApp/` for hex `Color(0x...)` literals; allowlist comes from `FrostPalette.kt`.
- Gradle task `verifyDesignMd` parsing DESIGN.md frontmatter and comparing to `core/ui/.../components/frost/` directory.
- Both depended on by `check`.

## Constraints

- Allow `Color.Transparent` / `Color.Unspecified`.
- Allow temporary debug overlays guarded by a `// frost-allow` magic comment.

## Definition of done

- Adding a non-Frost hex color fails CI with a clear error pointing at the file.
- Adding a Frost atom without updating DESIGN.md fails CI.
- Both tasks complete in under 5s.
