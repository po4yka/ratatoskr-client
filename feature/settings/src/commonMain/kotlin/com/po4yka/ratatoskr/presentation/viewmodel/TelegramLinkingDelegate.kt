package com.po4yka.ratatoskr.presentation.viewmodel

import com.po4yka.ratatoskr.domain.model.TelegramLinkData
import com.po4yka.ratatoskr.domain.usecase.GetTelegramLinkStatusUseCase
import com.po4yka.ratatoskr.domain.usecase.LinkTelegramUseCase
import com.po4yka.ratatoskr.domain.usecase.UnlinkTelegramUseCase
import com.po4yka.ratatoskr.presentation.state.TelegramLinkState
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

private val logger = KotlinLogging.logger {}

@Factory
class TelegramLinkingDelegate(
    private val getTelegramLinkStatusUseCase: GetTelegramLinkStatusUseCase,
    private val unlinkTelegramUseCase: UnlinkTelegramUseCase,
    private val linkTelegramUseCase: LinkTelegramUseCase,
) {
    fun loadLinkStatus(
        scope: CoroutineScope,
        currentState: () -> TelegramLinkState,
        onState: (TelegramLinkState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoading = true, error = null))
            runCatching { getTelegramLinkStatusUseCase() }
                .onSuccess { status ->
                    onState(
                        currentState().copy(
                            isLoading = false,
                            linkStatus = status,
                            linkNonce = if (status.linked) null else currentState().linkNonce,
                        ),
                    )
                }
                .onFailure { throwable ->
                    onState(currentState().copy(isLoading = false, error = throwable.message))
                }
        }
    }

    fun unlinkTelegram(
        scope: CoroutineScope,
        currentState: () -> TelegramLinkState,
        onState: (TelegramLinkState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoading = true, error = null))
            runCatching { unlinkTelegramUseCase() }
                .onSuccess { status ->
                    onState(currentState().copy(isLoading = false, linkStatus = status))
                }
                .onFailure { throwable ->
                    onState(currentState().copy(isLoading = false, error = throwable.message))
                }
        }
    }

    fun beginTelegramLink(
        scope: CoroutineScope,
        currentState: () -> TelegramLinkState,
        onState: (TelegramLinkState) -> Unit,
    ) {
        scope.launch {
            onState(currentState().copy(isLoading = true, error = null))
            runCatching { linkTelegramUseCase.begin() }
                .onSuccess { nonce ->
                    onState(currentState().copy(isLoading = false, linkNonce = nonce))
                }
                .onFailure { throwable ->
                    onState(currentState().copy(isLoading = false, error = throwable.message))
                }
        }
    }

    fun cancelTelegramLink(
        currentState: () -> TelegramLinkState,
        onState: (TelegramLinkState) -> Unit,
    ) {
        onState(currentState().copy(linkNonce = null))
    }

    @Suppress("unused")
    fun completeTelegramLink(
        telegramAuth: TelegramLinkData,
        scope: CoroutineScope,
        currentState: () -> TelegramLinkState,
        onState: (TelegramLinkState) -> Unit,
    ) {
        val nonce = currentState().linkNonce ?: return
        scope.launch {
            onState(currentState().copy(isLoading = true, error = null))
            runCatching { linkTelegramUseCase.complete(nonce, telegramAuth) }
                .onSuccess { status ->
                    onState(
                        currentState().copy(
                            isLoading = false,
                            linkStatus = status,
                            linkNonce = null,
                        ),
                    )
                }
                .onFailure { throwable ->
                    onState(currentState().copy(isLoading = false, error = throwable.message))
                }
        }
    }
}
