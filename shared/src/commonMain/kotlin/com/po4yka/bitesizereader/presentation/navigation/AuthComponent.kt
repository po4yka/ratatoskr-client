package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.retainedInstance
import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface AuthComponent {
    val viewModel: AuthViewModel

    fun onLoginSuccess()
}

class DefaultAuthComponent(
    componentContext: ComponentContext,
    private val onLoginSuccessCallback: () -> Unit,
) : AuthComponent, ComponentContext by componentContext, KoinComponent {
    override val viewModel: AuthViewModel = retainedInstance { get() }

    override fun onLoginSuccess() {
        onLoginSuccessCallback()
    }
}
