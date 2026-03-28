package com.po4yka.bitesizereader.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.ReadingPreferences
import com.po4yka.bitesizereader.ui.theme.Spacing
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.reading_settings_font_size
import bitesizereader.composeapp.generated.resources.reading_settings_line_spacing
import bitesizereader.composeapp.generated.resources.summary_detail_reading_settings
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun ReadingSettingsPanel(
    visible: Boolean,
    preferences: ReadingPreferences,
    onFontSizeScaleChange: (Float) -> Unit,
    onLineSpacingScaleChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                    .background(Carbon.theme.layer01)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            Text(
                text = stringResource(Res.string.summary_detail_reading_settings),
                style = Carbon.typography.headingCompact01,
                color = Carbon.theme.textPrimary,
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Font size slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.reading_settings_font_size),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    modifier = Modifier.weight(0.3f),
                )
                CarbonSlider(
                    value = preferences.fontSizeScale,
                    onValueChange = onFontSizeScaleChange,
                    valueRange = ReadingPreferences.MIN_FONT_SCALE..ReadingPreferences.MAX_FONT_SCALE,
                    steps = 7,
                    modifier = Modifier.weight(0.55f),
                )
                Text(
                    text = "${(preferences.fontSizeScale * 100).toInt()}%",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    modifier = Modifier.weight(0.15f),
                )
            }

            // Line spacing slider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.reading_settings_line_spacing),
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    modifier = Modifier.weight(0.3f),
                )
                CarbonSlider(
                    value = preferences.lineSpacingScale,
                    onValueChange = onLineSpacingScaleChange,
                    valueRange = ReadingPreferences.MIN_LINE_SPACING_SCALE..ReadingPreferences.MAX_LINE_SPACING_SCALE,
                    steps = 9,
                    modifier = Modifier.weight(0.55f),
                )
                Text(
                    text = "${(preferences.lineSpacingScale * 100).toInt()}%",
                    style = Carbon.typography.label01,
                    color = Carbon.theme.textSecondary,
                    modifier = Modifier.weight(0.15f),
                )
            }

            HorizontalDivider(color = Carbon.theme.borderSubtle00)
        }
    }
}
