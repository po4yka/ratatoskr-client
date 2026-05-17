package com.po4yka.ratatoskr.tile

import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.po4yka.ratatoskr.MainActivity
import com.po4yka.ratatoskr.util.share.ClipboardUrlParser

/**
 * Quick Settings tile that submits the current clipboard URL to the
 * summarisation flow in two taps from anywhere on the device.
 *
 * Tile state mirrors clipboard contents:
 * - Active when the clipboard holds an http(s) URL.
 * - Inactive when the clipboard is empty or non-URL.
 *
 * On click, launches [MainActivity] with the existing `submit_url`
 * shortcut action so the standard submit flow takes over — no
 * background networking happens in the tile process.
 */
class SubmitUrlTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        refreshTileState()
    }

    override fun onClick() {
        super.onClick()
        val clipboardUrl = readClipboardUrl()
        val launchIntent =
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(MainActivity.EXTRA_SHORTCUT_ACTION, MainActivity.SHORTCUT_ACTION_SUBMIT_URL)
                if (clipboardUrl != null) {
                    putExtra(MainActivity.EXTRA_PREFILLED_URL, clipboardUrl)
                }
            }
        startMainActivity(launchIntent)
    }

    private fun refreshTileState() {
        val tile: Tile = qsTile ?: return
        val url = readClipboardUrl()
        tile.state = if (url != null) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.updateTile()
    }

    private fun readClipboardUrl(): String? {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val firstItem = clipboard?.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)
        val text = firstItem?.coerceToText(this)?.toString().orEmpty()
        return ClipboardUrlParser.firstHttpUrl(text)
    }

    /**
     * Android 14 (API 34) deprecates the no-arg
     * `startActivityAndCollapse(Intent)` in favour of the
     * `PendingIntent` overload. Use the new API where available so the
     * tile remains functional on the locked screen.
     */
    private fun startMainActivity(intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pending =
                PendingIntent.getActivity(
                    this,
                    PENDING_INTENT_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                )
            startActivityAndCollapse(pending)
        } else {
            @Suppress("DEPRECATION")
            startActivityAndCollapse(intent)
        }
    }

    private companion object {
        private const val PENDING_INTENT_REQUEST_CODE = 1001
    }
}
