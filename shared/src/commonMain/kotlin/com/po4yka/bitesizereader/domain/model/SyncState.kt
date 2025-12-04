package com.po4yka.bitesizereader.domain.model

import kotlinx.datetime.Instant

data class SyncState(
    val lastSyncTime: Instant?,
    val lastSyncHash: String?
)