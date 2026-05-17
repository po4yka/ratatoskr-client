package com.po4yka.ratatoskr.util.share

/**
 * Two-stage clipboard probe used by the URL-detector banner.
 *
 * The iOS pasteboard read prompts the user with "X pasted from Y" any time
 * the app reads `UIPasteboard.string` in the foreground. Reading
 * unconditionally on resume to decide whether to show a "Submit URL" banner
 * would surface that prompt every launch — exactly the opposite of the
 * intended affordance.
 *
 * Instead, [hasUrl] checks whether the clipboard *contains* an http(s) URL
 * via `UIPasteboard.detectPatterns([.probableWebURL])` — a privacy-preserving
 * API added in iOS 14 that returns a boolean signal without exposing the
 * value. The banner is rendered based on that signal. [readUrl] returns the
 * actual URL and may trigger the platform paste prompt; it must only be
 * called after the user explicitly accepts the banner.
 *
 * Android does not have this distinction — both calls read `ClipboardManager`
 * directly without any prompt, since Android exposes clipboard contents in
 * the foreground without user friction (subject to its own focus rules).
 */
interface ClipboardProbe {
    /**
     * Returns true if the clipboard likely contains an http(s) URL. Safe to
     * call on every app resume — does not trigger the iOS paste prompt.
     */
    suspend fun hasUrl(): Boolean

    /**
     * Returns the clipboard URL when present, or null. On iOS this triggers
     * the system "X pasted from Y" prompt — only call after explicit user
     * intent (banner tap).
     */
    suspend fun readUrl(): String?
}
