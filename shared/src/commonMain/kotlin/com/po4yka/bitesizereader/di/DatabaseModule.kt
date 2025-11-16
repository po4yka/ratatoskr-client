package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.data.local.DatabaseHelper
import com.po4yka.bitesizereader.database.Database
import org.koin.dsl.module

/**
 * Koin module for database dependencies
 */
val databaseModule = module {
    // Database driver (platform-specific)
    single { get<DatabaseDriverFactory>().createDriver() }

    // Database instance
    single { Database(get()) }

    // Database helper
    single { DatabaseHelper(get()) }
}
