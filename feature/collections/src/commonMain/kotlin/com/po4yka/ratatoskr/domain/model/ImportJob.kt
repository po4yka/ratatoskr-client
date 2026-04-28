package com.po4yka.ratatoskr.domain.model

data class ImportJob(
    val id: Int,
    val sourceFormat: String,
    val fileName: String? = null,
    val status: String,
    val totalItems: Int,
    val processedItems: Int,
    val createdItems: Int,
    val skippedItems: Int,
    val failedItems: Int,
    val errors: List<String> = emptyList(),
    val createdAt: String,
    val updatedAt: String,
)
