package com.po4yka.bitesizereader.data.repository

import com.po4yka.bitesizereader.data.remote.dto.SyncItemDto
import com.po4yka.bitesizereader.feature.sync.api.SyncItemApplier
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class SyncItemApplierRegistry(
    appliers: List<SyncItemApplier>,
) {
    private val appliersByEntityType = appliers.associateBy { it.entityType }

    fun apply(item: SyncItemDto): Boolean {
        val applier = appliersByEntityType[item.entityType]
        if (applier == null) {
            logger.debug { "Skipping entity type not needed on mobile: ${item.entityType}" }
            return true
        }
        return applier.apply(item)
    }
}
