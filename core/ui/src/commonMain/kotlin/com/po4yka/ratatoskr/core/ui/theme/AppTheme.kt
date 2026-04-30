package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Project-owned theme object exposing the active [AppColors], [AppType], and Frost tokens
 * via composition locals.
 *
 * Usage from Composables:
 * ```
 * Text(
 *     text = "Hello",
 *     color = AppTheme.colors.textPrimary,
 *     style = AppTheme.type.body01,
 * )
 * // Frost tokens:
 * Box(Modifier.background(AppTheme.frostColors.page))
 * Spacer(Modifier.height(AppTheme.spacing.line))
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

    val frostColors: FrostColors
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostColors.current

    val frostType: FrostType
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostType.current

    val spacing: FrostSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostSpacing.current

    val alpha: FrostAlpha
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostAlpha.current

    val border: FrostBorder
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostBorder.current

    val motion: FrostMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalFrostMotion.current
}

internal val LocalAppColors =
    staticCompositionLocalOf<AppColors> {
        error("LocalAppColors not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalAppType =
    staticCompositionLocalOf<AppType> {
        error("LocalAppType not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostColors =
    staticCompositionLocalOf<FrostColors> {
        error("LocalFrostColors not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostType =
    staticCompositionLocalOf<FrostType> {
        error("LocalFrostType not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostSpacing =
    staticCompositionLocalOf<FrostSpacing> {
        error("LocalFrostSpacing not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostAlpha =
    staticCompositionLocalOf<FrostAlpha> {
        error("LocalFrostAlpha not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostBorder =
    staticCompositionLocalOf<FrostBorder> {
        error("LocalFrostBorder not provided. Wrap your UI in RatatoskrTheme().")
    }

internal val LocalFrostMotion =
    staticCompositionLocalOf<FrostMotion> {
        error("LocalFrostMotion not provided. Wrap your UI in RatatoskrTheme().")
    }
