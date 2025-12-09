package com.po4yka.bitesizereader.util.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Desktop stub implementation of NetworkMonitor for Compose Hot Reload development
 * Always reports network as available (for development only)
 */
class DesktopNetworkMonitor : NetworkMonitor {
    private val _networkStatus = MutableStateFlow(NetworkStatus.CONNECTED)
    override val networkStatus: Flow<NetworkStatus> = _networkStatus

    override suspend fun isConnected(): Boolean = true
}
