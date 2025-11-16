package com.po4yka.bitesizereader.util.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

/**
 * iOS implementation of NetworkMonitor using Network framework
 */
class IosNetworkMonitor : NetworkMonitor {
    private val _networkStatus = MutableStateFlow(NetworkStatus.UNKNOWN)
    override val networkStatus: Flow<NetworkStatus> = _networkStatus

    private val monitor = nw_path_monitor_create()

    init {
        setupMonitor()
    }

    private fun setupMonitor() {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status =
                if (path != null && nw_path_status_satisfied(path)) {
                    NetworkStatus.CONNECTED
                } else {
                    NetworkStatus.DISCONNECTED
                }
            _networkStatus.value = status
        }

        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
    }

    override suspend fun isConnected(): Boolean {
        return _networkStatus.value == NetworkStatus.CONNECTED
    }

    fun cancel() {
        nw_path_monitor_cancel(monitor)
    }
}
