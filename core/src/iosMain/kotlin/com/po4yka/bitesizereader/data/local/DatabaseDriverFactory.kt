package com.po4yka.bitesizereader.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.util.config.AppConfig

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, AppConfig.Database.NAME)
    }
}
