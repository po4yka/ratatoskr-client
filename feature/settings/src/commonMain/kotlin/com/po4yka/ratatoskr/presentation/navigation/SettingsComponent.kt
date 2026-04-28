package com.po4yka.ratatoskr.presentation.navigation

import com.po4yka.ratatoskr.presentation.viewmodel.ReadingGoalController
import com.po4yka.ratatoskr.presentation.viewmodel.SettingsViewModel

interface SettingsComponent {
    val viewModel: SettingsViewModel
    val readingGoalController: ReadingGoalController

    fun onDigestClicked()
}
