package com.po4yka.ratatoskr.util

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Regression coverage for [TestDispatchers].
 *
 * The previous implementation exposed a single `StandardTestDispatcher()`
 * at `object` level. `StandardTestDispatcher` keeps an internal pending-task
 * queue, so sharing one instance across test methods can leak coroutines
 * from one method into the next when tests are launched in parallel.
 *
 * After this change, `TestDispatchers.newStandard()` and `newUnconfined()`
 * construct a fresh instance per call so each test gets an isolated queue.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatchersIsolationTest {
    @Test
    fun `newStandard returns a distinct dispatcher per call`() {
        val a = TestDispatchers.newStandard()
        val b = TestDispatchers.newStandard()

        assertNotSame(a, b, "Each call must allocate a fresh StandardTestDispatcher")
    }

    @Test
    fun `newUnconfined returns a distinct dispatcher per call`() {
        val a = TestDispatchers.newUnconfined()
        val b = TestDispatchers.newUnconfined()

        assertNotSame(a, b, "Each call must allocate a fresh UnconfinedTestDispatcher")
    }
}
