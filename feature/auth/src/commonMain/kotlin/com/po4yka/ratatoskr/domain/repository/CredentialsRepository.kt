package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.DeveloperCredentials

interface CredentialsRepository {
    suspend fun saveCredentials(credentials: DeveloperCredentials)

    suspend fun getCredentials(): DeveloperCredentials?

    suspend fun clearCredentials()
}
