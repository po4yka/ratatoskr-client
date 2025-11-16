package com.po4yka.bitesizereader.domain.model

/**
 * Domain model for search queries.
 */
data class SearchQuery(
    val query: String,
    val filters: SearchFilters = SearchFilters()
)

/**
 * Search filters
 */
data class SearchFilters(
    val isRead: Boolean? = null,
    val lang: String? = null,
    val topicTags: List<String> = emptyList(),
    val fromDate: String? = null,
    val toDate: String? = null,
    val sortBy: SortField = SortField.CREATED_AT,
    val sortOrder: SortOrder = SortOrder.DESC
)

enum class SortField {
    CREATED_AT,
    READING_TIME,
    TITLE
}

enum class SortOrder {
    ASC,
    DESC
}
