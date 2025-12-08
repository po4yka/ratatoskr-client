package com.po4yka.bitesizereader.domain

import com.po4yka.bitesizereader.grpc.processing.ProcessingUpdate
import kotlinx.coroutines.flow.Flow

interface ProcessingService {
    fun submitUrl(url: String, language: String? = null, forceRefresh: Boolean = false): Flow<ProcessingUpdate>
}
