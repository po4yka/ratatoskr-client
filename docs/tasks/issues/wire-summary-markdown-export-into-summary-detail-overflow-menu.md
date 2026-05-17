---
title: Wire summary markdown export and Obsidian deep link into SummaryDetailScreen overflow menu
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire summary markdown export and Obsidian deep link into SummaryDetailScreen overflow menu #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `add-copy-as-markdown-and-obsidian-export` (landed deterministic `SummaryMarkdownExporter` and `ObsidianDeepLink` + tests).

## Objective

Surface the existing exporter and deep-link composer behind two overflow-menu entries on `SummaryDetailScreen`:

- **Copy as Markdown** — places the exporter output on the system clipboard.
- **Open in Obsidian** — opens the composed `obsidian://new?…` URL.

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- New `expect fun setClipboardText(text: String): Boolean` + `expect fun openExternalUrl(url: String): Boolean` in `core/common` (or use Compose `ClipboardManager` + `UriHandler` if it covers all targets).
- Android actual: `ClipboardManager` + `Intent.ACTION_VIEW`.
- iOS actual: `UIPasteboard.general` + `UIApplication.shared.open(_:)`.
- Desktop actual: AWT `Toolkit.getDefaultToolkit().systemClipboard` + `Desktop.getDesktop().browse(...)`.
- Two new entries in `SummaryDetailScreen`'s overflow menu, gated on Frost icons.
- Settings preference for default Obsidian vault name (empty = let Obsidian pick last-used).

## Constraints

- Clipboard write feedback via a Frost Toast.
- Obsidian deep link gracefully no-ops when Obsidian is not installed (catch the `ActivityNotFoundException` / `false` return).

## Definition of done

- Tapping "Copy as Markdown" places `SummaryMarkdownExporter.toMarkdown(summary)` on the clipboard.
- Tapping "Open in Obsidian" launches the Obsidian URL.
- Both entries honor a Settings toggle to hide them (off by default — show).
