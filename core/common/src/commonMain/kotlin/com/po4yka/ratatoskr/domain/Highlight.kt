package com.po4yka.ratatoskr.domain.model

import kotlin.time.Instant

data class Highlight(
    val id: String,
    val summaryId: String,
    val text: String,
    val nodeOffset: Int,
    val color: HighlightColor,
    val note: String?,
    val createdAt: Instant,
)

enum class HighlightColor(val colorName: String) {
    YELLOW("yellow"),
    GREEN("green"),
    BLUE("blue"),
    PINK("pink"),
}
