---
name: repo-task-board
description: Use when creating, updating, triaging, or completing repository tasks in `docs/tasks/issues/<slug>.md`. Trigger on mentions of roadmap, TODO, backlog, Kanban, task board, sprint, blocked work, agent-ready work, or any change under `docs/tasks/`. The `issues/<slug>.md` notes are the source of truth; `active.md`, `backlog.md`, `blocked.md`, `dashboard.md`, and `board.md` are Obsidian Tasks/Bases query views — never add task lines to them.
---

# Repository Task Board — ratatoskr-client

This repository uses Obsidian Tasks-compatible Markdown task lines as the canonical
task system. Each task is **one note** under `docs/tasks/issues/<slug>.md`. That
note is the only place a task line lives. The other files under `docs/tasks/` are
**query views** that aggregate those notes — they must never contain a task line
of their own, because Obsidian Tasks would then double-count it.

The canonical lifecycle and frontmatter schema live in
`docs/tasks/README.md`. Read it once when in doubt; this skill captures the
day-to-day rules.

## Canonical task line

Inside `docs/tasks/issues/<slug>.md`:

```md
- [ ] #task <imperative title> #repo/ratatoskr-client #area/<area> #status/<status> <priority> [paperclip:POY-NNN]
```

The `[paperclip:POY-NNN]` suffix is optional; include it when the task has a
Paperclip (Jira) cross-reference.

## Frontmatter schema

```yaml
---
title: Imperative task title
status: doing          # backlog | todo | doing | review | blocked | done | dropped
area: sync             # auth | api | kmp | sync | ci | frontend | observability | testing | content | scraper | llm | db | docs | ops | search | design
priority: high         # critical | high | medium | low
owner: Role name
paperclip: POY-NNN     # optional
blocks: []
blocked_by: []
created: YYYY-MM-DD
updated: YYYY-MM-DD
---
```

## Allowed status tags

`#status/backlog` · `#status/todo` · `#status/doing` · `#status/review` ·
`#status/blocked` · `#status/done` · `#status/dropped`

Priority markers: `🔺` critical · `⏫` high · `🔼` medium · `🔽` low.

## Hard rules

- **Source of truth is `docs/tasks/issues/<slug>.md`.** Never add task lines
  to `active.md`, `backlog.md`, `blocked.md`, `dashboard.md`, or `board.md` —
  those are query/Kanban views, and a literal `- [ ]` line in any of them
  would be double-counted by Obsidian Tasks.
- **Exactly one `#status/*` tag per task.** When transitioning, update the
  `status:` frontmatter field AND the `#status/*` tag on the canonical
  `- [ ]` line in the same edit — they must agree.
- **One note per task, kebab-case filename.** Create via the Templater
  template at `docs/tasks/templates/new-task.md` so frontmatter, tags, and
  task line stay in sync.
- **When blocked**, set `status: blocked`, change the tag to `#status/blocked`,
  and add an indented bullet under the task line explaining the blocker
  (so `blocked.md` shows the reason).
- **On completion**, change `[ ]` to `[x]`, set both `status: done` and
  `#status/done`, add `✅ YYYY-MM-DD`, and **delete the issue file**.
  Git history (`git log -- docs/tasks/issues/<slug>.md`) is the audit trail —
  leaving completed notes around clutters Bases views.
- **Preserve `[paperclip:POY-N]` tokens** on existing tasks across edits.

## Creating a task

1. Search `docs/tasks/issues/` for a similar slug — never duplicate.
2. If a related task exists, prefer extending it (acceptance criteria,
   linked blockers) over creating a parallel note.
3. Otherwise, create `docs/tasks/issues/<kebab-case-slug>.md` from the
   Templater template. Fill the frontmatter, the canonical `- [ ]` line,
   and a short spec body (problem · approach · acceptance criteria) — only
   include the body when the work isn't self-explanatory from the title.
4. Set the right initial `status`: `backlog` for "someone will pick this up
   later", `todo` for "ready to start now", `doing` only when actively in
   flight.

## AI-assistant rule

After implementing a task, delete `docs/tasks/issues/<slug>.md` as part of
the same change. The commit (and `git log`) become the record. Do not leave
the file behind with `status: done` — the canonical rule is "close = delete".

## Vault setup

Open the **repo root** as your Obsidian vault, not `docs/tasks/`. Bases
views, the Tasks plugin queries, and the Kanban board all resolve paths
relative to the root.
