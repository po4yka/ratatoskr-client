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
import com.gabrieldrn.carbon.Carbon
import com.po4yka.ratatoskr.core.ui.icons.CarbonIcons
import com.po4yka.ratatoskr.core.ui.theme.Dimensions
import com.po4yka.ratatoskr.core.ui.theme.IconSizes
import com.po4yka.ratatoskr.core.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun CarbonSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    searchIconContentDescription: String? = null,
    onClearQuery: (() -> Unit)? = null,
    backgroundColor: androidx.compose.ui.graphics.Color = Carbon.theme.layer02,
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
            imageVector = CarbonIcons.Search,
            contentDescription = searchIconContentDescription,
            tint = Carbon.theme.iconSecondary,
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
                Carbon.typography.bodyCompact01.copy(
                    color = Carbon.theme.textPrimary,
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
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textPlaceholder,
                    )
                }
                innerTextField()
            },
        )

        if (query.isNotEmpty()) {
            CarbonIconButton(
                imageVector = CarbonIcons.Close,
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
                .background(Carbon.theme.layer01)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CarbonSearchField(
            query = query,
            onQueryChange = onQueryChange,
            placeholder = stringResource(Res.string.search_placeholder),
            modifier = Modifier.weight(1f),
            searchIconContentDescription = stringResource(Res.string.summary_list_search),
        )

        Spacer(modifier = Modifier.width(Spacing.xs))

        CarbonIconButton(
            imageVector = CarbonIcons.Close,
            contentDescription = stringResource(Res.string.summary_list_close_search),
            onClick = onClose,
            buttonSize = Dimensions.compactIconButtonSize,
            iconSize = IconSizes.sm,
        )
    }
}
