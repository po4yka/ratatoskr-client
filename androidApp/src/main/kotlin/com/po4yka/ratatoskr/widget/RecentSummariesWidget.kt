package com.po4yka.ratatoskr.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.po4yka.ratatoskr.domain.usecase.GetSummariesUseCase
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecentSummariesWidget : GlanceAppWidget(), KoinComponent {
    private val getSummariesUseCase: GetSummariesUseCase by inject()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        val summaries = getSummariesUseCase(page = 1, pageSize = 5).firstOrNull().orEmpty()

        provideContent {
            RecentSummariesContent(summaries = summaries)
        }
    }
}
