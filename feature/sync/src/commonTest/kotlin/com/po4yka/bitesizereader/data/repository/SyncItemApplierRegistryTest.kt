package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.dto.SyncItemDto
import com.po4yka.bitesizereader.feature.sync.api.SyncItemApplier
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyncItemApplierRegistryTest {
    @Test
    fun `dispatches item to matching injected applier`() {
        val applied = mutableListOf<String>()
        val registry =
            SyncItemApplierRegistry(
                appliers =
                    listOf(
                        RecordingSyncItemApplier("summary", applied),
                        RecordingSyncItemApplier("highlight", applied),
                    ),
            )

        val appliedSuccessfully =
            registry.apply(
                SyncItemDto(
                    id = JsonPrimitive("42"),
                    entityType = "highlight",
                ),
            )

        assertTrue(appliedSuccessfully)
        assertEquals(listOf("highlight:42"), applied)
    }

    @Test
    fun `unknown entity types are ignored`() {
        val registry = SyncItemApplierRegistry(appliers = emptyList())

        val appliedSuccessfully =
            registry.apply(
                SyncItemDto(
                    id = JsonPrimitive("99"),
                    entityType = "serverOnly",
                ),
            )

        assertTrue(appliedSuccessfully)
    }

    private class RecordingSyncItemApplier(
        override val entityType: String,
        private val applied: MutableList<String>,
    ) : SyncItemApplier {
        override fun apply(item: SyncItemDto): Boolean {
            applied += "$entityType:${item.idAsString}"
            return true
        }
    }
}
