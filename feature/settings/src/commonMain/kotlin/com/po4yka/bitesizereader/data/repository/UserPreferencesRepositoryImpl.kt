package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.mappers.toDomain
import com.po4yka.bitesizereader.data.remote.UserPreferencesApi
import com.po4yka.bitesizereader.data.remote.dto.CreateGoalRequestDto
import com.po4yka.bitesizereader.data.remote.dto.UpdatePreferencesRequestDto
import com.po4yka.bitesizereader.domain.model.Goal
import com.po4yka.bitesizereader.domain.model.GoalProgress
import com.po4yka.bitesizereader.domain.model.Streak
import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats
import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [UserPreferencesRepository::class])
class UserPreferencesRepositoryImpl(
    private val userPreferencesApi: UserPreferencesApi,
) : UserPreferencesRepository {
    override suspend fun getPreferences(): UserPreferences {
        logger.debug { "Fetching user preferences" }
        val response = userPreferencesApi.getPreferences()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
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
            return requireNotNull(response.data).toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to update preferences")
        }
    }

    override suspend fun getStats(): UserStats {
        logger.debug { "Fetching user stats" }
        val response = userPreferencesApi.getStats()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch user stats")
        }
    }

    override suspend fun getStreak(): Streak {
        logger.debug { "Fetching user streak" }
        val response = userPreferencesApi.getStreak()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch streak")
        }
    }

    override suspend fun getGoals(): List<Goal> {
        logger.debug { "Fetching user goals" }
        val response = userPreferencesApi.getGoals()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).map { it.toDomain() }
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch goals")
        }
    }

    override suspend fun getGoalsProgress(): List<GoalProgress> {
        logger.debug { "Fetching user goals progress" }
        val response = userPreferencesApi.getGoalsProgress()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).map { it.toDomain() }
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to fetch goals progress")
        }
    }

    override suspend fun createGoal(
        goalType: String,
        targetCount: Int,
    ): Goal {
        logger.debug { "Creating goal: type=$goalType, target=$targetCount" }
        val request = CreateGoalRequestDto(goalType = goalType, targetCount = targetCount)
        val response = userPreferencesApi.createGoal(request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw IllegalStateException(response.error?.message ?: "Failed to create goal")
        }
    }
}
