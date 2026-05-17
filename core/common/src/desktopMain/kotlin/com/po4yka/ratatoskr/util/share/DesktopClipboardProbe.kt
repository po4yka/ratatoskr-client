package com.po4yka.ratatoskr.util.share

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

/**
 * Desktop-backed [ClipboardProbe] using AWT's system clipboard. Used only by the desktop
 * development host; production hosts are Android and iOS.
 */
class DesktopClipboardProbe : ClipboardProbe {
    override suspend fun hasUrl(): Boolean = readClipboardUrl() != null

    override suspend fun readUrl(): String? = readClipboardUrl()

    private fun readClipboardUrl(): String? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard ?: return null
        val text =
            try {
                clipboard.getData(DataFlavor.stringFlavor) as? String
            } catch (_: IllegalStateException) {
                null
            } catch (_: java.awt.datatransfer.UnsupportedFlavorException) {
                null
            } catch (_: java.io.IOException) {
                null
            }
        return text?.let { ClipboardUrlParser.firstHttpUrl(it) }
    }
}
