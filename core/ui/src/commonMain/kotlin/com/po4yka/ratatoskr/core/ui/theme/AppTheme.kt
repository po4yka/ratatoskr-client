package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Project-owned theme object exposing the active Frost tokens via composition locals.
 *
 * Usage from Composables:
 * ```
 * Box(Modifier.background(AppTheme.frostColors.page))
 * Spacer(Modifier.height(AppTheme.spacing.line))
 * Text(text = "x", color = AppTheme.frostColors.ink, style = AppTheme.frostType.monoBody)
 * ```
 *
 * Wired up by [RatatoskrTheme] in [Theme.kt].
 */
object AppTheme {
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
