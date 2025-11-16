package com.po4yka.bitesizereader.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.po4yka.bitesizereader.database.Database

/**
 * iOS implementation of database driver factory
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = Database.Schema,
            name = "bite_reader.db",
        )
    }
}
