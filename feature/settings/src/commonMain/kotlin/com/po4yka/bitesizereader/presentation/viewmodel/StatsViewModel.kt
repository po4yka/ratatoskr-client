package com.po4yka.bitesizereader.presentation.viewmodel

import com.po4yka.bitesizereader.domain.repository.UserPreferencesRepository
import com.po4yka.bitesizereader.domain.usecase.GetUserStatsUseCase
import com.po4yka.bitesizereader.presentation.state.StatsState
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

@Factory
class StatsViewModel(
    private val getUserStats: GetUserStatsUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : BaseViewModel() {
    private val _state = MutableStateFlow(StatsState())
    val state: StateFlow<StatsState> = _state.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, isStreakLoading = true, error = null, streakError = null) }

            // Load reading stats (existing)
            val statsDeferred =
                async {
                    runCatching { getUserStats() }
                }

            // Load streak + goals progress in parallel
            val streakDeferred =
                async {
                    runCatching { userPreferencesRepository.getStreak() }
                }
            val goalsProgressDeferred =
                async {
                    runCatching { userPreferencesRepository.getGoalsProgress() }
                }

            val statsResult = statsDeferred.await()
            val streakResult = streakDeferred.await()
            val goalsResult = goalsProgressDeferred.await()

            _state.update { current ->
                current.copy(
                    isLoading = false,
                    isStreakLoading = false,
                    stats = statsResult.getOrNull() ?: current.stats,
                    error = statsResult.exceptionOrNull()?.message?.takeIf { statsResult.isFailure },
                    streak = streakResult.getOrNull(),
                    goalsProgress = goalsResult.getOrElse { emptyList() },
                    streakError = streakResult.exceptionOrNull()?.message?.takeIf { streakResult.isFailure },
                )
            }
        }
    }
}
