package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import compose.icons.FeatherIcons
import compose.icons.TablerIcons
import compose.icons.feathericons.Bookmark
import compose.icons.feathericons.Folder
import compose.icons.feathericons.Home
import compose.icons.feathericons.Inbox
import compose.icons.feathericons.Map
import compose.icons.feathericons.Trash2
import compose.icons.tablericons.Bulb
import compose.icons.tablericons.Diamond
import compose.icons.tablericons.Droplet
import compose.icons.tablericons.DeviceGamepad
import compose.icons.tablericons.Palette
import compose.icons.tablericons.ToolsKitchen2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.domain.model.Collection

/**
 * Collection item component using Carbon Design System
 */
@Suppress("FunctionNaming")
@Composable
fun CollectionItem(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Icon
        Icon(
            imageVector = getIconForName(collection.iconName),
            contentDescription = null,
            tint = Carbon.theme.iconPrimary,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name
        Text(
            text = collection.name,
            style = Carbon.typography.bodyCompact01,
            color = Carbon.theme.textPrimary,
            modifier = Modifier.weight(1f),
        )

        // Count (if > 0)
        if (collection.count > 0) {
            Text(
                text = collection.count.toString(),
                style = Carbon.typography.bodyCompact01,
                color = Carbon.theme.textSecondary,
            )
        }
    }
}

private fun getIconForName(name: String?): ImageVector {
    return when (name) {
        "inbox" -> FeatherIcons.Inbox
        "bookmark" -> FeatherIcons.Bookmark
        "palette" -> TablerIcons.Palette
        "lightbulb" -> TablerIcons.Bulb
        "map" -> FeatherIcons.Map
        "restaurant" -> TablerIcons.ToolsKitchen2
        "sports_esports" -> TablerIcons.DeviceGamepad
        "spa" -> TablerIcons.Droplet
        "diamond" -> TablerIcons.Diamond
        "architecture" -> FeatherIcons.Home
        "delete" -> FeatherIcons.Trash2
        else -> FeatherIcons.Folder
    }
}
