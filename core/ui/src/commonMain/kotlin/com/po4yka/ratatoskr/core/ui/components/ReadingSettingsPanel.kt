package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostDivider
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketSlider
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import com.po4yka.ratatoskr.domain.model.ReadingPreferences
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.common_percent
import ratatoskr.core.ui.generated.resources.reading_settings_font_size
import ratatoskr.core.ui.generated.resources.reading_settings_line_spacing
import ratatoskr.core.ui.generated.resources.summary_detail_reading_settings

@Suppress("FunctionNaming")
@Composable
fun ReadingSettingsPanel(
    visible: Boolean,
    preferences: ReadingPreferences,
    onFontSizeScaleChange: (Float) -> Unit,
    onLineSpacingScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink

    AnimatedVisibility(
        visible = visible,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            FrostText(
                text = stringResource(Res.string.summary_detail_reading_settings),
                style = AppTheme.frostType.monoEmph,
                color = ink,
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Font size slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FrostText(
                    text = stringResource(Res.string.reading_settings_font_size),
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.weight(0.3f),
                )
                BracketSlider(
                    value = preferences.fontSizeScale,
                    onValueChange = onFontSizeScaleChange,
                    range = ReadingPreferences.MIN_FONT_SCALE..ReadingPreferences.MAX_FONT_SCALE,
                    steps = 7,
                    modifier = Modifier.weight(0.55f),
                )
                FrostText(
                    text = stringResource(Res.string.common_percent, (preferences.fontSizeScale * 100).toInt()),
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.weight(0.15f),
                )
            }

            // Line spacing slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FrostText(
                    text = stringResource(Res.string.reading_settings_line_spacing),
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.weight(0.3f),
                )
                BracketSlider(
                    value = preferences.lineSpacingScale,
                    onValueChange = onLineSpacingScaleChange,
                    range = ReadingPreferences.MIN_LINE_SPACING_SCALE..ReadingPreferences.MAX_LINE_SPACING_SCALE,
                    steps = 9,
                    modifier = Modifier.weight(0.55f),
                )
                FrostText(
                    text = stringResource(Res.string.common_percent, (preferences.lineSpacingScale * 100).toInt()),
                    style = AppTheme.frostType.monoSm,
                    color = ink.copy(alpha = AppTheme.alpha.secondary),
                    modifier = Modifier.weight(0.15f),
                )
            }

            FrostDivider(alpha = AppTheme.border.separatorAlpha)
        }
    }
}
