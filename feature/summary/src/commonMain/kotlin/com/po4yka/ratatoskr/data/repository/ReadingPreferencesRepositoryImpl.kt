package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.domain.model.ReadingPreferences
import com.po4yka.ratatoskr.domain.repository.ReadingPreferencesRepository
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getFloatFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Single

@OptIn(ExperimentalSettingsApi::class)
@Single(binds = [ReadingPreferencesRepository::class])
class ReadingPreferencesRepositoryImpl(
    private val settings: ObservableSettings,
) : ReadingPreferencesRepository {
    override fun getPreferences(): Flow<ReadingPreferences> {
        return combine(
            settings.getFloatFlow(KEY_FONT_SIZE_SCALE, DEFAULT_FONT_SIZE_SCALE),
            settings.getFloatFlow(KEY_LINE_SPACING_SCALE, DEFAULT_LINE_SPACING_SCALE),
        ) { fontSizeScale, lineSpacingScale ->
            ReadingPreferences(
                fontSizeScale = fontSizeScale,
                lineSpacingScale = lineSpacingScale,
            )
        }
    }

    override suspend fun updateFontSizeScale(scale: Float) {
        settings.putFloat(KEY_FONT_SIZE_SCALE, scale)
    }

    override suspend fun updateLineSpacingScale(scale: Float) {
        settings.putFloat(KEY_LINE_SPACING_SCALE, scale)
    }

    companion object {
        private const val KEY_FONT_SIZE_SCALE = "reading_font_size_scale"
        private const val KEY_LINE_SPACING_SCALE = "reading_line_spacing_scale"
        private const val DEFAULT_FONT_SIZE_SCALE = 1.0f
        private const val DEFAULT_LINE_SPACING_SCALE = 1.0f
    }
}
