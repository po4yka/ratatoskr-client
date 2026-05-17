---
title: Wire SummaryExportActionsUseCase into SummaryDetailScreen overflow menu with platform actuals
status: backlog
area: frontend
priority: medium
owner: Senior KMP Compose Multiplatform Engineer (Ratatoskr Client)
blocks: []
blocked_by: []
created: 2026-05-17
updated: 2026-05-17
---

- [ ] #task Wire SummaryExportActionsUseCase into SummaryDetailScreen overflow menu with platform actuals #repo/ratatoskr-client #area/frontend #status/backlog 🔼

Follow-up to `wire-summary-markdown-export-into-summary-detail-overflow-menu` (landed the `ClipboardWriter` and `ExternalUrlOpener` interfaces in `core/common/util/share/`, the `SummaryExportActionsUseCase` orchestrating `SummaryMarkdownExporter` + `ObsidianDeepLink` against those interfaces, and a 5-test contract suite using fake writers to prove the exporter output reaches the clipboard verbatim and the percent-encoded `obsidian://new?…` URL reaches the opener verbatim).

## Objective

Add platform actuals for the two new interfaces and surface the two overflow-menu entries on `SummaryDetailScreen`:

1. **`ClipboardWriter` actuals** — Android `ClipboardManager.setPrimaryClip(ClipData.newPlainText(...))`, iOS `UIPasteboard.general.setValue(text, forPasteboardType: UTType.text.identifier)` (use the typed-value form on iOS 14+; falls back cleanly when `string` is the only available type), Desktop `Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(text), null)`.
2. **`ExternalUrlOpener` actuals** — Android `Intent(ACTION_VIEW, Uri.parse(url)).addFlags(FLAG_ACTIVITY_NEW_TASK)` with `ActivityNotFoundException` → return false; iOS `UIApplication.shared.open(_:options:completionHandler:)` and report the success boolean via a `CompletableDeferred`; Desktop `Desktop.getDesktop().browse(URI(url))` for `https?`, and a `Runtime.getRuntime().exec(arrayOf("open", url))` shell-out on macOS for `obsidian://` (which `Desktop.browse` refuses).
3. **DI bindings** in the platform Koin modules (`AndroidModule.kt`, `IosModule.kt`, `DesktopModule.kt`).
4. **SummaryDetailScreen overflow menu** — add two entries:
   - `Copy as Markdown` → calls `useCase.copyAsMarkdown(summary)`, shows a Frost `Toast` ("Copied" or "Couldn't copy").
   - `Open in Obsidian` → calls `useCase.openInObsidian(summary, vault)` where `vault` reads a new `obsidianVault` field from `UserPreferences` (empty default), shows a Frost `Toast` on `false` ("Obsidian isn't installed").
5. **Settings → Sharing** — add a `BracketField` for "Obsidian vault" (default empty) and a `BracketSwitch` for "Show Obsidian export menu" (default on).

## Owner

Senior KMP/Compose Engineer (Ratatoskr Client).

## Expected artifact

- `core/common/src/androidMain/.../util/share/{AndroidClipboardWriter.kt,AndroidExternalUrlOpener.kt}`.
- `core/common/src/iosMain/.../util/share/{IosClipboardWriter.kt,IosExternalUrlOpener.kt}`.
- `core/common/src/desktopMain/.../util/share/{DesktopClipboardWriter.kt,DesktopExternalUrlOpener.kt}`.
- DI bindings in the three platform modules.
- Overflow-menu entries on `SummaryDetailScreen` + `UserPreferences.obsidianVault` + `UserPreferences.showObsidianExport`.
- Settings Sharing section updated.

## Constraints

- Clipboard write feedback via Frost Toast (use existing toast atom).
- Obsidian deep-link no-ops gracefully on missing-app — never crash.
- Default Obsidian vault stays empty so the user is not forced to configure it before first use.

## Definition of done

- Tapping "Copy as Markdown" places `SummaryMarkdownExporter.toMarkdown(summary)` on the clipboard on Android, iOS, and desktop.
- Tapping "Open in Obsidian" launches the Obsidian URL when Obsidian is installed; otherwise renders the "Obsidian isn't installed" toast.
- Both entries honor the Settings toggle (off → entries hidden).
