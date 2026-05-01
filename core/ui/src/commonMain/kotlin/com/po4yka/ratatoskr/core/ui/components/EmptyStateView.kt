package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.components.frost.BracketButton
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.IconSizes

/**
 * Pre-defined empty state types for common scenarios.
 */
enum class EmptyStateType {
    NO_ARTICLES,
    NO_SEARCH_RESULTS,
    NO_UNREAD_ARTICLES,
    NO_READ_ARTICLES,
    NO_ARCHIVED_ARTICLES,
    ERROR,
    SEARCH_PROMPT,
    COLLECTION_EMPTY,
}

/**
 * Contextual empty state view that displays pre-defined content based on the type.
 */
@Suppress("FunctionNaming")
@Composable
fun ContextualEmptyState(
    type: EmptyStateType,
    searchQuery: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val config =
        when (type) {
            EmptyStateType.NO_ARTICLES ->
                EmptyStateConfig(
                    title = "No articles yet",
                    message = "Submit a URL to generate your first summary",
                    icon = AppIcons.Document,
                    actionText = null,
                )
            EmptyStateType.NO_SEARCH_RESULTS ->
                EmptyStateConfig(
                    title = "No results found",
                    message =
                        if (searchQuery.isNullOrBlank()) {
                            "Try adjusting your search query"
                        } else {
                            "No articles match \"$searchQuery\""
                        },
                    icon = AppIcons.Search,
                    actionText = "Clear search",
                )
            EmptyStateType.NO_UNREAD_ARTICLES ->
                EmptyStateConfig(
                    title = "All caught up!",
                    message = "You've read all your articles",
                    icon = AppIcons.CheckmarkFilled,
                    actionText = "Show all articles",
                )
            EmptyStateType.NO_READ_ARTICLES ->
                EmptyStateConfig(
                    title = "No read articles",
                    message = "Articles you've read will appear here",
                    icon = AppIcons.Document,
                    actionText = "Show all articles",
                )
            EmptyStateType.NO_ARCHIVED_ARTICLES ->
                EmptyStateConfig(
                    title = "No archived articles",
                    message = "Archived articles will appear here",
                    icon = AppIcons.Archive,
                    actionText = "Show all articles",
                )
            EmptyStateType.ERROR ->
                EmptyStateConfig(
                    title = "Something went wrong",
                    message = "Unable to load articles. Please try again.",
                    icon = AppIcons.WarningAlt,
                    actionText = "Retry",
                )
            EmptyStateType.SEARCH_PROMPT ->
                EmptyStateConfig(
                    title = "Search articles",
                    message = "Enter a search term to find articles",
                    icon = AppIcons.Search,
                    actionText = null,
                )
            EmptyStateType.COLLECTION_EMPTY ->
                EmptyStateConfig(
                    title = "Collection is empty",
                    message = "Add articles to this collection",
                    icon = AppIcons.Folder,
                    actionText = null,
                )
        }

    EmptyStateView(
        title = config.title,
        message = config.message,
        icon = config.icon,
        actionText = config.actionText,
        onAction = if (config.actionText != null) onAction else null,
        modifier = modifier,
    )
}

private data class EmptyStateConfig(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val actionText: String?,
)

/** Reusable empty-state view. */
@Suppress("FunctionNaming", "LongParameterList") // Composable naming convention; UI component with optional params
@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector = AppIcons.Document,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val ink = AppTheme.frostColors.ink
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.spacing.gapSection, vertical = AppTheme.spacing.padPage),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FrostIcon(
            imageVector = icon,
            contentDescription = null,
            tint = ink.copy(alpha = AppTheme.alpha.secondary),
            modifier = Modifier.size(IconSizes.xl),
        )

        Spacer(modifier = Modifier.height(24.dp))

        FrostText(
            text = title,
            style = AppTheme.frostType.monoEmph,
            color = ink,
            modifier =
                Modifier.then(
                    Modifier.padding(horizontal = AppTheme.spacing.line),
                ),
        )

        Spacer(modifier = Modifier.height(AppTheme.spacing.cell))

        FrostText(
            text = message,
            style = AppTheme.frostType.monoBody,
            color = ink.copy(alpha = AppTheme.alpha.secondary),
        )

        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(AppTheme.spacing.padPage))
            BracketButton(label = actionText, onClick = onAction)
        }
    }
}
