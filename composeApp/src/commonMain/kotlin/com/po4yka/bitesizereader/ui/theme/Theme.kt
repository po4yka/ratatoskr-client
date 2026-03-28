package com.po4yka.bitesizereader.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gabrieldrn.carbon.CarbonDesignSystem
import com.gabrieldrn.carbon.Carbon
import com.gabrieldrn.carbon.foundation.color.Gray100Theme
import com.gabrieldrn.carbon.foundation.color.WhiteTheme

/**
 * Bite-Size Reader theme combining Carbon Design System with Material 3.
 *
 * This theme wraps CarbonDesignSystem to provide access to Carbon components
 * while maintaining Material 3 theming for existing components.
 */
@Composable
fun BiteSizeReaderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val carbonTheme = if (darkTheme) Gray100Theme else WhiteTheme

    CarbonDesignSystem(
        theme = carbonTheme,
    ) {
        val materialColorScheme =
            rememberCarbonMaterialColorScheme()
        val materialTypography =
            rememberCarbonMaterialTypography()

        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = materialTypography,
            content = content,
        )
    }
}

@Composable
private fun rememberCarbonMaterialColorScheme(): ColorScheme {
    val theme = Carbon.theme

    return remember(
        theme.background,
        theme.backgroundInverse,
        theme.borderSubtle00,
        theme.iconSecondary,
        theme.interactive,
        theme.layer01,
        theme.layer02,
        theme.linkPrimary,
        theme.supportError,
        theme.textOnColor,
        theme.textPrimary,
        theme.textSecondary,
    ) {
        androidx.compose.material3.lightColorScheme(
            primary = theme.interactive,
            onPrimary = theme.textOnColor,
            primaryContainer = theme.layer02,
            onPrimaryContainer = theme.textPrimary,
            secondary = theme.iconSecondary,
            onSecondary = theme.textOnColor,
            secondaryContainer = theme.layer02,
            onSecondaryContainer = theme.textPrimary,
            tertiary = theme.linkPrimary,
            onTertiary = theme.textOnColor,
            tertiaryContainer = theme.layer02,
            onTertiaryContainer = theme.textPrimary,
            error = theme.supportError,
            onError = theme.textOnColor,
            errorContainer = theme.supportError,
            onErrorContainer = theme.textOnColor,
            background = theme.background,
            onBackground = theme.textPrimary,
            surface = theme.layer01,
            onSurface = theme.textPrimary,
            surfaceVariant = theme.layer02,
            onSurfaceVariant = theme.textSecondary,
            outline = theme.borderSubtle00,
            outlineVariant = theme.borderSubtle00,
            inverseSurface = theme.backgroundInverse,
            inverseOnSurface = theme.textOnColor,
            inversePrimary = theme.linkPrimary,
        )
    }
}

@Composable
private fun rememberCarbonMaterialTypography(): Typography {
    val typography = Carbon.typography

    return remember(
        typography.body01,
        typography.bodyCompact01,
        typography.heading02,
        typography.heading03,
        typography.heading04,
        typography.headingCompact01,
        typography.label01,
    ) {
        Typography(
            displayLarge = typography.heading02,
            displayMedium = typography.heading03,
            displaySmall = typography.heading04,
            headlineLarge = typography.heading02,
            headlineMedium = typography.heading03,
            headlineSmall = typography.heading04,
            titleLarge = typography.headingCompact01,
            titleMedium = typography.headingCompact01,
            titleSmall = typography.label01,
            bodyLarge = typography.body01,
            bodyMedium = typography.bodyCompact01,
            bodySmall = typography.label01,
            labelLarge = typography.label01,
            labelMedium = typography.label01,
            labelSmall = typography.label01,
        )
    }
}
