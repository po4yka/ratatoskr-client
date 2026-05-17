package com.po4yka.ratatoskr.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base class for tests that use coroutines. Each test method gets a
 * freshly constructed [kotlinx.coroutines.test.StandardTestDispatcher]
 * so pending tasks cannot leak between tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class CoroutineTestBase {
    @BeforeTest
    fun setupDispatchers() {
        Dispatchers.setMain(TestDispatchers.newStandard())
    }

    @AfterTest
    fun tearDownDispatchers() {
        Dispatchers.resetMain()
    }
}
