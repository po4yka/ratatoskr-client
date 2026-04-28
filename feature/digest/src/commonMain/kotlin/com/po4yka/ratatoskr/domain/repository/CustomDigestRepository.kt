package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.CustomDigest
import com.po4yka.ratatoskr.domain.model.DigestFormat
import kotlinx.coroutines.flow.Flow

interface CustomDigestRepository {
    suspend fun createDigest(
        title: String,
        summaryIds: List<String>,
        format: DigestFormat,
    ): CustomDigest

    suspend fun getDigest(id: String): CustomDigest?

    fun getDigests(): Flow<List<CustomDigest>>

    suspend fun pollDigestStatus(id: String): CustomDigest

    suspend fun deleteDigest(id: String)
}
