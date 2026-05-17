package com.po4yka.ratatoskr.util.share

/**
 * Platform-agnostic launcher for non-`https?` deep-link URLs that hand off to
 * another app. The repo's primary use is the `obsidian://new?…` URL composed
 * by `ObsidianDeepLink`, but the contract is generic — any platform-registered
 * scheme is fair game.
 *
 * Returning [Boolean] rather than throwing avoids leaking
 * `ActivityNotFoundException` / `NSException` types into shared code. Callers
 * can render a Frost toast on `false` to tell the user "Obsidian isn't
 * installed" without inspecting platform-specific exceptions.
 *
 * Implementations:
 *  - Android: `Intent(ACTION_VIEW, Uri.parse(url))` — catches
 *    `ActivityNotFoundException` and returns false.
 *  - iOS: `UIApplication.shared.open(NSURL(string: url)!) { success in }` —
 *    the boolean return reflects whether iOS can route the URL scheme.
 *  - Desktop: `Desktop.getDesktop().browse(URI(url))` for `https?`, or a
 *    `Runtime.getRuntime().exec("open …")` shell-out for `obsidian://` on
 *    macOS.
 */
interface ExternalUrlOpener {
    /**
     * Asks the OS to open [url] in whichever app has registered the scheme.
     * Returns `true` if the OS dispatched the URL to a handler, `false` if no
     * handler is installed or the platform otherwise refused.
     */
    fun open(url: String): Boolean
}
