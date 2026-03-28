package com.po4yka.bitesizereader.presentation.viewmodel

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base ViewModel with lifecycle management integrated with Decompose's InstanceKeeper.
 *
 * Implements [InstanceKeeper.Instance] to properly integrate with Decompose navigation,
 * allowing ViewModels to survive configuration changes on Android and be properly
 * cleaned up when components are destroyed.
 *
 * ViewModels should be created using `retainedInstance { }` in components:
 * ```kotlin
 * class DefaultMyComponent(
 *     componentContext: ComponentContext,
 * ) : MyComponent, ComponentContext by componentContext, KoinComponent {
 *     override val viewModel = retainedInstance {
 *         getKoin().get<MyViewModel>()
 *     }
 * }
 * ```
 *
 * The ViewModel's coroutines are automatically cancelled when the component is destroyed.
 *
 * @param dispatcher The dispatcher to use for viewModelScope. Defaults to [Dispatchers.Default]
 *                   for KMP compatibility. In tests, pass a [TestDispatcher] for deterministic behavior.
 */
abstract class BaseViewModel(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : InstanceKeeper.Instance {
    /**
     * CoroutineScope tied to this ViewModel's lifecycle.
     *
     * Automatically cancelled when onDestroy() is called by InstanceKeeper.
     * Uses SupervisorJob so that failure of one coroutine doesn't cancel others.
     * Uses the provided dispatcher (defaults to Dispatchers.Default for KMP compatibility).
     */
    protected val viewModelScope = CoroutineScope(SupervisorJob() + dispatcher)

    /**
     * Called by InstanceKeeper when the component is destroyed.
     *
     * Cancels all running coroutines and cleans up resources.
     * This is called automatically by Decompose when the component lifecycle ends.
     */
    override fun onDestroy() {
        viewModelScope.cancel()
    }
}
