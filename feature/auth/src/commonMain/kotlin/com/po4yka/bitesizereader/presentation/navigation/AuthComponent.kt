package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel

interface AuthComponent {
    val viewModel: AuthViewModel

    fun onLoginSuccess()
}

class DefaultAuthComponent(
    componentContext: ComponentContext,
    private val viewModelFactory: () -> AuthViewModel,
    private val onLoginSuccessCallback: () -> Unit,
) : AuthComponent, ComponentContext by componentContext {
    override val viewModel: AuthViewModel = retainedInstance { viewModelFactory() }

    override fun onLoginSuccess() {
        onLoginSuccessCallback()
    }
}
