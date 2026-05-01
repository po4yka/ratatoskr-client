package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard
import com.po4yka.ratatoskr.core.ui.components.frost.SectionHeading
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Summary
import com.po4yka.ratatoskr.util.extractDomain
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.insights_fallback_source
import ratatoskr.core.ui.generated.resources.insights_title

@Suppress("FunctionNaming")
@Composable
fun InsightsSection(
    insights: List<Summary>,
    onSummaryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (insights.isEmpty()) return

    val ink = AppTheme.frostColors.ink

    BrutalistCard(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.spacing.line, vertical = AppTheme.spacing.gapInline),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppTheme.spacing.line),
        ) {
            SectionHeading(
                text = stringResource(Res.string.insights_title),
                modifier =
                    Modifier
                        .padding(horizontal = AppTheme.spacing.line)
                        .padding(bottom = AppTheme.spacing.cell),
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = AppTheme.spacing.line),
                horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.cell),
            ) {
                items(
                    items = insights,
                    key = { it.id },
                ) { summary ->
                    InsightCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) },
                    )
                }
            }
        }
    }
}

@Suppress("FunctionNaming")
@Composable
private fun InsightCard(
    summary: Summary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    BrutalistCard(
        modifier =
            modifier
                .width(200.dp)
                .clickable(role = Role.Button, onClick = onClick),
    ) {
        Column {
            if (summary.imageUrl != null) {
                ProxiedImage(
                    imageUrl = summary.imageUrl!!,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                )
            }

            Column(modifier = Modifier.padding(AppTheme.spacing.cell)) {
                FrostText(
                    text = summary.title,
                    style = AppTheme.frostType.monoEmph,
                    color = ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(AppTheme.spacing.gapInline))

                FrostText(
                    text = extractDomain(summary.sourceUrl) ?: stringResource(Res.string.insights_fallback_source),
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
