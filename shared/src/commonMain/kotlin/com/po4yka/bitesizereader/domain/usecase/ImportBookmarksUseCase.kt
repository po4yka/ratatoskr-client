package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.dto.ImportJobDto
import com.po4yka.bitesizereader.domain.repository.ImportExportRepository
import org.koin.core.annotation.Factory

@Factory
class ImportBookmarksUseCase(private val repository: ImportExportRepository) {
    suspend operator fun invoke(
        fileBytes: ByteArray,
        fileName: String,
        summarize: Boolean = false,
        createTags: Boolean = true,
        targetCollectionId: Int? = null,
        skipDuplicates: Boolean = true,
    ): ImportJobDto = repository.importBookmarks(
        fileBytes = fileBytes,
        fileName = fileName,
        summarize = summarize,
        createTags = createTags,
        targetCollectionId = targetCollectionId,
        skipDuplicates = skipDuplicates,
    )
}
