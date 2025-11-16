package com.po4yka.bitesizereader.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Helper class for Telegram authentication using Custom Tabs
 *
 * This class handles launching the Telegram Login Widget in a Custom Tab
 * and constructing the proper authentication URL.
 */
object TelegramAuthHelper {
    // TODO: Replace with your actual Telegram bot username
    private const val TELEGRAM_BOT_USERNAME = "your_bot_username"

    // Deep link callback URL
    private const val CALLBACK_URL = "bitesizereader://telegram-auth"

    /**
     * Launch Telegram authentication in a Custom Tab
     *
     * @param context Android context
     * @return true if launch was successful, false otherwise
     */
    fun launchTelegramAuth(context: Context): Boolean {
        return try {
            val authUrl = buildTelegramAuthUrl()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()

            customTabsIntent.launchUrl(context, Uri.parse(authUrl))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Build the Telegram Login Widget URL
     *
     * The URL follows this format:
     * https://oauth.telegram.org/auth?bot_id=BOT_ID&origin=ORIGIN&request_access=write&return_to=CALLBACK_URL
     *
     * Or using the widget:
     * https://oauth.telegram.org/embed/BOT_USERNAME?origin=ORIGIN&return_to=CALLBACK_URL
     */
    private fun buildTelegramAuthUrl(): String {
        // Using the Telegram Login Widget
        return buildString {
            append("https://oauth.telegram.org/auth")
            append("?bot_id=") // TODO: Add your bot ID
            append("&origin=") // TODO: Add your app origin
            append("&embed=1")
            append("&request_access=write")
            append("&return_to=")
            append(Uri.encode(CALLBACK_URL))
        }
    }

    /**
     * Alternative: Build URL for Telegram Web Login Widget (embed version)
     *
     * This is typically used when embedding the widget in a WebView.
     * For production, you would create an HTML page with the Telegram Login Widget script.
     */
    fun buildWebViewAuthUrl(): String {
        return buildString {
            append("https://oauth.telegram.org/embed/")
            append(TELEGRAM_BOT_USERNAME)
            append("?origin=android")
            append("&return_to=")
            append(Uri.encode(CALLBACK_URL))
        }
    }

    /**
     * Fallback: Open Telegram app directly for auth
     * This requires the Telegram app to be installed
     */
    fun launchTelegramApp(context: Context, botUsername: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tg://resolve?domain=$botUsername&start=auth")
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
