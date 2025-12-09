package com.po4yka.bitesizereader.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.util.config.AppConfig

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, AppConfig.Database.NAME)
    }
}
