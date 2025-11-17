package com.po4yka.bitesizereader.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.po4yka.bitesizereader.domain.model.SearchFilters
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = KotlinLogging.logger {}

/**
 * Glance widget showing recent summaries.
 *
 * This widget displays up to 5 most recent summaries from the user's library,
 * allowing quick access to their latest content without opening the app.
 *
 * Features:
 * - Shows title, TLDR, and reading time
 * - Click to open summary in app
 * - Auto-refreshes periodically
 * - Handles empty state gracefully
 */
class RecentSummariesWidget : GlanceAppWidget(), KoinComponent {

    private val getSummariesUseCase: GetSummariesUseCase by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        logger.debug { "Providing widget content for id: $id" }

        // Fetch recent summaries (limit to 5 for widget)
        val summaries = try {
            getSummariesUseCase(
                limit = 5,
                offset = 0,
                filters = SearchFilters()
            ).first()
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch summaries for widget" }
            emptyList()
        }

        provideContent {
            GlanceTheme {
                RecentSummariesContent(summaries = summaries)
            }
        }
    }
}
