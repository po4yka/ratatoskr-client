package com.po4yka.bitesizereader.widget

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.po4yka.bitesizereader.MainActivity
import com.po4yka.bitesizereader.domain.usecase.GetSummariesUseCase
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecentSummariesWidget : GlanceAppWidget(), KoinComponent {
    private val getSummariesUseCase: GetSummariesUseCase by inject()

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId,
    ) {
        // Fetch data
        // Note: In a real app, you might want to use a StateDefinition or store data in prefs
        // to avoid network/db calls directly in provideGlance context if it blocks.
        // For simplicity, we fetch once here.
        val summaries = getSummariesUseCase(1, 5).firstOrNull() ?: emptyList()

        provideContent {
            GlanceTheme {
                Column(
                    modifier =
                        GlanceModifier
                            .fillMaxSize()
                            .background(GlanceTheme.colors.background)
                            .padding(8.dp),
                ) {
                    Text(
                        text = "Recent Summaries",
                        style =
                            TextStyle(
                                color = GlanceTheme.colors.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        modifier = GlanceModifier.padding(bottom = 8.dp),
                    )

                    LazyColumn {
                        items(summaries) { summary ->
                            Column(
                                modifier =
                                    GlanceModifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(GlanceTheme.colors.surface)
                                        .clickable(
                                            actionStartActivity<MainActivity>(),
                                            // Pass intent extras if needed to open specific summary
                                            // For now just opens app
                                        ),
                            ) {
                                Text(
                                    text = summary.title,
                                    style =
                                        TextStyle(
                                            color = GlanceTheme.colors.onSurface,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                        ),
                                    modifier = GlanceModifier.padding(8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
