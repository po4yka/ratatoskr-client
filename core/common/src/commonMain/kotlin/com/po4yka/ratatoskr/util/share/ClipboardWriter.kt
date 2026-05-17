package com.po4yka.ratatoskr.util.share

/**
 * Platform-agnostic clipboard write side of the [ClipboardProbe] pair.
 *
 * [ClipboardProbe] handles the read side and is intentionally split because
 * iOS surfaces a privacy prompt on every read. The write side has no such
 * concern — every platform allows the foreground app to set the system
 * clipboard without user interaction.
 *
 * Implementations:
 *  - Android: `ClipboardManager.setPrimaryClip(ClipData.newPlainText(...))`
 *  - iOS: `UIPasteboard.general.string = text`
 *  - Desktop: `Toolkit.getDefaultToolkit().systemClipboard.setContents(...)`
 *
 * Returning [Boolean] (not throwing) lets the caller render a Frost toast with
 * the right wording — "Copied" vs "Couldn't copy" — without having to wrap
 * every call site in a `try`/`catch`.
 */
interface ClipboardWriter {
    /**
     * Places [text] on the system clipboard. Returns `true` on success, `false`
     * if the platform refused (e.g., desktop AWT `IllegalStateException` from a
     * locked clipboard owner). On Android and iOS this is effectively never
     * false in practice.
     */
    fun setText(text: String): Boolean
}
