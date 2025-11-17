package com.po4yka.bitesizereader.util.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Android implementation of NetworkMonitor using ConnectivityManager
 */
class AndroidNetworkMonitor(private val context: Context) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val networkStatus: Flow<NetworkStatus> =
        callbackFlow {
            val callback =
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        trySend(NetworkStatus.CONNECTED)
                    }

                    override fun onLost(network: Network) {
                        trySend(NetworkStatus.DISCONNECTED)
                    }

                    override fun onUnavailable() {
                        trySend(NetworkStatus.DISCONNECTED)
                    }
                }

            val request =
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()

            connectivityManager.registerNetworkCallback(request, callback)

            // Send initial state
            trySend(if (isConnectedNow()) NetworkStatus.CONNECTED else NetworkStatus.DISCONNECTED)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }

    override suspend fun isConnected(): Boolean = isConnectedNow()

    private fun isConnectedNow(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
