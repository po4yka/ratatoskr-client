---
title: Add copy-as-markdown and Obsidian export
status: backlog
area: content
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add copy-as-markdown and Obsidian export #repo/ratatoskr-client #area/content #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Export & Interop).

## Objective

Power users dump summaries into their "second brain" tools. Two complementary actions in the `SummaryDetailScreen.kt` overflow menu: "Copy as Markdown" (clipboard) and "Open in Obsidian" (via `obsidian://new?vault=…&content=…` deep link).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `SummaryMarkdownExporter` in `feature/summary/.../export/` (commonMain) rendering YAML frontmatter + TLDR + atoms (as bullets) + full content + tags + source URL.
- Two new overflow-menu entries with Frost icons.
- Obsidian deep-link composer that URL-encodes correctly on both platforms.

## Constraints

- Markdown is deterministic — running export twice on the same summary produces byte-identical output.
- Obsidian deep link uses configurable vault name (settings preference, default empty = Obsidian picks last-used).

## Definition of done

- Copy-as-Markdown places valid GFM on the clipboard.
- Open-in-Obsidian launches Obsidian with the summary populated.
- Markdown output passes basic linting (no orphan headers, no unescaped pipes in tables).
