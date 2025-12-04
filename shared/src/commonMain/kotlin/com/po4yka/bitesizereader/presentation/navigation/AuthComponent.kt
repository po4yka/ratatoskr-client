package com.po4yka.bitesizereader.presentation.navigation

import com.po4yka.bitesizereader.presentation.viewmodel.AuthViewModel
import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface AuthComponent {
    val viewModel: AuthViewModel
    fun onLoginSuccess()
}

class DefaultAuthComponent(
    componentContext: ComponentContext,
    private val onLoginSuccess: () -> Unit
) : AuthComponent, KoinComponent {
    override val viewModel: AuthViewModel by inject()

    override fun onLoginSuccess() {
        onLoginSuccess.invoke()
    }
}