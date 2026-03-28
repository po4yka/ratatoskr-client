package com.po4yka.bitesizereader

import com.po4yka.bitesizereader.app.AppCompositionRoot
import com.po4yka.bitesizereader.feature.summary.navigation.SummaryRoutes
import com.po4yka.bitesizereader.feature.sync.domain.usecase.SyncDataUseCase
import com.po4yka.bitesizereader.ios.IosAppBridge
import com.po4yka.bitesizereader.ios.RecentSummariesSnapshotPublisher
import com.po4yka.bitesizereader.presentation.navigation.RootComponent
import org.koin.mp.KoinPlatform

/**
 * Swift-facing convenience wrapper around [IosAppBridge].
 *
 * Default Kotlin arguments on [IosAppBridge] are not exported to Swift, so the
 * host app instantiates this wrapper instead of wiring bridge dependencies
 * manually on the Swift side.
 */
class IosAppHost {
    private val compositionRoot: AppCompositionRoot = iosCompositionRoot()
    private val koin = KoinPlatform.getKoin()
    private val bridge =
        IosAppBridge(
            compositionRoot = compositionRoot,
            syncDataUseCase = koin.get<SyncDataUseCase>(),
            snapshotPublisher =
                RecentSummariesSnapshotPublisher(
                    getSummariesUseCase = koin.get(),
                ),
        )

    val rootComponent: RootComponent
        get() = bridge.rootComponent

    fun onStart() = bridge.onStart()

    fun onResume() = bridge.onResume()

    fun onPause() = bridge.onPause()

    fun onStop() = bridge.onStop()

    fun onDestroy() = bridge.onDestroy()

    fun openSharedUrl(url: String) = bridge.open(SummaryRoutes.submitUrl(url))

    fun openSummaryDetail(summaryId: String) = bridge.open(SummaryRoutes.detail(summaryId))

    suspend fun runBackgroundSync(forceFull: Boolean = false) = bridge.runBackgroundSync(forceFull)

    suspend fun refreshRecentSummaries(limit: Int = 3): Int = bridge.refreshRecentSummaries(limit)
}
