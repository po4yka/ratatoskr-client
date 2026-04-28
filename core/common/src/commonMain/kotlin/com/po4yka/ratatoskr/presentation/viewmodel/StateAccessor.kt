package com.po4yka.ratatoskr.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface StateAccessor<T> {
    val value: T

    fun update(function: (T) -> T)
}

class MutableStateFlowAccessor<T>(
    private val flow: MutableStateFlow<T>,
) : StateAccessor<T> {
    override val value: T get() = flow.value

    override fun update(function: (T) -> T) = flow.update(function)
}
