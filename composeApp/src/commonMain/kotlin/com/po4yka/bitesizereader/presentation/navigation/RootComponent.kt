package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.po4yka.bitesizereader.navigation.AppRoute
import com.po4yka.bitesizereader.navigation.RootChildDescriptor

interface RootComponent {
    val childStack: Value<ChildStack<*, RootChildDescriptor>>

    fun open(route: AppRoute)
}
