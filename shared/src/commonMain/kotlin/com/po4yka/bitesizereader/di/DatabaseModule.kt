package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.DatabaseHelper
import com.po4yka.bitesizereader.database.Database
import org.koin.dsl.module

/**
 * Koin module for database dependencies
 *
 * Uses lazy initialization (createdAtStart = false) to defer database creation
 * until first data access, improving startup performance.
 */
val databaseModule =
    module {
        // Lazy singleton - Database driver (platform-specific)
        single(createdAtStart = false) { get<DatabaseDriverFactory>().createDriver() }

        // Lazy singleton - Database instance
        single(createdAtStart = false) { Database(get()) }

        // Lazy singleton - Database helper
        single(createdAtStart = false) { DatabaseHelper(get()) }
    }
