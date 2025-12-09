package com.po4yka.bitesizereader.domain.model

import kotlin.time.Instant

data class SyncState(
    val lastSyncTime: Instant?,
    val lastSyncHash: String?,
)
