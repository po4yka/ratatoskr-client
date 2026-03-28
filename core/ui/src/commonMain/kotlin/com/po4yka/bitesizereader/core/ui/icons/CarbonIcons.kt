package com.po4yka.bitesizereader.core.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * IBM Carbon Design System icons for Compose Multiplatform.
 * Icons are based on the official Carbon icons library:
 * https://carbondesignsystem.com/elements/icons/library/
 *
 * All icons are 32x32dp by default (Carbon's standard size).
 */
object CarbonIcons {
    // Icon dimension constants
    private val ICON_SIZE = 32.dp
    private const val VIEWPORT_SIZE = 32f

    // Common arc/circle radii used across multiple icons
    private object Radius {
        const val TINY = 1f       // Small dots, buttons
        const val SMALL = 2f     // Standard corners, small circles
        const val MEDIUM = 3f    // Medium circles
        const val LARGE = 4f     // Larger corners
        const val XL = 5f        // Extra large corners
        const val XXL = 6f       // Large arcs
        const val CIRCLE_SM = 8f // Small full circles
        const val CIRCLE_MD = 12f // Medium full circles
        const val CIRCLE_LG = 14f // Large full circles (main icon circles)
    }

    // Arc drawing parameter defaults
    private object Arc {
        const val NO_ROTATION = 0f
    }

    val Bookmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bookmark",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Inner filled part
                moveTo(24f, 4f)
                verticalLineTo(26.75f)
                lineToRelative(-7.1f, -3.59f)
                lineToRelative(-0.9f, -0.45f)
                lineToRelative(-0.9f, 0.45f)
                lineTo(8f, 26.75f)
                verticalLineTo(4f)
                horizontalLineTo(24f)
                // Outer outline
                moveTo(24f, 2f)
                horizontalLineTo(8f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 6f, 4f)
                verticalLineTo(30f)
                lineTo(16f, 25f)
                lineTo(26f, 30f)
                verticalLineTo(4f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 24f, 2f)
                close()
            }
        }.build()
    }

    val Folder: ImageVector by lazy {
        ImageVector.Builder(
            name = "Folder",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.17f, 6f)
                lineToRelative(3.42f, 3.41f)
                lineToRelative(0.58f, 0.59f)
                horizontalLineTo(28f)
                verticalLineTo(26f)
                horizontalLineTo(4f)
                verticalLineTo(6f)
                horizontalLineToRelative(7.17f)
                moveTo(11.17f, 4f)
                horizontalLineTo(4f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 6f)
                verticalLineTo(26f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(10f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, -2f)
                horizontalLineTo(16f)
                lineTo(12.59f, 4.59f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 11.17f, 4f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Gear outline
            path(fill = SolidColor(Color.Black)) {
                moveTo(27f, 16.76f)
                curveToRelative(0f, -0.25f, 0f, -0.5f, 0f, -0.76f)
                reflectiveCurveToRelative(0f, -0.51f, 0f, -0.77f)
                lineToRelative(1.92f, -1.68f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 29.3f, 11f)
                lineTo(26.94f, 7f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -1.73f, -1f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -0.64f, 0.1f)
                lineToRelative(-2.43f, 0.82f)
                arcToRelative(11.35f, 11.35f, 0f, false, false, -1.31f, -0.75f)
                lineToRelative(-0.51f, -2.52f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, -1.61f)
                horizontalLineTo(13.64f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, 1.61f)
                lineToRelative(-0.51f, 2.52f)
                arcToRelative(11.48f, 11.48f, 0f, false, false, -1.32f, 0.75f)
                lineTo(7.43f, 6.06f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 6.79f, 6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 5.06f, 7f)
                lineTo(2.7f, 11f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 0.41f, 2.51f)
                lineTo(5f, 15.24f)
                curveToRelative(0f, 0.25f, 0f, 0.5f, 0f, 0.76f)
                reflectiveCurveToRelative(0f, 0.51f, 0f, 0.77f)
                lineTo(3.11f, 18.45f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2.7f, 21f)
                lineTo(5.06f, 25f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 1.73f, 1f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 0.64f, -0.1f)
                lineToRelative(2.43f, -0.82f)
                arcToRelative(11.35f, 11.35f, 0f, false, false, 1.31f, 0.75f)
                lineToRelative(0.51f, 2.52f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 1.61f)
                horizontalLineToRelative(4.72f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -1.61f)
                lineToRelative(0.51f, -2.52f)
                arcToRelative(11.48f, 11.48f, 0f, false, false, 1.32f, -0.75f)
                lineToRelative(2.42f, 0.82f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 0.64f, 0.1f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 1.73f, -1f)
                lineTo(29.3f, 21f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -0.41f, -2.51f)
                close()
                moveTo(25.21f, 24f)
                lineToRelative(-3.43f, -1.16f)
                arcToRelative(8.86f, 8.86f, 0f, false, true, -2.71f, 1.57f)
                lineTo(18.36f, 28f)
                horizontalLineTo(13.64f)
                lineToRelative(-0.71f, -3.55f)
                arcToRelative(9.36f, 9.36f, 0f, false, true, -2.7f, -1.57f)
                lineTo(6.79f, 24f)
                lineTo(4.43f, 20f)
                lineToRelative(2.72f, -2.4f)
                arcToRelative(8.9f, 8.9f, 0f, false, true, 0f, -3.13f)
                lineTo(4.43f, 12f)
                lineTo(6.79f, 8f)
                lineToRelative(3.43f, 1.16f)
                arcToRelative(8.86f, 8.86f, 0f, false, true, 2.71f, -1.57f)
                lineTo(13.64f, 4f)
                horizontalLineToRelative(4.72f)
                lineToRelative(0.71f, 3.55f)
                arcToRelative(9.36f, 9.36f, 0f, false, true, 2.7f, 1.57f)
                lineTo(25.21f, 8f)
                lineTo(27.57f, 12f)
                lineToRelative(-2.72f, 2.4f)
                arcToRelative(8.9f, 8.9f, 0f, false, true, 0f, 3.13f)
                lineTo(27.57f, 20f)
                close()
            }
            // Center circle
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 22f)
                arcToRelative(Radius.XXL, Radius.XXL, Arc.NO_ROTATION, true, true, 6f, -6f)
                arcTo(5.94f, 5.94f, 0f, false, true, 16f, 22f)
                close()
                moveTo(16f, 12f)
                arcToRelative(3.91f, 3.91f, 0f, false, false, -4f, 4f)
                arcToRelative(3.91f, 3.91f, 0f, false, false, 4f, 4f)
                arcToRelative(3.91f, 3.91f, 0f, false, false, 4f, -4f)
                arcTo(3.91f, 3.91f, 0f, false, false, 16f, 12f)
                close()
            }
        }.build()
    }

    val ArrowLeft: ImageVector by lazy {
        ImageVector.Builder(
            name = "ArrowLeft",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14f, 26f)
                lineTo(15.41f, 24.59f)
                lineTo(7.83f, 17f)
                lineTo(28f, 17f)
                lineTo(28f, 15f)
                lineTo(7.83f, 15f)
                lineTo(15.41f, 7.41f)
                lineTo(14f, 6f)
                lineTo(4f, 16f)
                close()
            }
        }.build()
    }

    val CheckmarkFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "CheckmarkFilled",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 2f)
                arcTo(Radius.CIRCLE_LG, Radius.CIRCLE_LG, Arc.NO_ROTATION, true, false, 30f, 16f)
                arcTo(Radius.CIRCLE_LG, Radius.CIRCLE_LG, Arc.NO_ROTATION, false, false, 16f, 2f)
                close()
                moveTo(14f, 21.5908f)
                lineToRelative(-5f, -5f)
                lineTo(10.5906f, 15f)
                lineTo(14f, 18.4092f)
                lineTo(21.41f, 11f)
                lineToRelative(1.5957f, 1.5859f)
                close()
            }
        }.build()
    }

    val CircleOutline: ImageVector by lazy {
        ImageVector.Builder(
            name = "CircleOutline",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 2f)
                curveToRelative(-7.732f, 0f, -14f, 6.268f, -14f, 14f)
                reflectiveCurveToRelative(6.268f, 14f, 14f, 14f)
                reflectiveCurveToRelative(14f, -6.268f, 14f, -14f)
                reflectiveCurveTo(23.732f, 2f, 16f, 2f)
                close()
                moveTo(16f, 28f)
                curveToRelative(-6.6274f, 0f, -12f, -5.3726f, -12f, -12f)
                reflectiveCurveToRelative(5.3726f, -12f, 12f, -12f)
                reflectiveCurveToRelative(12f, 5.3726f, 12f, 12f)
                reflectiveCurveToRelative(-5.3726f, 12f, -12f, 12f)
                close()
            }
        }.build()
    }

    val Share: ImageVector by lazy {
        ImageVector.Builder(
            name = "Share",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(23f, 20f)
                arcToRelative(Radius.XL, Radius.XL, Arc.NO_ROTATION, false, false, -3.89f, 1.89f)
                lineTo(11.8f, 17.32f)
                arcToRelative(4.46f, 4.46f, 0f, false, false, 0f, -2.64f)
                lineToRelative(7.31f, -4.57f)
                arcTo(5f, 5f, 0f, true, false, 18f, 7f)
                arcToRelative(4.79f, 4.79f, 0f, false, false, 0.2f, 1.32f)
                lineToRelative(-7.31f, 4.57f)
                arcToRelative(Radius.XL, Radius.XL, Arc.NO_ROTATION, true, false, 0f, 6.22f)
                lineToRelative(7.31f, 4.57f)
                arcTo(4.79f, 4.79f, 0f, false, false, 18f, 25f)
                arcToRelative(Radius.XL, Radius.XL, Arc.NO_ROTATION, true, false, 5f, -5f)
                close()
                moveTo(23f, 4f)
                arcToRelative(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, true, true, -3f, 3f)
                arcTo(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, true, 23f, 4f)
                close()
                moveTo(7f, 19f)
                arcToRelative(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, true, true, 3f, -3f)
                arcTo(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, true, 7f, 19f)
                close()
                moveTo(23f, 28f)
                arcToRelative(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, true, true, 3f, -3f)
                arcTo(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, true, 23f, 28f)
                close()
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "Close",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17.4141f, 16f)
                lineTo(24f, 9.4141f)
                lineTo(22.5859f, 8f)
                lineTo(16f, 14.5859f)
                lineTo(9.4143f, 8f)
                lineTo(8f, 9.4141f)
                lineTo(14.5859f, 16f)
                lineTo(8f, 22.5859f)
                lineTo(9.4143f, 24f)
                lineTo(16f, 17.4141f)
                lineTo(22.5859f, 24f)
                lineTo(24f, 22.5859f)
                close()
            }
        }.build()
    }

    val Checkmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "Checkmark",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 24f)
                lineTo(4f, 15f)
                lineTo(5.414f, 13.586f)
                lineTo(13f, 21.171f)
                lineTo(26.586f, 7.586f)
                lineTo(28f, 9f)
                close()
            }
        }.build()
    }

    val Renew: ImageVector by lazy {
        ImageVector.Builder(
            name = "Renew",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Top arrow
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 10f)
                horizontalLineTo(6.78f)
                arcTo(11f, 11f, 0f, false, true, 27f, 16f)
                horizontalLineToRelative(2f)
                arcTo(13f, 13f, 0f, false, false, 6f, 7.68f)
                verticalLineTo(4f)
                horizontalLineTo(4f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                close()
            }
            // Bottom arrow
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 22f)
                horizontalLineToRelative(5.22f)
                arcTo(11f, 11f, 0f, false, true, 5f, 16f)
                horizontalLineTo(3f)
                arcToRelative(13f, 13f, 0f, false, false, 23f, 8.32f)
                verticalLineTo(28f)
                horizontalLineToRelative(2f)
                verticalLineTo(20f)
                horizontalLineTo(20f)
                close()
            }
        }.build()
    }

    val Home: ImageVector by lazy {
        ImageVector.Builder(
            name = "Home",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.6123f, 2.2138f)
                arcToRelative(1.01f, 1.01f, 0f, false, false, -1.2427f, 0f)
                lineTo(1f, 13.4194f)
                lineToRelative(1.2427f, 1.5717f)
                lineTo(4f, 13.6209f)
                verticalLineTo(26f)
                arcToRelative(2.0041f, 2.0041f, 0f, false, false, 2f, 2f)
                horizontalLineTo(26f)
                arcToRelative(2.0037f, 2.0037f, 0f, false, false, 2f, -2f)
                verticalLineTo(13.63f)
                lineTo(29.7573f, 15f)
                lineTo(31f, 13.4282f)
                close()
                moveTo(18f, 26f)
                horizontalLineTo(14f)
                verticalLineTo(18f)
                horizontalLineToRelative(4f)
                close()
                moveTo(20f, 26f)
                verticalLineTo(18f)
                arcToRelative(2.0023f, 2.0023f, 0f, false, false, -2f, -2f)
                horizontalLineTo(14f)
                arcToRelative(2.002f, 2.002f, 0f, false, false, -2f, 2f)
                verticalLineToRelative(8f)
                horizontalLineTo(6f)
                verticalLineTo(12.0615f)
                lineToRelative(10f, -7.79f)
                lineToRelative(10f, 7.8005f)
                verticalLineTo(26f)
                close()
            }
        }.build()
    }

    val TrashCan: ImageVector by lazy {
        ImageVector.Builder(
            name = "TrashCan",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 12f)
                horizontalLineTo(14f)
                verticalLineTo(24f)
                horizontalLineTo(12f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(18f, 12f)
                horizontalLineTo(20f)
                verticalLineTo(24f)
                horizontalLineTo(18f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 6f)
                verticalLineTo(8f)
                horizontalLineTo(6f)
                verticalLineTo(28f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineTo(24f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(8f)
                horizontalLineToRelative(2f)
                verticalLineTo(6f)
                close()
                moveTo(8f, 28f)
                verticalLineTo(8f)
                horizontalLineTo(24f)
                verticalLineTo(28f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                horizontalLineTo(20f)
                verticalLineTo(4f)
                horizontalLineTo(12f)
                close()
            }
        }.build()
    }

    val Document: ImageVector by lazy {
        ImageVector.Builder(
            name = "Document",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(25.7f, 9.3f)
                lineToRelative(-7f, -7f)
                curveTo(18.5f, 2.1f, 18.3f, 2f, 18f, 2f)
                horizontalLineTo(8f)
                curveTo(6.9f, 2f, 6f, 2.9f, 6f, 4f)
                verticalLineToRelative(24f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(16f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(10f)
                curveTo(26f, 9.7f, 25.9f, 9.5f, 25.7f, 9.3f)
                close()
                moveTo(18f, 4.4f)
                lineToRelative(5.6f, 5.6f)
                horizontalLineTo(18f)
                verticalLineTo(4.4f)
                close()
                moveTo(24f, 28f)
                horizontalLineTo(8f)
                verticalLineTo(4f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(6f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(6f)
                verticalLineTo(28f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 22f)
                horizontalLineTo(22f)
                verticalLineTo(24f)
                horizontalLineTo(10f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 16f)
                horizontalLineTo(22f)
                verticalLineTo(18f)
                horizontalLineTo(10f)
                close()
            }
        }.build()
    }

    val WarningAlt: ImageVector by lazy {
        ImageVector.Builder(
            name = "WarningAlt",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Exclamation dot
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 23f)
                arcToRelative(1.5f, 1.5f, 0f, true, false, 1.5f, 1.5f)
                arcTo(1.5f, 1.5f, 0f, false, false, 16f, 23f)
                close()
            }
            // Exclamation line
            path(fill = SolidColor(Color.Black)) {
                moveTo(15f, 12f)
                horizontalLineTo(17f)
                verticalLineTo(21f)
                horizontalLineTo(15f)
                close()
            }
            // Triangle outline
            path(fill = SolidColor(Color.Black)) {
                moveTo(29f, 30f)
                horizontalLineTo(3f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, -0.8872f, -1.4614f)
                lineToRelative(13f, -25f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 1.7744f, 0f)
                lineToRelative(13f, 25f)
                arcTo(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 29f, 30f)
                close()
                moveTo(4.6507f, 28f)
                horizontalLineTo(27.3493f)
                lineToRelative(0.002f, -0.0033f)
                lineTo(16.002f, 6.1714f)
                horizontalLineToRelative(-0.004f)
                lineTo(4.6487f, 27.9967f)
                close()
            }
        }.build()
    }

    val ColorPalette: ImageVector by lazy {
        ImageVector.Builder(
            name = "ColorPalette",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.54f, 2f)
                arcTo(Radius.CIRCLE_LG, Radius.CIRCLE_LG, Arc.NO_ROTATION, false, false, 2f, 16f)
                arcToRelative(4.82f, 4.82f, 0f, false, false, 6.09f, 4.65f)
                lineToRelative(1.12f, -0.31f)
                arcTo(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, true, 13f, 23.24f)
                verticalLineTo(27f)
                arcToRelative(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, false, 3f, 3f)
                arcTo(Radius.CIRCLE_LG, Radius.CIRCLE_LG, Arc.NO_ROTATION, false, false, 30f, 15.46f)
                arcTo(14.05f, 14.05f, 0f, false, false, 16.54f, 2f)
                close()
                moveTo(24.65f, 24.32f)
                arcTo(11.93f, 11.93f, 0f, false, true, 16f, 28f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, -1f, -1f)
                verticalLineTo(23.24f)
                arcToRelative(Radius.XL, Radius.XL, Arc.NO_ROTATION, false, false, -5f, -5f)
                arcToRelative(5.07f, 5.07f, 0f, false, false, -1.33f, 0.18f)
                lineToRelative(-1.12f, 0.31f)
                arcTo(2.82f, 2.82f, 0f, false, true, 4f, 16f)
                arcTo(12f, 12f, 0f, false, true, 16.47f, 4f)
                arcTo(12.18f, 12.18f, 0f, false, true, 28f, 15.53f)
                arcTo(11.89f, 11.89f, 0f, false, true, 24.65f, 24.32f)
                close()
            }
            // Color dots
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 9f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, -2f, 2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 10f, 9f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 7f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, -2f, 2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 7f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 9f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, -2f, 2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 22f, 9f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(25f, 15f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, -2f, 2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 25f, 15f)
                close()
            }
        }.build()
    }

    val Idea: ImageVector by lazy {
        ImageVector.Builder(
            name = "Idea",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 2f)
                arcTo(10f, 10f, 0f, false, false, 6f, 12f)
                arcToRelative(9.19f, 9.19f, 0f, false, false, 3.46f, 7.62f)
                curveToRelative(1f, 0.93f, 1.54f, 1.46f, 1.54f, 2.38f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -1.84f, -1.11f, -2.87f, -2.19f, -3.86f)
                arcTo(7.2f, 7.2f, 0f, false, true, 8f, 12f)
                arcToRelative(8f, 8f, 0f, false, true, 16f, 0f)
                arcToRelative(7.2f, 7.2f, 0f, false, true, -2.82f, 6.14f)
                curveToRelative(-1.07f, 1f, -2.18f, 2f, -2.18f, 3.86f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -0.92f, 0.53f, -1.45f, 1.54f, -2.39f)
                arcTo(9.18f, 9.18f, 0f, false, false, 26f, 12f)
                arcTo(10f, 10f, 0f, false, false, 16f, 2f)
                close()
            }
            // Base rectangles
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 24f)
                horizontalLineTo(21f)
                verticalLineTo(26f)
                horizontalLineTo(11f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 28f)
                horizontalLineTo(19f)
                verticalLineTo(30f)
                horizontalLineTo(13f)
                close()
            }
        }.build()
    }

    val Restaurant: ImageVector by lazy {
        ImageVector.Builder(
            name = "Restaurant",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Fork
            path(fill = SolidColor(Color.Black)) {
                moveTo(14f, 11f)
                arcToRelative(4f, 4f, 0f, false, true, -8f, 0f)
                verticalLineTo(2f)
                horizontalLineTo(4f)
                verticalLineToRelative(9f)
                arcToRelative(Radius.XXL, Radius.XXL, Arc.NO_ROTATION, false, false, 5f, 5.91f)
                verticalLineTo(30f)
                horizontalLineToRelative(2f)
                verticalLineTo(16.91f)
                arcTo(6f, 6f, 0f, false, false, 16f, 11f)
                verticalLineTo(2f)
                horizontalLineTo(14f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(8f, 2f)
                horizontalLineTo(10f)
                verticalLineTo(9f)
                horizontalLineTo(8f)
                close()
            }
            // Knife
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 2f)
                horizontalLineTo(21f)
                verticalLineTo(30f)
                horizontalLineToRelative(2f)
                verticalLineTo(20f)
                horizontalLineToRelative(3f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(8f)
                arcTo(5.78f, 5.78f, 0f, false, false, 22f, 2f)
                close()
                moveTo(26f, 18f)
                horizontalLineTo(23f)
                verticalLineTo(4.09f)
                curveToRelative(2.88f, 0.56f, 3f, 3.54f, 3f, 3.91f)
                close()
            }
        }.build()
    }

    val GameWireless: ImageVector by lazy {
        ImageVector.Builder(
            name = "GameWireless",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Main controller outline
            path(fill = SolidColor(Color.Black)) {
                moveTo(7.51f, 30f)
                arcToRelative(5.48f, 5.48f, 0f, false, true, -1.44f, -0.19f)
                arcTo(5.6f, 5.6f, 0f, false, true, 2.19f, 23f)
                lineToRelative(2.33f, -8.84f)
                arcToRelative(5.54f, 5.54f, 0f, false, true, 2.59f, -3.41f)
                arcToRelative(5.43f, 5.43f, 0f, false, true, 4.15f, -0.54f)
                arcTo(5.52f, 5.52f, 0f, false, true, 14.7f, 13f)
                horizontalLineToRelative(2.6f)
                arcToRelative(5.49f, 5.49f, 0f, false, true, 3.44f, -2.81f)
                arcToRelative(5.43f, 5.43f, 0f, false, true, 4.15f, 0.54f)
                arcToRelative(5.54f, 5.54f, 0f, false, true, 2.59f, 3.41f)
                lineTo(29.81f, 23f)
                arcToRelative(5.6f, 5.6f, 0f, false, true, -3.88f, 6.83f)
                arcToRelative(5.48f, 5.48f, 0f, false, true, -1.44f, 0.19f)
                arcToRelative(5.67f, 5.67f, 0f, false, true, -4.68f, -2.53f)
                lineTo(18f, 25f)
                horizontalLineTo(14f)
                lineToRelative(-1.81f, 2.49f)
                arcTo(5.67f, 5.67f, 0f, false, true, 7.51f, 30f)
                close()
                moveTo(9.79f, 12f)
                arcToRelative(3.51f, 3.51f, 0f, false, false, -1.83f, 0.51f)
                arcToRelative(3.55f, 3.55f, 0f, false, false, -1.65f, 2.18f)
                lineTo(3.98f, 23.53f)
                arcToRelative(3.58f, 3.58f, 0f, false, false, 2.48f, 4.37f)
                arcToRelative(3.63f, 3.63f, 0f, false, false, 3.91f, -1.23f)
                lineTo(12.62f, 23f)
                horizontalLineTo(19.38f)
                lineToRelative(2.25f, 3.67f)
                arcToRelative(3.63f, 3.63f, 0f, false, false, 3.91f, 1.23f)
                arcToRelative(3.58f, 3.58f, 0f, false, false, 2.48f, -4.37f)
                lineToRelative(-2.33f, -8.84f)
                arcToRelative(3.55f, 3.55f, 0f, false, false, -1.65f, -2.18f)
                arcToRelative(3.47f, 3.47f, 0f, false, false, -2.66f, -0.35f)
                arcTo(3.53f, 3.53f, 0f, false, false, 18.83f, 15f)
                horizontalLineTo(13.17f)
                arcToRelative(3.53f, 3.53f, 0f, false, false, -2.55f, -2.84f)
                arcTo(3.51f, 3.51f, 0f, false, false, 9.79f, 12f)
                close()
            }
            // Left button
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 20f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, 2f, -2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 10f, 20f)
                close()
            }
            // Right buttons
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 17f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, true, true, 1f, -1f)
                arcTo(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 22f, 17f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 21f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, true, true, 1f, -1f)
                arcTo(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 22f, 21f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 19f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, true, true, 1f, -1f)
                arcTo(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 20f, 19f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(24f, 19f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, true, true, 1f, -1f)
                arcTo(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, true, 24f, 19f)
                close()
            }
            // Wireless signals
            path(fill = SolidColor(Color.Black)) {
                moveTo(13.75f, 9f)
                lineToRelative(-1.5f, -1.33f)
                arcToRelative(Radius.XL, Radius.XL, Arc.NO_ROTATION, false, true, 7.5f, 0f)
                lineTo(18.25f, 9f)
                arcToRelative(Radius.MEDIUM, Radius.MEDIUM, Arc.NO_ROTATION, false, false, -4.5f, 0f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21.25f, 6.37f)
                arcToRelative(7f, 7f, 0f, false, false, -10.5f, 0f)
                lineTo(9.25f, 5.05f)
                arcToRelative(9f, 9f, 0f, false, true, 13.5f, 0f)
                close()
            }
        }.build()
    }

    val RainDrop: ImageVector by lazy {
        ImageVector.Builder(
            name = "RainDrop",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 24f)
                verticalLineTo(22f)
                arcToRelative(3.2965f, 3.2965f, 0f, false, false, 3f, -3f)
                horizontalLineToRelative(2f)
                arcTo(5.2668f, 5.2668f, 0f, false, true, 16f, 24f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 28f)
                arcToRelative(9.0114f, 9.0114f, 0f, false, true, -9f, -9f)
                arcToRelative(9.9843f, 9.9843f, 0f, false, true, 1.4941f, -4.9554f)
                lineTo(15.1528f, 3.4367f)
                arcToRelative(1.04f, 1.04f, 0f, false, true, 1.6944f, 0f)
                lineToRelative(6.6289f, 10.5564f)
                arcTo(10.0633f, 10.0633f, 0f, false, true, 25f, 19f)
                arcTo(9.0114f, 9.0114f, 0f, false, true, 16f, 28f)
                close()
                moveTo(16f, 5.8483f)
                lineToRelative(-5.7817f, 9.2079f)
                arcTo(7.9771f, 7.9771f, 0f, false, false, 9f, 19f)
                arcToRelative(7f, 7f, 0f, false, false, 14f, 0f)
                arcToRelative(8.0615f, 8.0615f, 0f, false, false, -1.248f, -3.9953f)
                close()
            }
        }.build()
    }

    val Gem: ImageVector by lazy {
        ImageVector.Builder(
            name = "Gem",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(23.5f, 4f)
                horizontalLineTo(8.5f)
                lineTo(1.7158f, 13.0454f)
                lineTo(16f, 29.5269f)
                lineTo(30.2842f, 13.0454f)
                close()
                moveTo(27f, 12f)
                horizontalLineTo(21.5543f)
                lineToRelative(-3.75f, -6f)
                horizontalLineTo(22.5f)
                close()
                moveTo(10.3021f, 14f)
                lineToRelative(3.7536f, 10.23f)
                lineTo(5.19f, 14f)
                close()
                moveTo(12.4321f, 14f)
                horizontalLineTo(19.568f)
                lineToRelative(-3.569f, 9.7212f)
                close()
                moveTo(12.8046f, 12f)
                lineTo(16f, 6.8867f)
                lineTo(19.1957f, 12f)
                close()
                moveTo(21.6979f, 14f)
                horizontalLineTo(26.81f)
                lineTo(17.9427f, 24.2314f)
                close()
                moveTo(9.5f, 6f)
                horizontalLineToRelative(4.6957f)
                lineToRelative(-3.75f, 6f)
                horizontalLineTo(5f)
                close()
            }
        }.build()
    }

    val OverflowMenuVertical: ImageVector by lazy {
        ImageVector.Builder(
            name = "OverflowMenuVertical",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Top dot
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 10f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, 16f, 6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 10f)
                close()
            }
            // Middle dot
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 18f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, 16f, 14f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 18f)
                close()
            }
            // Bottom dot
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 26f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, 16f, 22f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 26f)
                close()
            }
        }.build()
    }

    val Email: ImageVector by lazy {
        ImageVector.Builder(
            name = "Email",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(28f, 6f)
                horizontalLineTo(4f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 8f)
                verticalLineTo(24f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(8f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 28f, 6f)
                close()
                moveTo(25.8f, 8f)
                lineTo(16f, 14.78f)
                lineTo(6.2f, 8f)
                close()
                moveTo(4f, 24f)
                verticalLineTo(8.91f)
                lineToRelative(11.43f, 7.91f)
                arcToRelative(Radius.TINY, Radius.TINY, Arc.NO_ROTATION, false, false, 1.14f, 0f)
                lineTo(28f, 8.91f)
                verticalLineTo(24f)
                close()
            }
        }.build()
    }

    val Map: ImageVector by lazy {
        ImageVector.Builder(
            name = "Map",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            // Location pin
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 24f)
                lineToRelative(-6.09f, -8.6f)
                arcTo(8.14f, 8.14f, 0f, false, true, 16f, 2f)
                arcToRelative(8.08f, 8.08f, 0f, false, true, 8f, 8.13f)
                arcToRelative(8.2f, 8.2f, 0f, false, true, -1.8f, 5.13f)
                close()
                moveTo(16f, 4f)
                arcToRelative(6.07f, 6.07f, 0f, false, false, -6f, 6.13f)
                arcToRelative(6.19f, 6.19f, 0f, false, false, 1.49f, 4f)
                lineTo(16f, 20.52f)
                lineTo(20.63f, 14f)
                arcTo(6.24f, 6.24f, 0f, false, false, 22f, 10.13f)
                arcTo(6.07f, 6.07f, 0f, false, false, 16f, 4f)
                close()
            }
            // Pin dot
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 11f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, true, true, 2f, -2f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 11f)
                close()
            }
            // Map frame
            path(fill = SolidColor(Color.Black)) {
                moveTo(28f, 12f)
                horizontalLineTo(26f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(2f)
                verticalLineTo(28f)
                horizontalLineTo(4f)
                verticalLineTo(14f)
                horizontalLineTo(6f)
                verticalLineTo(12f)
                horizontalLineTo(4f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, 2f)
                verticalLineTo(28f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(14f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 28f, 12f)
                close()
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Search",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(29f, 27.5859f)
                lineTo(22.4131f, 21f)
                arcToRelative(11f, 11f, 0f, true, false, -1.4141f, 1.4141f)
                lineTo(27.5859f, 29f)
                close()
                moveTo(4f, 13f)
                arcToRelative(9f, 9f, 0f, true, true, 9f, 9f)
                arcTo(9.01f, 9.01f, 0f, false, true, 4f, 13f)
                close()
            }
        }.build()
    }

    val Filter: ImageVector by lazy {
        ImageVector.Builder(
            name = "Filter",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(18f, 28f)
                horizontalLineTo(14f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, -2f, -2f)
                verticalLineTo(18.41f)
                lineTo(4.59f, 11f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 4f, 9.59f)
                verticalLineTo(6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 6f, 4f)
                horizontalLineTo(26f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 2f, 2f)
                verticalLineTo(9.59f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 27.41f, 11f)
                lineTo(20f, 18.41f)
                verticalLineTo(26f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 18f, 28f)
                close()
                moveTo(6f, 6f)
                verticalLineTo(9.59f)
                lineToRelative(8f, 8f)
                verticalLineTo(26f)
                horizontalLineToRelative(4f)
                verticalLineTo(17.59f)
                lineToRelative(8f, -8f)
                verticalLineTo(6f)
                close()
            }
        }.build()
    }

    val Grid: ImageVector by lazy {
        ImageVector.Builder(
            name = "Grid",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4f)
                horizontalLineTo(6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 4f, 6f)
                verticalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 12f, 4f)
                close()
                moveTo(6f, 12f)
                verticalLineTo(6f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(6f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(26f, 4f)
                horizontalLineTo(20f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, 2f)
                verticalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(6f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 26f, 4f)
                close()
                moveTo(20f, 12f)
                verticalLineTo(6f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(6f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 18f)
                horizontalLineTo(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, 2f)
                verticalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(20f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 12f, 18f)
                close()
                moveTo(6f, 26f)
                verticalLineTo(20f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(6f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(26f, 18f)
                horizontalLineTo(20f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, -2f, 2f)
                verticalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineToRelative(6f)
                arcToRelative(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(20f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 26f, 18f)
                close()
                moveTo(20f, 26f)
                verticalLineTo(20f)
                horizontalLineToRelative(6f)
                verticalLineToRelative(6f)
                close()
            }
        }.build()
    }

    val List: ImageVector by lazy {
        ImageVector.Builder(
            name = "List",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 6f)
                horizontalLineTo(28f)
                verticalLineTo(8f)
                horizontalLineTo(10f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 15f)
                horizontalLineTo(28f)
                verticalLineTo(17f)
                horizontalLineTo(10f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 24f)
                horizontalLineTo(28f)
                verticalLineTo(26f)
                horizontalLineTo(10f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 5f)
                horizontalLineTo(8f)
                verticalLineTo(9f)
                horizontalLineTo(4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 14f)
                horizontalLineTo(8f)
                verticalLineTo(18f)
                horizontalLineTo(4f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 23f)
                horizontalLineTo(8f)
                verticalLineTo(27f)
                horizontalLineTo(4f)
                close()
            }
        }.build()
    }

    val SortAscending: ImageVector by lazy {
        ImageVector.Builder(
            name = "SortAscending",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(27.6f, 20.6f)
                lineTo(24f, 24.2f)
                verticalLineTo(4f)
                horizontalLineTo(22f)
                verticalLineTo(24.2f)
                lineToRelative(-3.6f, -3.6f)
                lineTo(17f, 22f)
                lineToRelative(6f, 6f)
                lineToRelative(6f, -6f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(9f, 4f)
                horizontalLineTo(3f)
                verticalLineTo(6f)
                horizontalLineTo(9f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 10f)
                horizontalLineTo(3f)
                verticalLineTo(12f)
                horizontalLineTo(11f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 16f)
                horizontalLineTo(3f)
                verticalLineTo(18f)
                horizontalLineTo(13f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(15f, 22f)
                horizontalLineTo(3f)
                verticalLineTo(24f)
                horizontalLineTo(15f)
                close()
            }
        }.build()
    }

    val BookmarkAdd: ImageVector by lazy {
        ImageVector.Builder(
            name = "BookmarkAdd",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(24f, 16f)
                verticalLineTo(26.75f)
                lineToRelative(-7.1f, -3.59f)
                lineToRelative(-0.9f, -0.45f)
                lineToRelative(-0.9f, 0.45f)
                lineTo(8f, 26.75f)
                verticalLineTo(4f)
                horizontalLineTo(18f)
                verticalLineTo(2f)
                horizontalLineTo(8f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, false, 6f, 4f)
                verticalLineTo(30f)
                lineTo(16f, 25f)
                lineTo(26f, 30f)
                verticalLineTo(16f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(26f, 6f)
                horizontalLineTo(24f)
                verticalLineTo(2f)
                horizontalLineTo(22f)
                verticalLineTo(6f)
                horizontalLineTo(18f)
                verticalLineTo(8f)
                horizontalLineTo(22f)
                verticalLineTo(12f)
                horizontalLineTo(24f)
                verticalLineTo(8f)
                horizontalLineTo(28f)
                verticalLineTo(6f)
                horizontalLineTo(26f)
                close()
            }
        }.build()
    }

    val Favorite: ImageVector by lazy {
        ImageVector.Builder(
            name = "Favorite",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Heart outline (Carbon "favorite" icon)
                moveTo(22.45f, 6f)
                arcTo(5.47f, 5.47f, Arc.NO_ROTATION, false, true, 27.92f, 11.45f)
                curveToRelative(0f, 2.87f, -1.82f, 5.81f, -5.41f, 8.72f)
                lineToRelative(-0.17f, 0.14f)
                lineTo(16f, 25.28f)
                lineToRelative(-6.34f, -4.97f)
                lineToRelative(-0.17f, -0.14f)
                curveTo(5.9f, 17.26f, 4.08f, 14.32f, 4.08f, 11.45f)
                arcTo(5.47f, 5.47f, Arc.NO_ROTATION, false, true, 9.55f, 6f)
                arcTo(5.28f, 5.28f, Arc.NO_ROTATION, false, true, 16f, 9.64f)
                arcTo(5.28f, 5.28f, Arc.NO_ROTATION, false, true, 22.45f, 6f)
                moveTo(22.45f, 4f)
                arcTo(7.27f, 7.27f, Arc.NO_ROTATION, false, false, 16f, 7.58f)
                arcTo(7.27f, 7.27f, Arc.NO_ROTATION, false, false, 9.55f, 4f)
                arcTo(7.47f, 7.47f, Arc.NO_ROTATION, false, false, 2.08f, 11.45f)
                curveToRelative(0f, 3.53f, 2.09f, 6.87f, 6.21f, 10.14f)
                lineToRelative(0.17f, 0.14f)
                lineTo(16f, 27.72f)
                lineToRelative(7.54f, -5.99f)
                lineToRelative(0.17f, -0.14f)
                curveToRelative(4.12f, -3.27f, 6.21f, -6.61f, 6.21f, -10.14f)
                arcTo(7.47f, 7.47f, Arc.NO_ROTATION, false, false, 22.45f, 4f)
                close()
            }
        }.build()
    }

    val FavoriteFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "FavoriteFilled",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Filled heart (Carbon "favorite--filled" icon)
                moveTo(22.45f, 4f)
                arcTo(7.27f, 7.27f, Arc.NO_ROTATION, false, false, 16f, 7.58f)
                arcTo(7.27f, 7.27f, Arc.NO_ROTATION, false, false, 9.55f, 4f)
                arcTo(7.47f, 7.47f, Arc.NO_ROTATION, false, false, 2.08f, 11.45f)
                curveToRelative(0f, 3.53f, 2.09f, 6.87f, 6.21f, 10.14f)
                lineToRelative(0.17f, 0.14f)
                lineTo(16f, 27.72f)
                lineToRelative(7.54f, -5.99f)
                lineToRelative(0.17f, -0.14f)
                curveToRelative(4.12f, -3.27f, 6.21f, -6.61f, 6.21f, -10.14f)
                arcTo(7.47f, 7.47f, Arc.NO_ROTATION, false, false, 22.45f, 4f)
                close()
            }
        }.build()
    }

    val Add: ImageVector by lazy {
        ImageVector.Builder(
            name = "Add",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 15f)
                verticalLineTo(2f)
                horizontalLineTo(15f)
                verticalLineTo(15f)
                horizontalLineTo(2f)
                verticalLineTo(17f)
                horizontalLineTo(15f)
                verticalLineTo(30f)
                horizontalLineTo(17f)
                verticalLineTo(17f)
                horizontalLineTo(30f)
                verticalLineTo(15f)
                horizontalLineTo(17f)
                close()
            }
        }.build()
    }

    val Notification: ImageVector by lazy {
        ImageVector.Builder(
            name = "Notification",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Bell icon (Carbon "notification" icon)
                moveTo(28.71f, 24.29f)
                lineTo(26f, 21.59f)
                verticalLineTo(14f)
                arcTo(10f, 10f, Arc.NO_ROTATION, false, false, 22f, 4.18f)
                verticalLineTo(2f)
                horizontalLineTo(20f)
                verticalLineTo(4.56f)
                arcTo(8f, 8f, Arc.NO_ROTATION, false, false, 18f, 14f)
                verticalLineTo(21.59f)
                lineToRelative(-2.71f, 2.7f)
                arcTo(1f, 1f, Arc.NO_ROTATION, false, false, 16f, 26f)
                horizontalLineTo(26f)
                arcTo(1f, 1f, Arc.NO_ROTATION, false, false, 28.71f, 24.29f)
                close()
                moveTo(26f, 24f)
                horizontalLineTo(18.41f)
                lineTo(20f, 22.41f)
                verticalLineTo(14f)
                arcTo(6f, 6f, Arc.NO_ROTATION, false, true, 26f, 14f)
                verticalLineTo(22.41f)
                lineTo(27.59f, 24f)
                horizontalLineTo(26f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 28f)
                horizontalLineTo(11f)
                verticalLineTo(26f)
                horizontalLineTo(6f)
                arcTo(1f, 1f, Arc.NO_ROTATION, false, true, 3.29f, 24.29f)
                lineTo(6f, 21.59f)
                verticalLineTo(14f)
                arcTo(10f, 10f, Arc.NO_ROTATION, false, true, 16f, 4.05f)
                verticalLineTo(6.09f)
                arcTo(8f, 8f, Arc.NO_ROTATION, false, false, 8f, 14f)
                verticalLineTo(22.41f)
                lineTo(4.41f, 26f)
                horizontalLineTo(21f)
                verticalLineTo(28f)
                close()
            }
        }.build()
    }

    val ChevronUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronUp",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 10f)
                lineTo(5.41f, 20.59f)
                lineTo(6.83f, 22f)
                lineTo(16f, 12.83f)
                lineTo(25.17f, 22f)
                lineTo(26.59f, 20.59f)
                lineTo(16f, 10f)
                close()
            }
        }.build()
    }

    val ChevronDown: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronDown",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 22f)
                lineTo(5.41f, 11.41f)
                lineTo(6.83f, 10f)
                lineTo(16f, 19.17f)
                lineTo(25.17f, 10f)
                lineTo(26.59f, 11.41f)
                lineTo(16f, 22f)
                close()
            }
        }.build()
    }

    val Download: ImageVector by lazy {
        ImageVector.Builder(
            name = "Download",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Arrow pointing down
                moveTo(26f, 24f)
                verticalLineTo(28f)
                horizontalLineTo(6f)
                verticalLineTo(24f)
                horizontalLineTo(4f)
                verticalLineTo(28f)
                arcToRelative(2f, 2f, Arc.NO_ROTATION, false, false, 2f, 2f)
                horizontalLineTo(26f)
                arcToRelative(2f, 2f, Arc.NO_ROTATION, false, false, 2f, -2f)
                verticalLineTo(24f)
                close()
                // Down arrow
                moveTo(26f, 14f)
                lineToRelative(-1.41f, -1.41f)
                lineTo(17f, 20.17f)
                verticalLineTo(2f)
                horizontalLineTo(15f)
                verticalLineTo(20.17f)
                lineToRelative(-7.59f, -7.58f)
                lineTo(6f, 14f)
                lineToRelative(10f, 10f)
                close()
            }
        }.build()
    }

    val ThumbsUp: ImageVector by lazy {
        // Carbon "ThumbsUp" icon (32x32) - simplified clean path
        ImageVector.Builder(
            name = "ThumbsUp",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Handle/wrist block
                moveTo(4f, 14f)
                horizontalLineTo(8f)
                verticalLineTo(28f)
                horizontalLineTo(4f)
                close()
                // Thumb body
                moveTo(28f, 14f)
                horizontalLineTo(18f)
                verticalLineTo(8f)
                curveTo(18f, 6.346f, 16.654f, 5f, 15f, 5f)
                lineTo(9f, 14f)
                verticalLineTo(28f)
                horizontalLineTo(24f)
                curveTo(25.1f, 28f, 26.05f, 27.27f, 26.32f, 26.2f)
                lineTo(28f, 19f)
                curveTo(28.17f, 18.3f, 28.01f, 17.56f, 27.56f, 16.98f)
                curveTo(27.11f, 16.4f, 26.42f, 16.05f, 27f, 16f)
                horizontalLineTo(28f)
                close()
            }
        }.build()
    }

    val ThumbsDown: ImageVector by lazy {
        // Carbon "ThumbsDown" icon (32x32) - simplified clean path (mirrored ThumbsUp)
        ImageVector.Builder(
            name = "ThumbsDown",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Handle/wrist block
                moveTo(28f, 4f)
                horizontalLineTo(24f)
                verticalLineTo(18f)
                horizontalLineTo(28f)
                close()
                // Thumb body
                moveTo(4f, 18f)
                horizontalLineTo(14f)
                verticalLineTo(24f)
                curveTo(14f, 25.654f, 15.346f, 27f, 17f, 27f)
                lineTo(23f, 18f)
                verticalLineTo(4f)
                horizontalLineTo(8f)
                curveTo(6.9f, 4f, 5.95f, 4.73f, 5.68f, 5.8f)
                lineTo(4f, 13f)
                curveTo(3.83f, 13.7f, 3.99f, 14.44f, 4.44f, 15.02f)
                curveTo(4.89f, 15.6f, 5.58f, 15.95f, 5f, 16f)
                horizontalLineTo(4f)
                close()
            }
        }.build()
    }

    val Archive: ImageVector by lazy {
        ImageVector.Builder(
            name = "Archive",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Box base
                moveTo(14f, 19f)
                horizontalLineTo(18f)
                verticalLineTo(17f)
                horizontalLineTo(14f)
                close()
                // Box body
                moveTo(6f, 6f)
                verticalLineTo(26f)
                horizontalLineTo(26f)
                verticalLineTo(6f)
                close()
                moveTo(24f, 24f)
                horizontalLineTo(8f)
                verticalLineTo(10f)
                horizontalLineTo(24f)
                close()
                // Lid
                moveTo(4f, 2f)
                verticalLineTo(8f)
                horizontalLineTo(28f)
                verticalLineTo(2f)
                close()
                moveTo(26f, 6f)
                horizontalLineTo(6f)
                verticalLineTo(4f)
                horizontalLineTo(26f)
                close()
            }
        }.build()
    }

    val PlayFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "PlayFilled",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(7f, 28f)
                lineTo(7f, 4f)
                lineTo(27f, 16f)
                close()
            }
        }.build()
    }

    val PauseFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "PauseFilled",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Left bar
                moveTo(6f, 4f)
                horizontalLineTo(13f)
                verticalLineTo(28f)
                horizontalLineTo(6f)
                close()
                // Right bar
                moveTo(19f, 4f)
                horizontalLineTo(26f)
                verticalLineTo(28f)
                horizontalLineTo(19f)
                close()
            }
        }.build()
    }

    val Star: ImageVector by lazy {
        ImageVector.Builder(
            name = "Star",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Carbon star (5-pointed) path
                moveTo(16f, 2f)
                lineTo(20.12f, 11.16f)
                lineTo(30f, 12.26f)
                lineTo(23f, 18.74f)
                lineTo(24.94f, 28.56f)
                lineTo(16f, 23.54f)
                lineTo(7.06f, 28.56f)
                lineTo(9f, 18.74f)
                lineTo(2f, 12.26f)
                lineTo(11.88f, 11.16f)
                close()
            }
        }.build()
    }

    val WifiOff: ImageVector by lazy {
        ImageVector.Builder(
            name = "WifiOff",
            defaultWidth = ICON_SIZE,
            defaultHeight = ICON_SIZE,
            viewportWidth = VIEWPORT_SIZE,
            viewportHeight = VIEWPORT_SIZE,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Diagonal slash
                moveTo(5f, 3.59f)
                lineTo(3.59f, 5f)
                lineTo(27f, 28.41f)
                lineTo(28.41f, 27f)
                close()
                // Dot at center bottom
                moveTo(16f, 23f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 18f, 25f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 27f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 14f, 25f)
                arcTo(Radius.SMALL, Radius.SMALL, Arc.NO_ROTATION, false, true, 16f, 23f)
                close()
                // Small arc (ring 1)
                moveTo(10.76f, 19.83f)
                lineTo(12.18f, 21.24f)
                arcTo(5.5f, 5.5f, Arc.NO_ROTATION, false, true, 19.83f, 21.24f)
                lineTo(21.24f, 19.83f)
                arcTo(7.53f, 7.53f, Arc.NO_ROTATION, false, false, 10.76f, 19.83f)
                close()
                // Medium arc (ring 2) — only right portion visible after slash
                moveTo(22.64f, 18.43f)
                lineTo(24.06f, 17.02f)
                arcTo(11.5f, 11.5f, Arc.NO_ROTATION, false, false, 21.68f, 13.42f)
                lineTo(20.22f, 14.88f)
                arcTo(9.47f, 9.47f, Arc.NO_ROTATION, false, true, 22.64f, 18.43f)
                close()
                // Large arc (ring 3) — right portion
                moveTo(26.85f, 14.21f)
                lineTo(28.27f, 12.79f)
                arcTo(17.5f, 17.5f, Arc.NO_ROTATION, false, false, 22.31f, 8.43f)
                lineTo(20.87f, 9.87f)
                arcTo(15.5f, 15.5f, Arc.NO_ROTATION, false, true, 26.85f, 14.21f)
                close()
                // Large arc left portion
                moveTo(7.55f, 18.43f)
                arcTo(9.44f, 9.44f, Arc.NO_ROTATION, false, true, 9.97f, 14.88f)
                lineTo(8.52f, 13.42f)
                arcTo(11.47f, 11.47f, Arc.NO_ROTATION, false, false, 5.74f, 17.02f)
                close()
                moveTo(5.14f, 14.21f)
                arcTo(15.48f, 15.48f, Arc.NO_ROTATION, false, true, 11.13f, 9.87f)
                lineTo(9.69f, 8.43f)
                arcTo(17.47f, 17.47f, Arc.NO_ROTATION, false, false, 3.73f, 12.79f)
                close()
            }
        }.build()
    }
}
