---
title: Add smart collections from saved searches
status: backlog
area: search
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add smart collections from saved searches #repo/ratatoskr-client #area/search #status/backlog 🔼

Filed from the 2026-05-17 nice-to-have feature brainstorm (Discovery & Organization).

## Objective

`SearchFilters` and `recentSearches` already exist in `feature/summary` search; collections are static today. Let users save a query+filter combo as an auto-populating "smart collection" that always reflects the current matches.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `SmartCollection` domain type in `feature/collections.domain` with serialized `SearchQuery + SearchFilters`.
- "Save as collection" action from `SearchScreen.kt`.
- `CollectionsScreen.kt` renders smart collections inline with regular ones, marked with a `StatusBadge("SMART")`.
- Tapping a smart collection opens a list view that re-runs the saved query against current data.

## Constraints

- Smart collections live alongside regular ones; do not break existing collection sharing/ACL.
- Query is re-evaluated on open — no cached materialized view yet.

## Definition of done

- User can save a search, see it in collections, and open it to view current matches.
- Smart collections sync via existing sync mechanism (or are local-only initially, documented).
