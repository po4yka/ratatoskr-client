package com.po4yka.bitesizereader.di

import com.po4yka.bitesizereader.data.local.DatabaseDriverFactory
import com.po4yka.bitesizereader.database.Database
import com.po4yka.bitesizereader.domain.model.Summary
import com.po4yka.bitesizereader.database.SummaryEntity
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.adapter.primitive.IntColumnAdapter
import kotlinx.datetime.Instant
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val databaseModule = module {
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        Database(
            driver = driver,
            summaryEntityAdapter = SummaryEntity.Adapter(
                createdAtAdapter = object : ColumnAdapter<Instant, Long> {
                    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
                    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                },
                tagsAdapter = object : ColumnAdapter<List<String>, String> {
                    override fun decode(databaseValue: String): List<String> =
                         if (databaseValue.isBlank()) emptyList() else databaseValue.split(",")
                    override fun encode(value: List<String>): String = value.joinToString(",")
                },
                isReadAdapter = object : ColumnAdapter<Boolean, Long> {
                     override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
                     override fun encode(value: Boolean): Long = if (value) 1L else 0L
                }
            ),
            requestEntityAdapter = com.po4yka.bitesizereader.database.RequestEntity.Adapter(
                createdAtAdapter = object : ColumnAdapter<Instant, Long> {
                    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
                    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                },
                updatedAtAdapter = object : ColumnAdapter<Instant, Long> {
                    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
                    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                }
            ),
            syncMetadataEntityAdapter = com.po4yka.bitesizereader.database.SyncMetadataEntity.Adapter(
                lastSyncTimeAdapter = object : ColumnAdapter<Instant, Long> {
                    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)
                    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
                }
            )
        )
    }
}