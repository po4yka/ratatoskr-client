package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.UserApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.UpdatePreferencesRequest
import com.po4yka.ratatoskr.api.generated.models.V1UserGoalsRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.data.remote.dto.GoalDto
import com.po4yka.ratatoskr.data.remote.dto.GoalProgressDto
import com.po4yka.ratatoskr.data.remote.dto.StreakDto
import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.koin.core.annotation.Single

private val parserJson = Json { ignoreUnknownKeys = true }

@Single(binds = [UserPreferencesRepository::class])
class UserPreferencesRepositoryImpl : UserPreferencesRepository {

    override suspend fun getPreferences(): UserPreferences {
        val envelope = UserApi.getUserPreferencesV1UserPreferencesGet().unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no preferences data" }.toDomain()
    }

    override suspend fun updatePreferences(langPreference: String?): UserPreferences {
        val langEnum = langPreference?.let { raw ->
            UpdatePreferencesRequest.LangPreference.entries.firstOrNull {
                it.name.equals(raw, ignoreCase = true)
            }
        }
        val envelope = UserApi.updateUserPreferencesV1UserPreferencesPatch(
            body = UpdatePreferencesRequest(langPreference = langEnum),
        ).unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no preferences data" }.toDomain()
    }

    override suspend fun getStats(): UserStats {
        val envelope = UserApi.getUserStatsV1UserStatsGet().unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no stats data" }.toDomain()
    }

    override suspend fun getStreak(): Streak {
        val element = UserApi.getStreakV1UserStreakGet().unwrap()
        return parserJson.decodeFromJsonElement<StreakDto>(element).toDomain()
    }

    override suspend fun getGoals(): List<Goal> {
        val element = UserApi.listGoalsV1UserGoalsGet().unwrap()
        return parserJson.decodeFromJsonElement<List<GoalDto>>(element).map { it.toDomain() }
    }

    override suspend fun getGoalsProgress(): List<GoalProgress> {
        val element = UserApi.getGoalProgressV1UserGoalsProgressGet().unwrap()
        return parserJson.decodeFromJsonElement<List<GoalProgressDto>>(element).map { it.toDomain() }
    }

    override suspend fun createGoal(goalType: String, targetCount: Int): Goal {
        val goalTypeEnum = V1UserGoalsRequest.GoalType.entries.firstOrNull {
            it.name.equals(goalType, ignoreCase = true)
        } ?: V1UserGoalsRequest.GoalType.DAILY
        val element = UserApi.upsertGoalV1UserGoalsPost(
            body = V1UserGoalsRequest(
                goalType = goalTypeEnum,
                targetCount = targetCount.toLong(),
            ),
        ).unwrap()
        return parserJson.decodeFromJsonElement<GoalDto>(element).toDomain()
    }
}
