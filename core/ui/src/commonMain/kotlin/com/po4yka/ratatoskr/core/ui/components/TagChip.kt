package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gabrieldrn.carbon.tag.ReadOnlyTag
import com.gabrieldrn.carbon.tag.TagSize
import com.gabrieldrn.carbon.tag.TagType

/**
 * Tag component for displaying topic tags using Carbon Design System
 */
@Composable
fun TagChip(
    tag: String,
    modifier: Modifier = Modifier,
) {
    ReadOnlyTag(
        text = tag,
        modifier = modifier,
        type = TagType.Gray,
        size = TagSize.Small,
    )
}
