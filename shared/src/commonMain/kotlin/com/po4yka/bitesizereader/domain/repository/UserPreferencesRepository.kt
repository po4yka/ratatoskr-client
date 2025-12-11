package com.po4yka.bitesizereader.domain.repository

import com.po4yka.bitesizereader.domain.model.UserPreferences
import com.po4yka.bitesizereader.domain.model.UserStats

/**
 * Repository for managing user preferences and statistics.
 */
interface UserPreferencesRepository {
    /** Get current user preferences */
    suspend fun getPreferences(): UserPreferences

    /** Update user preferences */
    suspend fun updatePreferences(langPreference: String? = null): UserPreferences

    /** Get user statistics */
    suspend fun getStats(): UserStats
}
