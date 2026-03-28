package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalController
import com.po4yka.bitesizereader.presentation.viewmodel.SettingsViewModel

interface SettingsComponent {
    val viewModel: SettingsViewModel
    val readingGoalController: ReadingGoalController

    fun onDigestClicked()
}
