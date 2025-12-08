package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.po4yka.bitesizereader.domain.model.Collection

@Composable
fun CollectionItem(
    collection: Collection,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = getIconForName(collection.iconName),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name
        Text(
            text = collection.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Count (if > 0)
        if (collection.count > 0) {
            Text(
                text = collection.count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        "architecture" -> Icons.Default.Home // Fallback for 'architecture' if not available
        "delete" -> Icons.Default.Delete
        else -> Icons.Default.Folder
    }
}
