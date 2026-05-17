package com.po4yka.ratatoskr.presentation.viewmodel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

/**
 * Regression test for the lost-update bug fixed in
 * docs/tasks/issues/adopt-state-update-convention-in-viewmodels.md.
 *
 * The naive `_state.value = _state.value.copy(...)` pattern silently drops one
 * of the writes when two coroutines interleave their read-modify-write between
 * separate fields. `_state.update { it.copy(...) }` uses a CAS loop and
 * therefore preserves both writes.
 */
class StateUpdateSemanticsTest {
    private data class TwoField(
        val a: Int = 0,
        val b: Int = 0,
    )

    @Test
    fun `update preserves concurrent writes to disjoint fields`() =
        runTest {
            val flow = MutableStateFlow(TwoField())
            val iterations = 5_000

            withContext(Dispatchers.Default) {
                val writerA =
                    async {
                        repeat(iterations) { flow.update { it.copy(a = it.a + 1) } }
                    }
                val writerB =
                    async {
                        repeat(iterations) { flow.update { it.copy(b = it.b + 1) } }
                    }
                listOf(writerA, writerB).awaitAll()
            }

            assertEquals(iterations, flow.value.a, "writerA increments must all be retained")
            assertEquals(iterations, flow.value.b, "writerB increments must all be retained")
        }
}
