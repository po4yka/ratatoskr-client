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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.gabrieldrn.carbon.Carbon
import com.po4yka.bitesizereader.ui.icons.CarbonIcons

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
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = CarbonIcons.Search,
            contentDescription = "Search",
            tint = Carbon.theme.iconSecondary,
            modifier = Modifier.size(20.dp),
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
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
                        text = "Search articles...",
                        style = Carbon.typography.bodyCompact01,
                        color = Carbon.theme.textPlaceholder,
                    )
                }
                innerTextField()
            },
        )

        if (query.isNotEmpty()) {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(32.dp),
            ) {
                Icon(
                    imageVector = CarbonIcons.Close,
                    contentDescription = "Clear search",
                    tint = Carbon.theme.iconSecondary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                imageVector = CarbonIcons.Close,
                contentDescription = "Close search",
                tint = Carbon.theme.iconPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}
