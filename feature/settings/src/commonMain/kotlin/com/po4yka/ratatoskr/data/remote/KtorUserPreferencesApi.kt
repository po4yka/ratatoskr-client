package com.po4yka.ratatoskr.data.remote

import com.po4yka.ratatoskr.data.remote.dto.ApiResponseDto
import com.po4yka.ratatoskr.data.remote.dto.CreateGoalRequestDto
import com.po4yka.ratatoskr.data.remote.dto.GoalDto
import com.po4yka.ratatoskr.data.remote.dto.GoalProgressDto
import com.po4yka.ratatoskr.data.remote.dto.StreakDto
import com.po4yka.ratatoskr.data.remote.dto.UpdatePreferencesRequestDto
import com.po4yka.ratatoskr.data.remote.dto.UserPreferencesDto
import com.po4yka.ratatoskr.data.remote.dto.UserStatsDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.annotation.Single

@Single(binds = [UserPreferencesApi::class])
class KtorUserPreferencesApi(private val client: HttpClient) : UserPreferencesApi {
    override suspend fun getPreferences(): ApiResponseDto<UserPreferencesDto> {
        return client.get("v1/user/preferences").body()
    }

    override suspend fun updatePreferences(request: UpdatePreferencesRequestDto): ApiResponseDto<UserPreferencesDto> {
        return client.patch("v1/user/preferences") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun getStats(): ApiResponseDto<UserStatsDto> {
        return client.get("v1/user/stats").body()
    }

    override suspend fun getStreak(): ApiResponseDto<StreakDto> {
        return client.get("v1/user/streak").body()
    }

    override suspend fun getGoals(): ApiResponseDto<List<GoalDto>> {
        return client.get("v1/user/goals").body()
    }

    override suspend fun getGoalsProgress(): ApiResponseDto<List<GoalProgressDto>> {
        return client.get("v1/user/goals/progress").body()
    }

    override suspend fun createGoal(request: CreateGoalRequestDto): ApiResponseDto<GoalDto> {
        return client.post("v1/user/goals") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
