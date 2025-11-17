package com.po4yka.bitesizereader.presentation.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base ViewModel with lifecycle management and proper cleanup
 *
 * Provides a scoped CoroutineScope that is automatically cancelled when the ViewModel
 * is cleared, preventing memory leaks and resource waste.
 *
 * Usage:
 * ```kotlin
 * class MyViewModel(...) : BaseViewModel() {
 *     init {
 *         viewModelScope.launch {
 *             // Your code here
 *         }
 *     }
 * }
 * ```
 *
 * Don't forget to call onCleared() when the ViewModel is no longer needed:
 * - Android: In onStop/onDestroy or when navigation leaves the screen
 * - iOS: In the wrapper's deinit
 */
abstract class BaseViewModel {
    /**
     * CoroutineScope tied to this ViewModel's lifecycle
     *
     * Automatically cancelled when onCleared() is called.
     * Uses SupervisorJob so that failure of one coroutine doesn't cancel others.
     */
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Called when the ViewModel is about to be destroyed
     *
     * Cancels all running coroutines and cleans up resources.
     * This method should be called from:
     * - Android: Navigation component's onStop or Activity/Fragment onDestroy
     * - iOS: SwiftUI wrapper's deinit
     */
    open fun onCleared() {
        viewModelScope.cancel()
    }
}
