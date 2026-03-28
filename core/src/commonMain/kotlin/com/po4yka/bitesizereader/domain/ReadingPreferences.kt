package com.po4yka.bitesizereader.domain.model

data class ReadingPreferences(
    val fontSizeScale: Float = 1.0f,
    val lineSpacingScale: Float = 1.0f,
) {
    companion object {
        const val MIN_FONT_SCALE = 0.8f
        const val MAX_FONT_SCALE = 1.6f
        const val MIN_LINE_SPACING_SCALE = 1.0f
        const val MAX_LINE_SPACING_SCALE = 2.0f
    }
}
