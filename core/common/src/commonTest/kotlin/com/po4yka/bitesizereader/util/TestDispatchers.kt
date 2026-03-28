package com.po4yka.bitesizereader.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

/**
 * Test coroutine dispatcher utilities
 */
@OptIn(ExperimentalCoroutinesApi::class)
object TestDispatchers {
    /**
     * Standard test dispatcher for most tests
     */
    val standard: TestDispatcher = StandardTestDispatcher()

    /**
     * Unconfined test dispatcher for immediate execution
     */
    val unconfined: TestDispatcher = UnconfinedTestDispatcher()

    /**
     * Set the main dispatcher for tests
     */
    fun setMain(dispatcher: CoroutineDispatcher = standard) {
        Dispatchers.setMain(dispatcher)
    }

    /**
     * Reset the main dispatcher after tests
     */
    fun resetMain() {
        Dispatchers.resetMain()
    }
}
