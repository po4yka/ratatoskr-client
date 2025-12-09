package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
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
        "inbox" -> Icons.Default.Inbox
        "bookmark" -> Icons.Default.Bookmark
        "palette" -> Icons.Default.Palette
        "lightbulb" -> Icons.Default.Lightbulb
        "map" -> Icons.Default.Map
        "restaurant" -> Icons.Default.Restaurant
        "sports_esports" -> Icons.Default.SportsEsports
        "spa" -> Icons.Default.Spa
        "diamond" -> Icons.Default.Diamond
        "architecture" -> Icons.Default.Home
        "delete" -> Icons.Default.Delete
        else -> Icons.Default.Folder
    }
}
