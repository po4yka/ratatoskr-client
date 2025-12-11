package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.UserPreferencesApi
import com.po4yka.bitesizereader.data.remote.dto.UpdatePreferencesRequestDto
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats
import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single
class UserPreferencesRepositoryImpl(
    private val userPreferencesApi: UserPreferencesApi,
) : UserPreferencesRepository {
    override suspend fun getPreferences(): UserPreferences {
        logger.debug { "Fetching user preferences" }
        val response = userPreferencesApi.getPreferences()
        if (response.success && response.data != null) {
            return response.data.toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch preferences")
        }
    }

    override suspend fun updatePreferences(langPreference: String?): UserPreferences {
        logger.debug { "Updating user preferences: langPreference=$langPreference" }
        val request =
            UpdatePreferencesRequestDto(
                langPreference = langPreference,
            )
        val response = userPreferencesApi.updatePreferences(request)
        if (response.success && response.data != null) {
            return response.data.toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to update preferences")
        }
    }

    override suspend fun getStats(): UserStats {
        logger.debug { "Fetching user stats" }
        val response = userPreferencesApi.getStats()
        if (response.success && response.data != null) {
            return response.data.toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch user stats")
        }
    }
}
