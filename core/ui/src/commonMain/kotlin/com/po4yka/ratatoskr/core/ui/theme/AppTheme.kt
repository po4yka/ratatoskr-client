package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Project-owned theme object exposing the active [AppColors] and [AppType] via composition locals.
 *
 * Usage from Composables:
 * ```
 * Text(
 *     text = "Hello",
 *     color = AppTheme.colors.textPrimary,
 *     style = AppTheme.type.body01,
 * )
 * ```
 *
 * Wired up by [RatatoskrTheme] in [Theme.kt].
 */
object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val type: AppType
        @Composable
        @ReadOnlyComposable
        get() = LocalAppType.current
}

internal val LocalAppColors =
    staticCompositionLocalOf<AppColors> {
        error("LocalAppColors not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalAppType =
    staticCompositionLocalOf<AppType> {
        error("LocalAppType not provided. Wrap your UI in RatatoskrTheme().")
    }
