package com.po4yka.ratatoskr.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.po4yka.ratatoskr.database.Database
import com.po4yka.ratatoskr.util.config.AppConfig

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, AppConfig.Database.NAME)
    }
}
