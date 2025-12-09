package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import com.po4yka.bitesizereader.data.remote.SystemApi
import com.po4yka.bitesizereader.domain.repository.SystemRepository
import kotlinx.coroutines.flow.Flow

class SystemRepositoryImpl(
    private val api: SystemApi,
) : SystemRepository {
    override suspend fun downloadDatabase(outputFile: String): Flow<DownloadProgress> {
        return api.downloadDatabase(outputFile)
    }
}
