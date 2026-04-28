package com.po4yka.ratatoskr.presentation.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.po4yka.ratatoskr.navigation.AppRoute
import com.po4yka.ratatoskr.navigation.RootChildDescriptor

interface RootComponent {
    val childStack: Value<ChildStack<*, RootChildDescriptor>>

    fun open(route: AppRoute)
}
