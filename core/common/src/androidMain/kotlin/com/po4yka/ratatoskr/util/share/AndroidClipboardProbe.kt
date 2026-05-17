package com.po4yka.ratatoskr.util.share

import android.content.ClipboardManager
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android-backed [ClipboardProbe] that reads through [ClipboardManager]. Android does not
 * require a two-stage prompt — the same primary clip read backs both [hasUrl] and [readUrl].
 *
 * Android 12+ shows a one-time toast when an app reads the clipboard while in the foreground,
 * but does not block the read. Callers should still gate [readUrl] behind explicit user intent
 * to keep the UX consistent with the iOS path.
 */
class AndroidClipboardProbe(
    private val context: Context,
) : ClipboardProbe {
    private val clipboard: ClipboardManager
        get() = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override suspend fun hasUrl(): Boolean = readClipboardUrl() != null

    override suspend fun readUrl(): String? = readClipboardUrl()

    private suspend fun readClipboardUrl(): String? =
        withContext(Dispatchers.Main) {
            val text =
                clipboard.primaryClip
                    ?.takeIf { it.itemCount > 0 }
                    ?.getItemAt(0)
                    ?.coerceToText(context)
                    ?.toString()
            text?.let { ClipboardUrlParser.firstHttpUrl(it) }
        }
}
