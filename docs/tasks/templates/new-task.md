---
title: <% tp.system.prompt("Task title (imperative verb phrase)") %>
status: backlog
area: <% tp.system.suggester(["auth","api","kmp","sync","ci","frontend","observability","testing","content","scraper","llm","db","docs","ops","search","design"], ["auth","api","kmp","sync","ci","frontend","observability","testing","content","scraper","llm","db","docs","ops","search","design"]) %>
priority: <% tp.system.suggester(["critical","high","medium","low"], ["critical","high","medium","low"]) %>
owner: <% tp.system.prompt("Owner role") %>
blocks: []
blocked_by: []
created: <% tp.date.now("YYYY-MM-DD") %>
updated: <% tp.date.now("YYYY-MM-DD") %>
---

- [ ] #task <% tp.frontmatter["title"] %> #repo/ratatoskr-client #area/<% tp.frontmatter["area"] %> #status/backlog 🔼

## Objective

## Context

## Acceptance criteria

- [ ]

## Definition of done
