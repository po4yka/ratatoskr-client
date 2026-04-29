package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.AppTheme

/**
 * Tag component for displaying topic tags. Hand-rolled to mirror Carbon's previous gray/small
 * `ReadOnlyTag` styling without taking on Material `AssistChip(enabled=false)` (which renders
 * dimmer than Carbon's gray).
 */
@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(AppTheme.colors.layer02)
                .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = tag,
            style = AppTheme.type.label01,
            color = AppTheme.colors.textSecondary,
        )
    }
}
