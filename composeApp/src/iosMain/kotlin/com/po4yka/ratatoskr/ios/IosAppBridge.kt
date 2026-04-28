package com.po4yka.ratatoskr.ios

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.po4yka.ratatoskr.app.AppCompositionRoot
import com.po4yka.ratatoskr.feature.sync.domain.usecase.SyncDataUseCase
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.presentation.navigation.RootComponent

/**
 * Swift-facing bridge that owns the app lifecycle and root component on iOS.
 */
class IosAppBridge(
    private val compositionRoot: AppCompositionRoot,
    private val syncDataUseCase: SyncDataUseCase,
    private val snapshotPublisher: RecentSummariesSnapshotPublisher,
) {
    private val lifecycle = LifecycleRegistry().apply { onCreate() }

    val rootComponent: RootComponent = compositionRoot.createRoot(DefaultComponentContext(lifecycle))

    fun onStart() {
        lifecycle.onStart()
    }

    fun onResume() {
        lifecycle.onResume()
    }

    fun onPause() {
        lifecycle.onPause()
    }

    fun onStop() {
        lifecycle.onStop()
    }

    fun onDestroy() {
        lifecycle.onDestroy()
    }

    fun open(route: AppRoute) {
        rootComponent.open(route)
    }

    suspend fun runBackgroundSync(forceFull: Boolean = false) {
        syncDataUseCase(forceFull = forceFull)
    }

    suspend fun refreshRecentSummaries(limit: Int = 3): Int = snapshotPublisher.publish(limit)
}
