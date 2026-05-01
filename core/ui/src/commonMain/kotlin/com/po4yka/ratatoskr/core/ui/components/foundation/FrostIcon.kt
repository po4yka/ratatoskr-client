@file:Suppress("MagicNumber")

package com.po4yka.ratatoskr.core.ui.components.foundation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription as a11yContentDescription
import androidx.compose.ui.unit.dp
import com.po4yka.ratatoskr.core.ui.theme.LocalContentColor

/**
 * Frost icon — thin Image wrapper with tint, replacing the M3 Icon composable.
 *
 * - No Material 3 dependency: built on Compose Foundation only.
 * - Tint defaults to LocalContentColor (provided by RatatoskrTheme).
 * - 24.dp default minimum size matches M3 Icon defaults.
 */
@Composable
fun FrostIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    FrostIcon(
        painter = rememberVectorPainter(imageVector),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun FrostIcon(
    bitmap: ImageBitmap,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    FrostIcon(
        painter = remember(bitmap) { BitmapPainter(bitmap) },
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun FrostIcon(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    val colorFilter = if (tint == Color.Unspecified) null else ColorFilter.tint(tint)
    val semanticsModifier =
        if (contentDescription != null) {
            Modifier.semantics {
                this.a11yContentDescription = contentDescription
                this.role = Role.Image
            }
        } else {
            Modifier
        }
    Image(
        painter = painter,
        contentDescription = null, // semantics handled above
        modifier = modifier.defaultMinSize(24.dp, 24.dp).then(semanticsModifier),
        colorFilter = colorFilter,
    )
}
