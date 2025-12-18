package com.po4yka.bitesizereader.presentation.state

/**
 * Filter for article read status.
 */
enum class ReadFilter {
    ALL,
    UNREAD,
    READ,
}

/**
 * Sort order for article list.
 */
enum class SortOrder {
    NEWEST,
    OLDEST,
    ALPHABETICAL,
}

/**
 * Layout mode for article display.
 */
enum class LayoutMode {
    LIST,
    GRID,
}

/**
 * View density for article cards.
 */
enum class ViewDensity {
    COMPACT,
    COMFORTABLE,
}
