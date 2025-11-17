package com.po4yka.bitesizereader.util.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Desktop stub implementation of NetworkMonitor for Compose Hot Reload development
 * Always reports network as available (for development only)
 */
class DesktopNetworkMonitor : NetworkMonitor {
    private val _isConnected = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean> = _isConnected

    override fun startMonitoring() {
        // No-op for desktop
    }

    override fun stopMonitoring() {
        // No-op for desktop
    }
}
