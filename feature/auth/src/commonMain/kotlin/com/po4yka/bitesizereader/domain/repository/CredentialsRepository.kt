package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.DeveloperCredentials

interface CredentialsRepository {
    suspend fun saveCredentials(credentials: DeveloperCredentials)

    suspend fun getCredentials(): DeveloperCredentials?

    suspend fun clearCredentials()
}
