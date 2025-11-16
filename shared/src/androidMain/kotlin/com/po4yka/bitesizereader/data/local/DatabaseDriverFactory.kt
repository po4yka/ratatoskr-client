package com.po4yka.bitesizereader.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.po4yka.bitesizereader.database.Database

/**
 * Android implementation of database driver factory
 */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "bite_reader.db"
        )
    }
}
