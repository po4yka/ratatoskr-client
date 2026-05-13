package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.api.generated.api.UserApi
import com.po4yka.ratatoskr.api.generated.bootstrap.unwrap
import com.po4yka.ratatoskr.api.generated.models.UpdatePreferencesRequest
import com.po4yka.ratatoskr.api.generated.models.V1UserGoalsRequest
import com.po4yka.ratatoskr.data.mappers.toDomain
import com.po4yka.ratatoskr.domain.model.Goal
import com.po4yka.ratatoskr.domain.model.GoalProgress
import com.po4yka.ratatoskr.domain.model.Streak
import com.po4yka.ratatoskr.domain.model.UserPreferences
import com.po4yka.ratatoskr.domain.model.UserStats
import com.po4yka.ratatoskr.domain.repository.UserPreferencesRepository
import org.koin.core.annotation.Single

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
        val envelope = UserApi.getStreakV1UserStreakGet().unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no streak data" }.toDomain()
    }

    override suspend fun getGoals(): List<Goal> {
        val envelope = UserApi.listGoalsV1UserGoalsGet().unwrap()
        return envelope.`data`?.goals?.map { it.toDomain() }.orEmpty()
    }

    override suspend fun getGoalsProgress(): List<GoalProgress> {
        val envelope = UserApi.getGoalProgressV1UserGoalsProgressGet().unwrap()
        return envelope.`data`?.progress?.map { it.toDomain() }.orEmpty()
    }

    override suspend fun createGoal(goalType: String, targetCount: Int): Goal {
        val goalTypeEnum = V1UserGoalsRequest.GoalType.entries.firstOrNull {
            it.name.equals(goalType, ignoreCase = true)
        } ?: V1UserGoalsRequest.GoalType.DAILY
        val envelope = UserApi.upsertGoalV1UserGoalsPost(
            body = V1UserGoalsRequest(
                goalType = goalTypeEnum,
                targetCount = targetCount.toLong(),
            ),
        ).unwrap()
        return requireNotNull(envelope.`data`) { "Server returned no goal data" }.toDomain()
    }
}
