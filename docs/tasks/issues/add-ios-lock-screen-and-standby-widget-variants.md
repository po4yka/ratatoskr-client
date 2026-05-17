---
title: Add iOS Lock Screen and StandBy widget variants
status: backlog
area: frontend
priority: low
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Add iOS Lock Screen and StandBy widget variants #repo/ratatoskr-client #area/frontend #status/backlog 🔽

Filed from the 2026-05-17 nice-to-have platform-polish brainstorm (iOS).

## Objective

Extend the existing `RecentSummariesWidget` to support iOS 16+ Lock Screen complications and iOS 17+ StandBy Mode. The same timeline data drives:

- `accessoryCircular` — unread count.
- `accessoryRectangular` — freshest summary title.
- `accessoryInline` — single-line headline.
- StandBy variant — large-text headline tuned for bedside viewing distance.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- Extend `RecentSummariesWidget.swift` `supportedFamilies` list with the four accessory families.
- New compact layouts in `RecentSummariesView.swift` keyed off `widgetFamily` env.
- StandBy-tuned layout via `containerBackground(...)` with INK background.

## Constraints

- Frost INK/PAGE constants; no system tint.
- StandBy must remain readable from 2m+ distance — use the largest body size that fits.

## Definition of done

- Lock Screen widget gallery shows three Ratatoskr variants.
- StandBy preview renders the headline at large size.
- All variants update on the same timeline as the existing systemMedium widget.
