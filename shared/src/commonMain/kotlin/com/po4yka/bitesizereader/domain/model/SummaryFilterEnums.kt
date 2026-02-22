package com.po4yka.bitesizereader.domain.model

/**
 * Filter for article read status.
 */
enum class ReadFilter {
    ALL,
    UNREAD,
    READ,
    FAVORITED,
    ARCHIVED,
}

/**
 * Sort order for article list.
 */
enum class SortOrder {
    NEWEST,
    OLDEST,
    ALPHABETICAL,
}
