package com.po4yka.bitesizereader.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value

/**
 * Root navigation component
 */
interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed class Child {
        class Login(val component: LoginComponent) : Child()
        class SummaryList(val component: SummaryListComponent) : Child()
        class SummaryDetail(val component: SummaryDetailComponent) : Child()
        class SubmitURL(val component: SubmitURLComponent) : Child()
        class Search(val component: SearchComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext,
    private val onLoginSuccess: () -> Unit = {},
    private val loginComponentFactory: (ComponentContext, () -> Unit) -> LoginComponent,
    private val summaryListComponentFactory: (ComponentContext, (Int) -> Unit, () -> Unit, () -> Unit) -> SummaryListComponent,
    private val summaryDetailComponentFactory: (ComponentContext, Int, () -> Unit) -> SummaryDetailComponent,
    private val submitURLComponentFactory: (ComponentContext, () -> Unit) -> SubmitURLComponent,
    private val searchComponentFactory: (ComponentContext, (Int) -> Unit, () -> Unit) -> SearchComponent
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Screen>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Screen.serializer(),
            initialConfiguration = Screen.Login,
            handleBackButton = true,
            childFactory = ::child
        )

    private fun child(config: Screen, componentContext: ComponentContext): RootComponent.Child =
        when (config) {
            is Screen.Login -> RootComponent.Child.Login(
                loginComponentFactory(
                    componentContext,
                    ::navigateToSummaryList
                )
            )

            is Screen.SummaryList -> RootComponent.Child.SummaryList(
                summaryListComponentFactory(
                    componentContext,
                    ::navigateToSummaryDetail,
                    ::navigateToSubmitURL,
                    ::navigateToSearch
                )
            )

            is Screen.SummaryDetail -> RootComponent.Child.SummaryDetail(
                summaryDetailComponentFactory(
                    componentContext,
                    config.id,
                    ::onBackClicked
                )
            )

            is Screen.SubmitURL -> RootComponent.Child.SubmitURL(
                submitURLComponentFactory(
                    componentContext,
                    ::onBackClicked
                )
            )

            is Screen.Search -> RootComponent.Child.Search(
                searchComponentFactory(
                    componentContext,
                    ::navigateToSummaryDetail,
                    ::onBackClicked
                )
            )

            is Screen.Settings -> throw NotImplementedError("Settings screen not yet implemented")
        }

    private fun navigateToSummaryList() {
        navigation.push(Screen.SummaryList)
    }

    private fun navigateToSummaryDetail(id: Int) {
        navigation.push(Screen.SummaryDetail(id))
    }

    private fun navigateToSubmitURL() {
        navigation.push(Screen.SubmitURL)
    }

    private fun navigateToSearch() {
        navigation.push(Screen.Search)
    }

    override fun onBackClicked() {
        navigation.pop()
    }
}

// Component interfaces (to be implemented in platform-specific code)

interface LoginComponent {
    val state: Value<com.po4yka.bitesizereader.presentation.state.LoginState>
    fun loginWithTelegram(
        telegramUserId: Long,
        authHash: String,
        authDate: Long,
        username: String?,
        firstName: String?,
        lastName: String?,
        photoUrl: String?,
        clientId: String
    )
}

interface SummaryListComponent {
    val state: Value<com.po4yka.bitesizereader.presentation.state.SummaryListState>
    fun refresh()
    fun loadMore()
    fun markAsRead(id: Int, isRead: Boolean)
    fun sync(forceFullSync: Boolean = false)
}

interface SummaryDetailComponent {
    val state: Value<com.po4yka.bitesizereader.presentation.state.SummaryDetailState>
    fun retry()
}

interface SubmitURLComponent {
    val state: Value<com.po4yka.bitesizereader.presentation.state.SubmitURLState>
    fun setURL(url: String)
    fun submitURL()
    fun retry()
    fun reset()
}

interface SearchComponent {
    val state: Value<com.po4yka.bitesizereader.presentation.state.SearchState>
    fun setQuery(query: String)
    fun search()
    fun clearSearch()
}
