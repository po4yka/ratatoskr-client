package com.po4yka.ratatoskr.feature.sync.domain.usecase

import com.po4yka.ratatoskr.domain.model.SyncProgress
import com.po4yka.ratatoskr.domain.model.SyncResult
import com.po4yka.ratatoskr.domain.model.SyncState
import com.po4yka.ratatoskr.feature.sync.domain.repository.ApplyResult
import com.po4yka.ratatoskr.feature.sync.domain.repository.LocalChange
import com.po4yka.ratatoskr.feature.sync.domain.repository.SyncRepository
import com.po4yka.ratatoskr.util.battery.BatteryStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class RefreshRecentSummariesUseCaseTest {
    private class FakeBatteryStatus(var low: Boolean = false) : BatteryStatus {
        override fun isLow(): Boolean = low
    }

    private class RecordingSyncRepository(
        private val throwOnSync: Throwable? = null,
    ) : SyncRepository {
        var syncCalls: Int = 0
            private set
        var lastForceFull: Boolean? = null
            private set

        override val syncProgress: StateFlow<SyncProgress?> = MutableStateFlow(null)

        override fun cancelSync() { /* not relevant for this use case */ }

        override suspend fun sync(forceFull: Boolean) {
            syncCalls++
            lastForceFull = forceFull
            throwOnSync?.let { throw it }
        }

        override fun getSyncState(): Flow<SyncState> = flowOf()

        override suspend fun createSyncSession(limit: Int?): String = fail("not part of the refresh-recent path")

        override suspend fun fullSync(
            sessionId: String,
            limit: Int?,
        ): SyncResult = fail("forceFull = false should not call fullSync")

        override suspend fun deltaSync(
            sessionId: String,
            since: Long,
            limit: Int?,
            etag: String?,
        ): SyncResult = fail("the use case relies on sync(forceFull = false), not deltaSync directly")

        override suspend fun applyChanges(
            sessionId: String,
            changes: List<LocalChange>,
        ): ApplyResult = fail("not part of the refresh-recent path")
    }

    @Test
    fun `skips when battery is low and never touches the sync repository`() =
        runTest {
            val repo = RecordingSyncRepository()
            val useCase = RefreshRecentSummariesUseCase(repo, FakeBatteryStatus(low = true))

            val outcome = useCase()

            assertEquals(
                RefreshRecentSummariesUseCase.RefreshOutcome.Skipped(
                    RefreshRecentSummariesUseCase.SkipReason.BATTERY_LOW,
                ),
                outcome,
            )
            assertEquals(0, repo.syncCalls)
        }

    @Test
    fun `runs a delta sync when battery is healthy`() =
        runTest {
            val repo = RecordingSyncRepository()
            val useCase = RefreshRecentSummariesUseCase(repo, FakeBatteryStatus(low = false))

            val outcome = useCase()

            assertEquals(RefreshRecentSummariesUseCase.RefreshOutcome.Refreshed, outcome)
            assertEquals(1, repo.syncCalls, "sync should run exactly once")
            assertEquals(false, repo.lastForceFull, "short-cadence top-up must not force a full sync")
        }

    @Test
    fun `wraps a sync failure as Failed preserving the original exception`() =
        runTest {
            val boom = IllegalStateException("simulated 5xx")
            val repo = RecordingSyncRepository(throwOnSync = boom)
            val useCase = RefreshRecentSummariesUseCase(repo, FakeBatteryStatus(low = false))

            val outcome = useCase()

            val failed = assertIs<RefreshRecentSummariesUseCase.RefreshOutcome.Failed>(outcome)
            assertEquals(boom, failed.cause)
            assertEquals(1, repo.syncCalls)
        }

    @Test
    fun `battery check happens before sync is invoked`() =
        runTest {
            // Same guard as the first test, but with an exception-throwing repo to prove the
            // battery check short-circuits *before* the repo would have a chance to fail.
            val repo = RecordingSyncRepository(throwOnSync = IllegalStateException("must never call"))
            val useCase = RefreshRecentSummariesUseCase(repo, FakeBatteryStatus(low = true))

            val outcome = useCase()

            assertTrue(outcome is RefreshRecentSummariesUseCase.RefreshOutcome.Skipped)
            assertEquals(0, repo.syncCalls)
        }
}
