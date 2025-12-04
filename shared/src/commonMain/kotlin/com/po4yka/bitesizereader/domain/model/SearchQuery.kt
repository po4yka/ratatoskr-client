package com.po4yka.bitesizereader.domain.model

data class SearchQuery(
    val query: String,
    val page: Int = 1,
    val pageSize: Int = 20
)