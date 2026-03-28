package com.po4yka.bitesizereader.util.network

import kotlinx.coroutines.flow.Flow

/**
 * Network connectivity state
 */
enum class NetworkStatus {
    /** Connected to network */
    CONNECTED,

    /** Not connected to network */
    DISCONNECTED,

    /** Connection state unknown */
    UNKNOWN,
}

/**
 * Interface for monitoring network connectivity
 */
interface NetworkMonitor {
    /**
     * Flow of network status changes
     */
    val networkStatus: Flow<NetworkStatus>

    /**
     * Check if currently connected to network
     */
    suspend fun isConnected(): Boolean
}

/**
 * Check if network status indicates connectivity
 */
fun NetworkStatus.isConnected(): Boolean = this == NetworkStatus.CONNECTED
