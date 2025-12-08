package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.data.remote.DownloadProgress
import kotlinx.coroutines.flow.Flow

interface SystemRepository {
    suspend fun downloadDatabase(outputFile: String): Flow<DownloadProgress>
}
