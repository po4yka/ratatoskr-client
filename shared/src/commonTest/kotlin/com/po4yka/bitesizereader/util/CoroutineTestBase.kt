package com.po4yka.bitesizereader.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Base class for tests that use coroutines
 */
@OptIn(ExperimentalCoroutinesApi::class)
abstract class CoroutineTestBase {
    @BeforeTest
    fun setupDispatchers() {
        Dispatchers.setMain(TestDispatchers.standard)
    }

    @AfterTest
    fun tearDownDispatchers() {
        Dispatchers.resetMain()
    }
}
