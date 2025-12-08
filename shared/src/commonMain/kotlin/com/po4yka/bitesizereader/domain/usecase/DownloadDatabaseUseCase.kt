package com.po4yka.bitesizereader.domain.usecase

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import com.po4yka.bitesizereader.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow

class DownloadDatabaseUseCase(
    private val repository: SystemRepository
) {
    suspend operator fun invoke(outputFile: String): Flow<DownloadProgress> {
        return repository.downloadDatabase(outputFile)
    }
}
