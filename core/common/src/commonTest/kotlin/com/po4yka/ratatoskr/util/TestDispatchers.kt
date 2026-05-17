package com.po4yka.ratatoskr.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Test coroutine dispatcher factories.
 *
 * `StandardTestDispatcher` and `UnconfinedTestDispatcher` carry an
 * internal pending-task queue, so they MUST NOT be shared across test
 * methods — pending coroutines from one test can leak into another and
 * cause flaky failures or hangs when tests run in parallel.
 *
 * Always call [newStandard] or [newUnconfined] to obtain a fresh
 * instance per invocation; never cache a dispatcher at object scope.
 */
@OptIn(ExperimentalCoroutinesApi::class)
object TestDispatchers {
    /** Construct a fresh [StandardTestDispatcher]. */
    fun newStandard(): TestDispatcher = StandardTestDispatcher()

    /** Construct a fresh [UnconfinedTestDispatcher]. */
    fun newUnconfined(): TestDispatcher = UnconfinedTestDispatcher()

    /** Install [dispatcher] (or a fresh standard one) as the Main dispatcher. */
    fun setMain(dispatcher: CoroutineDispatcher = newStandard()) {
        Dispatchers.setMain(dispatcher)
    }

    /** Reset the Main dispatcher after tests. */
    fun resetMain() {
        Dispatchers.resetMain()
    }
}
