package com.po4yka.ratatoskr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Ratatoskr theme: project-owned [AppTheme] tokens layered on top of Material 3.
 *
 * Provides [AppColors] and [AppType] via [LocalAppColors] / [LocalAppType] for the
 * design-system seam (`AppTheme.colors.X` / `AppTheme.type.X`), and bridges those tokens
 * into a Material 3 [MaterialTheme] so existing Material primitives (Button, OutlinedTextField,
 * CircularProgressIndicator, ...) pick up the same palette + scale.
 *
 * Also provides Frost tokens via [LocalFrostColors], [LocalFrostType], [LocalFrostSpacing],
 * [LocalFrostAlpha], [LocalFrostBorder], and [LocalFrostMotion]. Access via [AppTheme.frostColors],
 * [AppTheme.spacing], [AppTheme.alpha], [AppTheme.border], [AppTheme.motion].
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
    ) {
        MaterialTheme(
            colorScheme = appColors.toMaterialColorScheme(darkTheme),
            typography = appType.toMaterialTypography(),
            content = content,
        )
    }
}

/**
 * Bridge [AppColors] into a Material 3 [ColorScheme].
 *
 * Slot mapping is preserved from the previous Carbon-bridged scheme so the visual surface stays
 * the same: `interactive→primary`, `iconSecondary→secondary`, `linkPrimary→tertiary`,
 * `layer01→surface`, `layer02→surfaceVariant`, etc.
 */
private fun AppColors.toMaterialColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) {
        darkColorScheme(
            primary = interactive,
            onPrimary = textOnColor,
            primaryContainer = layer02,
            onPrimaryContainer = textPrimary,
            secondary = iconSecondary,
            onSecondary = textOnColor,
            secondaryContainer = layer02,
            onSecondaryContainer = textPrimary,
            tertiary = linkPrimary,
            onTertiary = textOnColor,
            tertiaryContainer = layer02,
            onTertiaryContainer = textPrimary,
            error = supportError,
            onError = textOnColor,
            errorContainer = supportError,
            onErrorContainer = textOnColor,
            background = background,
            onBackground = textPrimary,
            surface = layer01,
            onSurface = textPrimary,
            surfaceVariant = layer02,
            onSurfaceVariant = textSecondary,
            outline = borderSubtle00,
            outlineVariant = borderSubtle00,
            inverseSurface = backgroundInverse,
            inverseOnSurface = textOnColor,
            inversePrimary = linkPrimary,
        )
    } else {
        lightColorScheme(
            primary = interactive,
            onPrimary = textOnColor,
            primaryContainer = layer02,
            onPrimaryContainer = textPrimary,
            secondary = iconSecondary,
            onSecondary = textOnColor,
            secondaryContainer = layer02,
            onSecondaryContainer = textPrimary,
            tertiary = linkPrimary,
            onTertiary = textOnColor,
            tertiaryContainer = layer02,
            onTertiaryContainer = textPrimary,
            error = supportError,
            onError = textOnColor,
            errorContainer = supportError,
            onErrorContainer = textOnColor,
            background = background,
            onBackground = textPrimary,
            surface = layer01,
            onSurface = textPrimary,
            surfaceVariant = layer02,
            onSurfaceVariant = textSecondary,
            outline = borderSubtle00,
            outlineVariant = borderSubtle00,
            inverseSurface = backgroundInverse,
            inverseOnSurface = textOnColor,
            inversePrimary = linkPrimary,
        )
    }

/**
 * Bridge [AppType] into a Material 3 [Typography].
 *
 * Slot mapping is preserved from the previous Carbon-bridged scheme: heading02/03/04 cover
 * display + headline slots, headingCompact01 covers titleLarge / titleMedium, label01 covers
 * the title-small / label / body-small slots, body01 / bodyCompact01 cover body-large /
 * body-medium.
 */
private fun AppType.toMaterialTypography(): Typography =
    Typography(
        displayLarge = heading02,
        displayMedium = heading03,
        displaySmall = heading04,
        headlineLarge = heading02,
        headlineMedium = heading03,
        headlineSmall = heading04,
        titleLarge = headingCompact01,
        titleMedium = headingCompact01,
        titleSmall = label01,
        bodyLarge = body01,
        bodyMedium = bodyCompact01,
        bodySmall = label01,
        labelLarge = label01,
        labelMedium = label01,
        labelSmall = label01,
    )
