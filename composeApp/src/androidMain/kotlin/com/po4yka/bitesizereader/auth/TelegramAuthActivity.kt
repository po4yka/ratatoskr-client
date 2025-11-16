package com.po4yka.bitesizereader.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.po4yka.bitesizereader.presentation.viewmodel.LoginViewModel
import org.koin.android.ext.android.inject

/**
 * Activity to handle Telegram authentication callback
 *
 * This activity receives the deep link after Telegram authentication
 * and processes the auth data.
 */
class TelegramAuthActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the deep link
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri? = intent?.data

        if (data != null && data.scheme == "bitesizereader" && data.host == "telegram-auth") {
            // Parse Telegram auth data from URL
            val telegramUserId = data.getQueryParameter("id")?.toLongOrNull()
            val authHash = data.getQueryParameter("hash")
            val authDate = data.getQueryParameter("auth_date")?.toLongOrNull()
            val username = data.getQueryParameter("username")
            val firstName = data.getQueryParameter("first_name")
            val lastName = data.getQueryParameter("last_name")
            val photoUrl = data.getQueryParameter("photo_url")

            Log.d("TelegramAuth", "Received auth callback: userId=$telegramUserId")

            if (telegramUserId != null && authHash != null && authDate != null) {
                // Call the ViewModel to process authentication
                // Platform identifier
                loginViewModel.loginWithTelegram(
                    telegramUserId = telegramUserId,
                    authHash = authHash,
                    authDate = authDate,
                    username = username,
                    firstName = firstName,
                    lastName = lastName,
                    photoUrl = photoUrl,
                    clientId = "android",
                )

                // Close this activity and return to main app
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Log.e("TelegramAuth", "Invalid auth data received")
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        } else {
            Log.e("TelegramAuth", "Invalid deep link: $data")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
}
