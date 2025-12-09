package com.po4yka.bitesizereader.ui.components

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
    onClick: (() -> Unit)? = null,
) {
    // Carbon ReadOnlyTag is used for display-only tags
    // For interactive tags, we use the same component since Carbon doesn't have
    // a separate interactive tag in the current version
    ReadOnlyTag(
        text = tag,
        modifier = modifier,
        type = TagType.Gray,
        size = TagSize.Small,
    )
}

/**
 * Selectable tag chip for filters using Carbon Design System
 */
@Composable
fun SelectableTagChip(
    tag: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Use different tag types to indicate selection state
    ReadOnlyTag(
        text = tag,
        modifier = modifier,
        type = if (selected) TagType.Blue else TagType.Gray,
        size = TagSize.Small,
    )
}
