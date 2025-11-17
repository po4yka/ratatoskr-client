package com.po4yka.bitesizereader

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import com.po4yka.bitesizereader.ui.theme.BiteSizeReaderTheme

/**
 * Main activity for Android app
 * Handles app launch, share intents, and widget clicks
 */
class MainActivity : ComponentActivity() {
    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Create root navigation component
        rootComponent =
            RootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            BiteSizeReaderTheme {
                App(rootComponent = rootComponent)
            }
        }

        // Handle incoming intents
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    /**
     * Handle all incoming intents (share, widget clicks, etc.)
     */
    private fun handleIntent(intent: Intent?) {
        intent ?: return

        // Handle widget click - navigate to specific summary
        intent.getStringExtra("summaryId")?.toIntOrNull()?.let { summaryId ->
            rootComponent.navigateToSummaryDetail(id = summaryId)
            return
        }

        // Handle share intent from other apps
        handleShareIntent(intent)
    }

    /**
     * Handle shared content from other apps
     */
    private fun handleShareIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                // Extract URL from shared text (could be URL or text containing URL)
                val url = extractUrl(sharedText)
                if (url != null) {
                    // Navigate to Submit URL screen with pre-filled URL
                    rootComponent.navigateToSubmitUrl(prefilledUrl = url)
                }
            }
        }
    }

    /**
     * Extract URL from shared text
     * Handles cases where text is a URL or contains a URL
     */
    private fun extractUrl(text: String): String? {
        val trimmed = text.trim()

        // Check if the entire text is a URL
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed.split("\\s".toRegex()).firstOrNull()
        }

        // Try to find URL in text using regex
        val urlPattern = Regex("https?://[^\\s]+")
        return urlPattern.find(trimmed)?.value
    }
}
