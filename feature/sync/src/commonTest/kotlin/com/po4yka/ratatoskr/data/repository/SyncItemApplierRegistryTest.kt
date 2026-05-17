package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.remote.dto.SyncItemDto
import com.po4yka.ratatoskr.feature.sync.api.SyncEntity
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.JsonPrimitive

class SyncItemApplierRegistryTest {
    @Test
    fun `dispatches item to matching injected applier`() {
        val applied = mutableListOf<String>()
        val registry =
            SyncItemApplierRegistry(
                appliers =
                    listOf(
                        RecordingSyncItemApplier(SyncEntity.ENTITY_TYPE_SUMMARY, applied),
                        RecordingSyncItemApplier(SyncEntity.ENTITY_TYPE_HIGHLIGHT, applied),
                    ),
            )

        val appliedSuccessfully =
            registry.apply(
                SyncItemDto(
                    id = JsonPrimitive("42"),
                    entityType = SyncEntity.ENTITY_TYPE_HIGHLIGHT,
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

    @Test
    fun `dispatch routes by typed SyncEntity subtype`() {
        var seenSubtype: String? = null
        val registry =
            SyncItemApplierRegistry(
                appliers =
                    listOf(
                        object : SyncItemApplier {
                            override val entityType: String = SyncEntity.ENTITY_TYPE_TAG

                            override fun apply(entity: SyncEntity): Boolean {
                                seenSubtype = entity::class.simpleName
                                return true
                            }
                        },
                    ),
            )

        registry.apply(
            SyncItemDto(
                id = JsonPrimitive("7"),
                entityType = SyncEntity.ENTITY_TYPE_TAG,
            ),
        )

        assertEquals("Tag", seenSubtype)
    }

    private class RecordingSyncItemApplier(
        override val entityType: String,
        private val applied: MutableList<String>,
    ) : SyncItemApplier {
        override fun apply(entity: SyncEntity): Boolean {
            applied += "$entityType:${entity.id}"
            return true
        }
    }
}
