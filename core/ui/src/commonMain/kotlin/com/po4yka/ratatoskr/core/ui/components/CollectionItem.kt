package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.domain.model.Collection

/** Collection item for list rows. */
@Suppress("FunctionNaming", "UnstableCollections") // Collection is a domain model, not kotlin.collections
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
        FrostIcon(
            imageVector = getIconForName(collection.iconName),
            contentDescription = collection.name,
            tint = AppTheme.frostColors.ink,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Name
        FrostText(
            text = collection.name,
            style = AppTheme.frostType.monoBody,
            color = AppTheme.frostColors.ink,
            modifier = Modifier.weight(1f),
        )

        // Count (if > 0)
        if (collection.count > 0) {
            FrostText(
                text = collection.count.toString(),
                style = AppTheme.frostType.monoBody,
                color = AppTheme.frostColors.ink.copy(alpha = AppTheme.alpha.secondary),
            )
        }
    }
}

private fun getIconForName(name: String?): ImageVector {
    return when (name) {
        "inbox" -> AppIcons.Email
        "bookmark" -> AppIcons.Bookmark
        "palette" -> AppIcons.ColorPalette
        "lightbulb" -> AppIcons.Idea
        "map" -> AppIcons.Map
        "restaurant" -> AppIcons.Restaurant
        "sports_esports" -> AppIcons.GameWireless
        "spa" -> AppIcons.RainDrop
        "diamond" -> AppIcons.Gem
        "architecture" -> AppIcons.Home
        "delete" -> AppIcons.TrashCan
        else -> AppIcons.Folder
    }
}
