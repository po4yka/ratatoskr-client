package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.UserPreferencesApi
import com.po4yka.ratatoskr.data.remote.dto.CreateGoalRequestDto
import com.po4yka.ratatoskr.data.remote.dto.UpdatePreferencesRequestDto
import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import com.po4yka.ratatoskr.util.error.AppError
import com.po4yka.ratatoskr.util.error.toAppError
import io.github.oshai.kotlinlogging.KotlinLogging
import org.koin.core.annotation.Single

private val logger = KotlinLogging.logger {}

@Single(binds = [UserPreferencesRepository::class])
class UserPreferencesRepositoryImpl(
    private val userPreferencesApi: UserPreferencesApi,
) : UserPreferencesRepository {
    override suspend fun getPreferences(): UserPreferences {
        val response = userPreferencesApi.getPreferences()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch preferences")
        }
    }

    override suspend fun updatePreferences(langPreference: String?): UserPreferences {
        val request =
            UpdatePreferencesRequestDto(
                langPreference = langPreference,
            )
        val response = userPreferencesApi.updatePreferences(request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to update preferences")
        }
    }

    override suspend fun getStats(): UserStats {
        val response = userPreferencesApi.getStats()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch user stats")
        }
    }

    override suspend fun getStreak(): Streak {
        val response = userPreferencesApi.getStreak()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch streak")
        }
    }

    override suspend fun getGoals(): List<Goal> {
        val response = userPreferencesApi.getGoals()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).map { it.toDomain() }
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch goals")
        }
    }

    override suspend fun getGoalsProgress(): List<GoalProgress> {
        val response = userPreferencesApi.getGoalsProgress()
        if (response.success && response.data != null) {
            return requireNotNull(response.data).map { it.toDomain() }
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to fetch goals progress")
        }
    }

    override suspend fun createGoal(
        goalType: String,
        targetCount: Int,
    ): Goal {
        val request = CreateGoalRequestDto(goalType = goalType, targetCount = targetCount)
        val response = userPreferencesApi.createGoal(request)
        if (response.success && response.data != null) {
            return requireNotNull(response.data).toDomain()
        } else {
            throw response.error?.toAppError() ?: AppError.ServerError(code = 500, fallbackMessage = "Failed to create goal")
        }
    }
}
