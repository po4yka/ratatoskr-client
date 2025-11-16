package com.po4yka.bitesizereader.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

/**
 * Common Flow extensions
 */

/**
 * Wraps a Flow to emit loading state before emitting values
 */
fun <T> Flow<T>.withLoadingState(
    onStart: () -> Unit = {},
    onError: (Throwable) -> Unit = {}
): Flow<T> = this
    .onStart { onStart() }
    .catch { error ->
        onError(error)
        throw error
    }
