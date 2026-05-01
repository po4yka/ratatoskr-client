package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Project-owned [LocalContentColor] so Frost atoms and composables can read a default ink
 * color without pulling in the Material 3 composition local.
 */
val LocalContentColor = compositionLocalOf<Color> { Color.Unspecified }

/**
 * Project-owned [LocalTextStyle] so Frost atoms and composables can read a default text style
 * without pulling in the Material 3 composition local.
 */
val LocalTextStyle = compositionLocalOf<TextStyle> { TextStyle.Default }

/**
 * Ratatoskr theme: project-owned [AppTheme] tokens.
 *
 * Provides [AppColors] and [AppType] via [LocalAppColors] / [LocalAppType] for the
 * design-system seam (`AppTheme.colors.X` / `AppTheme.type.X`).
 *
 * Also provides Frost tokens via [LocalFrostColors], [LocalFrostType], [LocalFrostSpacing],
 * [LocalFrostAlpha], [LocalFrostBorder], and [LocalFrostMotion]. Access via [AppTheme.frostColors],
 * [AppTheme.spacing], [AppTheme.alpha], [AppTheme.border], [AppTheme.motion].
 *
 * [LocalContentColor] and [LocalTextStyle] are provided with Frost ink / monoBody defaults so
 * composables that read them don't need a Material dependency.
 */
@Composable
fun RatatoskrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val appColors = if (darkTheme) darkAppColors else lightAppColors
    val appType = rememberDefaultAppType()
    val frostColors = if (darkTheme) frostDark else frostLight
    val frostType = rememberFrostType()

    CompositionLocalProvider(
        LocalAppColors provides appColors,
        LocalAppType provides appType,
        LocalFrostColors provides frostColors,
        LocalFrostType provides frostType,
        LocalFrostSpacing provides frostSpacingDefault,
        LocalFrostAlpha provides frostAlphaDefault,
        LocalFrostBorder provides frostBorderDefault,
        LocalFrostMotion provides frostMotionDefault,
        LocalContentColor provides frostColors.ink,
        LocalTextStyle provides frostType.monoBody.copy(color = frostColors.ink),
        content = content,
    )
}
