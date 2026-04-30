package com.po4yka.ratatoskr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import com.po4yka.ratatoskr.core.ui.components.frost.BrutalistCard

// TODO: callers should migrate to BrutalistCard directly; LayerCard is a transitional shim
@Composable
fun LayerCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    @Suppress("UNUSED_PARAMETER") backgroundColor: Color = Color.Unspecified,
    @Suppress("UNUSED_PARAMETER") borderColor: Color = Color.Unspecified,
    @Suppress("UNUSED_PARAMETER") shape: Shape = RectangleShape,
    content: @Composable BoxScope.() -> Unit,
) {
    val clickModifier =
        if (onClick != null) {
            Modifier.clickable(enabled = enabled, role = Role.Button, onClick = onClick)
        } else {
            Modifier
        }

    BrutalistCard(modifier = modifier.then(clickModifier)) {
        Box(content = content)
    }
}
