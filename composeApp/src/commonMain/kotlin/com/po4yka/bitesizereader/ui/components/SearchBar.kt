package com.po4yka.bitesizereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import bitesizereader.composeapp.generated.resources.Res
import bitesizereader.composeapp.generated.resources.search_clear
import bitesizereader.composeapp.generated.resources.search_placeholder
import bitesizereader.composeapp.generated.resources.summary_list_close_search
import bitesizereader.composeapp.generated.resources.summary_list_search
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons
import com.po4yka.bitesizereader.ui.theme.Dimensions
import com.po4yka.bitesizereader.ui.theme.IconSizes
import com.po4yka.bitesizereader.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource

@Suppress("FunctionNaming")
@Composable
fun SummarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Carbon.theme.layer01)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = CarbonIcons.Search,
            contentDescription = stringResource(Res.string.summary_list_search),
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
                        text = stringResource(Res.string.search_placeholder),
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
                onClick = { onQueryChange("") },
                buttonSize = Dimensions.compactIconButtonSize,
                iconSize = IconSizes.xs,
            )
        }

        CarbonIconButton(
            imageVector = CarbonIcons.Close,
            contentDescription = stringResource(Res.string.summary_list_close_search),
            onClick = onClose,
            buttonSize = Dimensions.compactIconButtonSize,
            iconSize = IconSizes.sm,
        )
    }
}
