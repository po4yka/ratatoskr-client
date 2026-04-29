package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import ratatoskr.core.ui.generated.resources.Res
import ratatoskr.core.ui.generated.resources.search_clear
import ratatoskr.core.ui.generated.resources.search_placeholder
import ratatoskr.core.ui.generated.resources.summary_list_close_search
import ratatoskr.core.ui.generated.resources.summary_list_search
import com.po4yka.ratatoskr.core.ui.theme.AppTheme
import com.po4yka.ratatoskr.core.ui.icons.AppIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    searchIconContentDescription: String? = null,
    onClearQuery: (() -> Unit)? = null,
    backgroundColor: androidx.compose.ui.graphics.Color = AppTheme.colors.layer02,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val clearQuery = onClearQuery ?: { onQueryChange("") }

    Row(
        modifier =
            modifier
                .background(backgroundColor)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = AppIcons.Search,
            contentDescription = searchIconContentDescription,
            tint = AppTheme.colors.iconSecondary,
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
            textStyle =
                AppTheme.type.bodyCompact01.copy(
                    color = AppTheme.colors.textPrimary,
                ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions =
                KeyboardActions(
                    onSearch = { focusManager.clearFocus() },
                ),
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = AppTheme.type.bodyCompact01,
                        color = AppTheme.colors.textPlaceholder,
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
                .background(AppTheme.colors.layer01)
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
