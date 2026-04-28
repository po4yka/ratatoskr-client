package com.po4yka.ratatoskr.domain.repository

import com.po4yka.ratatoskr.domain.model.ReadingPreferences
import kotlinx.coroutines.flow.Flow

interface ReadingPreferencesRepository {
    fun getPreferences(): Flow<ReadingPreferences>

    suspend fun updateFontSizeScale(scale: Float)

    suspend fun updateLineSpacingScale(scale: Float)
}
