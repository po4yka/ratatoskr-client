package com.po4yka.ratatoskr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.retainedComponent
import com.po4yka.ratatoskr.app.AppLaunchAction
import com.po4yka.ratatoskr.app.assembleAppCompositionRoot
import com.po4yka.ratatoskr.app.handleLaunchAction
import com.po4yka.ratatoskr.presentation.navigation.RootComponent
import com.po4yka.ratatoskr.util.share.ClipboardUrlParser
import org.koin.android.ext.android.getKoin

class MainActivity : ComponentActivity() {
    private lateinit var rootComponent: RootComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val compositionRoot = assembleAppCompositionRoot(getKoin())
        rootComponent = retainedComponent { compositionRoot.createRoot(it) }

        setContent {
            App(
                rootComponent = rootComponent,
                imageUrlTransformer = compositionRoot.imageUrlTransformer(),
            )
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.toLaunchAction()?.let { action ->
            rootComponent.handleLaunchAction(action)
        }
    }

    private fun Intent.toLaunchAction(): AppLaunchAction? =
        when {
            action == Intent.ACTION_SEND && type.equals(TEXT_PLAIN_MIME_TYPE, ignoreCase = true) -> {
                AppLaunchAction.SubmitUrl(prefilledUrl = ClipboardUrlParser.firstHttpUrl(sharedText()))
            }

            getStringExtra(EXTRA_SHORTCUT_ACTION) == SHORTCUT_ACTION_SUBMIT_URL ->
                AppLaunchAction.SubmitUrl(prefilledUrl = getStringExtra(EXTRA_PREFILLED_URL))

            getStringExtra(EXTRA_SHORTCUT_ACTION) == SHORTCUT_ACTION_SEARCH -> AppLaunchAction.Search

            else -> null
        }

    private fun Intent.sharedText(): String =
        listOfNotNull(
            getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString(),
            dataString,
        ).firstOrNull { it.isNotBlank() }.orEmpty()

    companion object {
        const val EXTRA_SHORTCUT_ACTION = "action"
        const val SHORTCUT_ACTION_SUBMIT_URL = "submit_url"
        const val EXTRA_PREFILLED_URL = "prefilled_url"
        private const val SHORTCUT_ACTION_SEARCH = "search"
        private const val TEXT_PLAIN_MIME_TYPE = "text/plain"
    }
}
