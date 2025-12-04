package com.po4yka.bitesizereader.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.po4yka.bitesizereader.data.remote.dto.AuthRequestDto
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import org.koin.android.ext.android.inject

class TelegramAuthActivity : ComponentActivity() {
    
    private val authViewModel: AuthViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "bitesizereader" && data.host == "telegram-auth") {
            // Parse parameters from the URL fragment or query
            // Telegram widget usually returns data in query params
            val id = data.getQueryParameter("id")
            val firstName = data.getQueryParameter("first_name") ?: ""
            val lastName = data.getQueryParameter("last_name")
            val username = data.getQueryParameter("username")
            val photoUrl = data.getQueryParameter("photo_url")
            val authDate = data.getQueryParameter("auth_date")?.toLongOrNull() ?: 0L
            val hash = data.getQueryParameter("hash") ?: ""

            if (id != null && hash.isNotEmpty()) {
                val authRequest = AuthRequestDto(
                    id = id,
                    firstName = firstName,
                    lastName = lastName,
                    username = username,
                    photoUrl = photoUrl,
                    authDate = authDate,
                    hash = hash
                )
                authViewModel.login(authRequest)
            }
            
            // Close this activity and return to main app
            finish()
        }
    }
}