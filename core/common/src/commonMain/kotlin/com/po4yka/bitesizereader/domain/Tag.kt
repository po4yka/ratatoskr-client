package com.po4yka.bitesizereader.domain.model

data class Tag(
    val id: Int,
    val name: String,
    val color: String?,
    val summaryCount: Int,
    val createdAt: String,
    val updatedAt: String,
)
