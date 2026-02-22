package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.domain.repository.SummaryRepository
import org.koin.core.annotation.Factory

@Factory
class ArchiveSummaryUseCase(
    private val summaryRepository: SummaryRepository,
) {
    suspend operator fun invoke(id: String, archive: Boolean = true) {
        if (archive) {
            summaryRepository.archiveSummary(id)
        } else {
            summaryRepository.unarchiveSummary(id)
        }
    }
}
