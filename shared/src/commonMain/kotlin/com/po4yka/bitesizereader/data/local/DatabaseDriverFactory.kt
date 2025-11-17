package com.po4yka.bitesizereader.data.local

import app.cash.sqldelight.db.SqlDriver

/**
 * Expect declaration for platform-specific database driver factory
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
