package com.po4yka.bitesizereader.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
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
    val Bookmark: ImageVector by lazy {
        ImageVector.Builder(
            name = "Bookmark",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcTo(2f, 2f, 0f, false, false, 6f, 4f)
                verticalLineTo(30f)
                lineTo(16f, 25f)
                lineTo(26f, 30f)
                verticalLineTo(4f)
                arcTo(2f, 2f, 0f, false, false, 24f, 2f)
                close()
            }
        }.build()
    }

    val Folder: ImageVector by lazy {
        ImageVector.Builder(
            name = "Folder",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcTo(2f, 2f, 0f, false, false, 2f, 6f)
                verticalLineTo(26f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
                verticalLineTo(10f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, -2f)
                horizontalLineTo(16f)
                lineTo(12.59f, 4.59f)
                arcTo(2f, 2f, 0f, false, false, 11.17f, 4f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            // Gear outline
            path(fill = SolidColor(Color.Black)) {
                moveTo(27f, 16.76f)
                curveToRelative(0f, -0.25f, 0f, -0.5f, 0f, -0.76f)
                reflectiveCurveToRelative(0f, -0.51f, 0f, -0.77f)
                lineToRelative(1.92f, -1.68f)
                arcTo(2f, 2f, 0f, false, false, 29.3f, 11f)
                lineTo(26.94f, 7f)
                arcToRelative(2f, 2f, 0f, false, false, -1.73f, -1f)
                arcToRelative(2f, 2f, 0f, false, false, -0.64f, 0.1f)
                lineToRelative(-2.43f, 0.82f)
                arcToRelative(11.35f, 11.35f, 0f, false, false, -1.31f, -0.75f)
                lineToRelative(-0.51f, -2.52f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, -1.61f)
                horizontalLineTo(13.64f)
                arcToRelative(2f, 2f, 0f, false, false, -2f, 1.61f)
                lineToRelative(-0.51f, 2.52f)
                arcToRelative(11.48f, 11.48f, 0f, false, false, -1.32f, 0.75f)
                lineTo(7.43f, 6.06f)
                arcTo(2f, 2f, 0f, false, false, 6.79f, 6f)
                arcTo(2f, 2f, 0f, false, false, 5.06f, 7f)
                lineTo(2.7f, 11f)
                arcToRelative(2f, 2f, 0f, false, false, 0.41f, 2.51f)
                lineTo(5f, 15.24f)
                curveToRelative(0f, 0.25f, 0f, 0.5f, 0f, 0.76f)
                reflectiveCurveToRelative(0f, 0.51f, 0f, 0.77f)
                lineTo(3.11f, 18.45f)
                arcTo(2f, 2f, 0f, false, false, 2.7f, 21f)
                lineTo(5.06f, 25f)
                arcToRelative(2f, 2f, 0f, false, false, 1.73f, 1f)
                arcToRelative(2f, 2f, 0f, false, false, 0.64f, -0.1f)
                lineToRelative(2.43f, -0.82f)
                arcToRelative(11.35f, 11.35f, 0f, false, false, 1.31f, 0.75f)
                lineToRelative(0.51f, 2.52f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 1.61f)
                horizontalLineToRelative(4.72f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -1.61f)
                lineToRelative(0.51f, -2.52f)
                arcToRelative(11.48f, 11.48f, 0f, false, false, 1.32f, -0.75f)
                lineToRelative(2.42f, 0.82f)
                arcToRelative(2f, 2f, 0f, false, false, 0.64f, 0.1f)
                arcToRelative(2f, 2f, 0f, false, false, 1.73f, -1f)
                lineTo(29.3f, 21f)
                arcToRelative(2f, 2f, 0f, false, false, -0.41f, -2.51f)
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
                arcToRelative(6f, 6f, 0f, true, true, 6f, -6f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 2f)
                arcTo(14f, 14f, 0f, true, false, 30f, 16f)
                arcTo(14f, 14f, 0f, false, false, 16f, 2f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(23f, 20f)
                arcToRelative(5f, 5f, 0f, false, false, -3.89f, 1.89f)
                lineTo(11.8f, 17.32f)
                arcToRelative(4.46f, 4.46f, 0f, false, false, 0f, -2.64f)
                lineToRelative(7.31f, -4.57f)
                arcTo(5f, 5f, 0f, true, false, 18f, 7f)
                arcToRelative(4.79f, 4.79f, 0f, false, false, 0.2f, 1.32f)
                lineToRelative(-7.31f, 4.57f)
                arcToRelative(5f, 5f, 0f, true, false, 0f, 6.22f)
                lineToRelative(7.31f, 4.57f)
                arcTo(4.79f, 4.79f, 0f, false, false, 18f, 25f)
                arcToRelative(5f, 5f, 0f, true, false, 5f, -5f)
                close()
                moveTo(23f, 4f)
                arcToRelative(3f, 3f, 0f, true, true, -3f, 3f)
                arcTo(3f, 3f, 0f, false, true, 23f, 4f)
                close()
                moveTo(7f, 19f)
                arcToRelative(3f, 3f, 0f, true, true, 3f, -3f)
                arcTo(3f, 3f, 0f, false, true, 7f, 19f)
                close()
                moveTo(23f, 28f)
                arcToRelative(3f, 3f, 0f, true, true, 3f, -3f)
                arcTo(3f, 3f, 0f, false, true, 23f, 28f)
                close()
            }
        }.build()
    }

    val Close: ImageVector by lazy {
        ImageVector.Builder(
            name = "Close",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineTo(24f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcToRelative(1f, 1f, 0f, false, true, -0.8872f, -1.4614f)
                lineToRelative(13f, -25f)
                arcToRelative(1f, 1f, 0f, false, true, 1.7744f, 0f)
                lineToRelative(13f, 25f)
                arcTo(1f, 1f, 0f, false, true, 29f, 30f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.54f, 2f)
                arcTo(14f, 14f, 0f, false, false, 2f, 16f)
                arcToRelative(4.82f, 4.82f, 0f, false, false, 6.09f, 4.65f)
                lineToRelative(1.12f, -0.31f)
                arcTo(3f, 3f, 0f, false, true, 13f, 23.24f)
                verticalLineTo(27f)
                arcToRelative(3f, 3f, 0f, false, false, 3f, 3f)
                arcTo(14f, 14f, 0f, false, false, 30f, 15.46f)
                arcTo(14.05f, 14.05f, 0f, false, false, 16.54f, 2f)
                close()
                moveTo(24.65f, 24.32f)
                arcTo(11.93f, 11.93f, 0f, false, true, 16f, 28f)
                arcToRelative(1f, 1f, 0f, false, true, -1f, -1f)
                verticalLineTo(23.24f)
                arcToRelative(5f, 5f, 0f, false, false, -5f, -5f)
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
                arcToRelative(2f, 2f, 0f, true, true, -2f, 2f)
                arcTo(2f, 2f, 0f, false, true, 10f, 9f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 7f)
                arcToRelative(2f, 2f, 0f, true, true, -2f, 2f)
                arcTo(2f, 2f, 0f, false, true, 16f, 7f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 9f)
                arcToRelative(2f, 2f, 0f, true, true, -2f, 2f)
                arcTo(2f, 2f, 0f, false, true, 22f, 9f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(25f, 15f)
                arcToRelative(2f, 2f, 0f, true, true, -2f, 2f)
                arcTo(2f, 2f, 0f, false, true, 25f, 15f)
                close()
            }
        }.build()
    }

    val Idea: ImageVector by lazy {
        ImageVector.Builder(
            name = "Idea",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            // Fork
            path(fill = SolidColor(Color.Black)) {
                moveTo(14f, 11f)
                arcToRelative(4f, 4f, 0f, false, true, -8f, 0f)
                verticalLineTo(2f)
                horizontalLineTo(4f)
                verticalLineToRelative(9f)
                arcToRelative(6f, 6f, 0f, false, false, 5f, 5.91f)
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
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcToRelative(2f, 2f, 0f, true, true, 2f, -2f)
                arcTo(2f, 2f, 0f, false, true, 10f, 20f)
                close()
            }
            // Right buttons
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 17f)
                arcToRelative(1f, 1f, 0f, true, true, 1f, -1f)
                arcTo(1f, 1f, 0f, false, true, 22f, 17f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 21f)
                arcToRelative(1f, 1f, 0f, true, true, 1f, -1f)
                arcTo(1f, 1f, 0f, false, true, 22f, 21f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 19f)
                arcToRelative(1f, 1f, 0f, true, true, 1f, -1f)
                arcTo(1f, 1f, 0f, false, true, 20f, 19f)
                close()
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(24f, 19f)
                arcToRelative(1f, 1f, 0f, true, true, 1f, -1f)
                arcTo(1f, 1f, 0f, false, true, 24f, 19f)
                close()
            }
            // Wireless signals
            path(fill = SolidColor(Color.Black)) {
                moveTo(13.75f, 9f)
                lineToRelative(-1.5f, -1.33f)
                arcToRelative(5f, 5f, 0f, false, true, 7.5f, 0f)
                lineTo(18.25f, 9f)
                arcToRelative(3f, 3f, 0f, false, false, -4.5f, 0f)
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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

    val Email: ImageVector by lazy {
        ImageVector.Builder(
            name = "Email",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(28f, 6f)
                horizontalLineTo(4f)
                arcTo(2f, 2f, 0f, false, false, 2f, 8f)
                verticalLineTo(24f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
                verticalLineTo(8f)
                arcTo(2f, 2f, 0f, false, false, 28f, 6f)
                close()
                moveTo(25.8f, 8f)
                lineTo(16f, 14.78f)
                lineTo(6.2f, 8f)
                close()
                moveTo(4f, 24f)
                verticalLineTo(8.91f)
                lineToRelative(11.43f, 7.91f)
                arcToRelative(1f, 1f, 0f, false, false, 1.14f, 0f)
                lineTo(28f, 8.91f)
                verticalLineTo(24f)
                close()
            }
        }.build()
    }

    val Map: ImageVector by lazy {
        ImageVector.Builder(
            name = "Map",
            defaultWidth = 32.dp,
            defaultHeight = 32.dp,
            viewportWidth = 32f,
            viewportHeight = 32f,
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
                arcToRelative(2f, 2f, 0f, true, true, 2f, -2f)
                arcTo(2f, 2f, 0f, false, true, 16f, 11f)
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
                arcToRelative(2f, 2f, 0f, false, false, -2f, 2f)
                verticalLineTo(28f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, 2f)
                horizontalLineTo(28f)
                arcToRelative(2f, 2f, 0f, false, false, 2f, -2f)
                verticalLineTo(14f)
                arcTo(2f, 2f, 0f, false, false, 28f, 12f)
                close()
            }
        }.build()
    }
}
