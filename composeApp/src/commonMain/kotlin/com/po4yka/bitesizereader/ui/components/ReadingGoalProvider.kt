package com.po4yka.bitesizereader.ui.components

import androidx.compose.runtime.staticCompositionLocalOf
import com.po4yka.bitesizereader.presentation.viewmodel.ReadingGoalViewModel

val LocalReadingGoalViewModel =
    staticCompositionLocalOf<ReadingGoalViewModel> {
        error("ReadingGoalViewModel was not provided")
    }
