package com.po4yka.bitesizereader.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.po4yka.bitesizereader.util.config.AppConfig

object TelegramAuthHelper {
    fun launchTelegramAuth(context: Context) {
        val botUsername = AppConfig.Telegram.botUsername
        val origin = AppConfig.Telegram.callbackUrl

        // Construct Telegram Login Widget URL
        // Since we don't have a web backend hosting the widget for this native app flow (unless we use a dummy page),
        // we can use a direct deep link to the bot if supported, or better, open a Custom Tab to a hosted login page.
        //
        // For a truly serverless/native feel without a web server, we often use the Telegram Login Widget
        // hosted on a static page (e.g. GitHub Pages) that redirects back to our app scheme.
        //
        // For this implementation, let's assume we have a hosted page that invokes the widget.
        // http://10.0.2.2:8000/login is a placeholder for a local server or similar.
        // Or we can use the bot direct link? No, bot direct link opens chat.

        // Strategy: Open a Custom Tab to a URL that hosts the Telegram Login Widget.
        // The widget, upon success, will redirect to `bitesizereader://telegram-auth?hash=...`.

        // Placeholder URL - user must configure this in real app
        val loginPageUrl = "${AppConfig.Api.baseUrl}/v1/auth/login-widget?bot=$botUsername&origin=$origin"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(loginPageUrl))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}
