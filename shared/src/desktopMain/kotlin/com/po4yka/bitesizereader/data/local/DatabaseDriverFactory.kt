package com.po4yka.bitesizereader.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.po4yka.bitesizereader.database.Database

/**
 * Desktop implementation of DatabaseDriverFactory for Compose Hot Reload
 * Uses JDBC SQLite driver for desktop JVM
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}
