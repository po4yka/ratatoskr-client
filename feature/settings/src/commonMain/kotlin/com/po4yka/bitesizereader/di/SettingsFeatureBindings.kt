package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel
import com.po4yka.bitesizereader.presentation.viewmodel.StatsViewModel
import org.koin.dsl.module

val settingsFeatureBindingsModule =
    module {
        factory {
            SettingsViewModel(
                telegramDelegate = get(),
                syncDelegate = get(),
                accountDelegate = get(),
            )
        }
        factory {
            ReadingGoalViewModel(
                getReadingGoalProgressUseCase = get(),
                updateReadingGoalUseCase = get(),
                recalculateStreakUseCase = get(),
            )
        }
        factory {
            StatsViewModel(
                getUserStats = get(),
                userPreferencesRepository = get(),
            )
        }
    }
