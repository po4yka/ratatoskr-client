package com.po4yka.ratatoskr.util.error

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class RunCatchingDomainTest {
    @Test
    fun `success path returns success result with block return value`() =
        runTest {
            val result = runCatchingDomain { 42 }
            assertTrue(result.isSuccess)
            assertEquals(42, result.getOrNull())
        }

    @Test
    fun `arbitrary exception is captured as failure rather than propagated`() =
        runTest {
            val boom = IllegalStateException("kapow")
            val result = runCatchingDomain<Unit> { throw boom }
            assertTrue(result.isFailure)
            assertEquals(boom, result.exceptionOrNull())
        }

    @Test
    fun `AppError subtype is captured as failure preserving the original instance`() =
        runTest {
            val err = AppError.NetworkError()
            val result = runCatchingDomain<Unit> { throw err }
            assertTrue(result.isFailure)
            assertIs<AppError.NetworkError>(result.exceptionOrNull())
        }

    @Test
    fun `CancellationException is rethrown so structured concurrency keeps working`() =
        runTest {
            assertFailsWith<CancellationException> {
                runCatchingDomain<Unit> { throw CancellationException("parent cancelled") }
            }
        }

    @Test
    fun `child coroutine cancellation still propagates through runCatchingDomain`() =
        runTest {
            // Regression guard: an overly-broad runCatching would swallow the CancellationException
            // raised when a sibling coroutine fails, leaving the parent scope blocked.
            var ran = false
            try {
                coroutineScope {
                    val deferred =
                        async {
                            delay(1_000)
                            fail("should have been cancelled")
                        }
                    async {
                        delay(10)
                        runCatchingDomain<Unit> {
                            delay(1_000)
                            ran = true
                        }
                    }
                    deferred.cancel(CancellationException("parent went away"))
                    throw CancellationException("parent went away")
                }
                fail("scope should have surfaced cancellation")
            } catch (_: CancellationException) {
                // expected
            }
            assertEquals(false, ran)
        }
}
