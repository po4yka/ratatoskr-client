package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostIcon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.po4yka.ratatoskr.core.ui.components.foundation.FrostText
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.search_clear
import ratatoskr.core.ui.generated.resources.search_placeholder
import ratatoskr.core.ui.generated.resources.summary_list_close_search
import ratatoskr.core.ui.generated.resources.summary_list_search

@Composable
fun AppSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    searchIconContentDescription: String? = null,
    onClearQuery: (() -> Unit)? = null,
    @Suppress("UNUSED_PARAMETER") backgroundColor: androidx.compose.ui.graphics.Color = AppTheme.frostColors.page,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val clearQuery = onClearQuery ?: { onQueryChange("") }
    val ink = AppTheme.frostColors.ink

    Row(
        modifier =
            modifier
                .background(AppTheme.frostColors.page, RectangleShape)
                .border(AppTheme.border.hairline, ink.copy(alpha = AppTheme.border.separatorAlpha), RectangleShape)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FrostIcon(
            imageVector = AppIcons.Search,
            contentDescription = searchIconContentDescription,
            tint = ink.copy(alpha = AppTheme.alpha.secondary),
            modifier = Modifier.size(IconSizes.sm),
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.sm),
            singleLine = true,
            textStyle = AppTheme.frostType.monoBody.copy(color = ink),
            cursorBrush = SolidColor(ink),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    FrostText(
                        text = placeholder,
                        style = AppTheme.frostType.monoBody,
                        color = ink.copy(alpha = AppTheme.alpha.inactive),
                    )
                }
                innerTextField()
            },
        )

        if (query.isNotEmpty()) {
            AppIconButton(
                imageVector = AppIcons.Close,
                contentDescription = stringResource(Res.string.search_clear),
                onClick = clearQuery,
                buttonSize = Dimensions.compactIconButtonSize,
                iconSize = IconSizes.xs,
            )
        }

        trailingContent()
    }
}

@Suppress("FunctionNaming")
@Composable
fun SummarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(AppTheme.frostColors.page)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppSearchField(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = stringResource(Res.string.search_placeholder),
            modifier = Modifier.weight(1f),
            searchIconContentDescription = stringResource(Res.string.summary_list_search),
        )

        Spacer(modifier = Modifier.width(Spacing.xs))

        AppIconButton(
            imageVector = AppIcons.Close,
            contentDescription = stringResource(Res.string.summary_list_close_search),
            onClick = onClose,
            buttonSize = Dimensions.compactIconButtonSize,
            iconSize = IconSizes.sm,
        )
    }
}
