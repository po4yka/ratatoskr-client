package com.po4yka.ratatoskr.data.repository

import com.po4yka.ratatoskr.data.mappers.toSyncEntity
import com.po4yka.ratatoskr.data.remote.dto.SyncItemDto
import com.po4yka.ratatoskr.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class SyncItemApplierRegistry(
    appliers: List<SyncItemApplier>,
) {
    private val appliersByEntityType = appliers.associateBy { it.entityType }

    fun apply(item: SyncItemDto): Boolean {
        val entity = item.toSyncEntity()
        val applier = appliersByEntityType[entity.entityType]
        if (applier == null) {
            logger.debug { "Skipping entity type not needed on mobile: ${entity.entityType}" }
            return true
        }
        return applier.apply(entity)
    }
}
