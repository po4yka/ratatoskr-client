package com.po4yka.ratatoskr.domain.usecase

import com.po4yka.ratatoskr.domain.repository.ImportExportRepository
import org.koin.core.annotation.Factory

@Factory
class ExportBookmarksUseCase(private val repository: ImportExportRepository) {
    suspend operator fun invoke(
        format: String = "json",
        tag: String? = null,
        collectionId: Int? = null,
    ): ByteArray = repository.exportBookmarks(format = format, tag = tag, collectionId = collectionId)
}
