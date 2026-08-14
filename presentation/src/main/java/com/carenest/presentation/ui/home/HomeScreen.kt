package com.carenest.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.components.toast.ToastHost
import com.carenest.designsystem.components.toast.rememberToastState
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.components.rememberTimeBasedGreeting
import com.carenest.presentation.ui.home.components.HomeAICard
import com.carenest.presentation.ui.home.components.HomeHistoryEmpty
import com.carenest.presentation.ui.home.components.HomeHistoryHeader
import com.carenest.presentation.ui.home.components.HomeHistoryItem
import com.carenest.presentation.ui.home.components.HomeGreetingBar
import com.carenest.presentation.ui.home.components.HomeSearchBar
import com.carenest.presentation.ui.home.components.HomeServicesGrid
import com.carenest.presentation.ui.home.components.HomeShimmerLoading
import com.carenest.designsystem.R as RD

@Composable
fun HomeScreen(
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetails: (String) -> Unit,
    onNavigateToServiceHistoryDetails: (String) -> Unit,
    onNavigateToActiveRequest: (String, String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToAIChat: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val toastState = rememberToastState()

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    ScreenTopBar(
        title = stringResource(R.string.onboarding_title),
        showLeadingIcon = false,
        profileImage = painterResource(id = com.carenest.designsystem.R.drawable.ic_profile),
        onProfileClick = onNavigateToProfile
    )

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToServices -> onNavigateToServices()
            is HomeEffect.NavigateToServiceDetails -> onNavigateToServiceDetails(effect.serviceId)
            is HomeEffect.NavigateToServiceHistoryDetails -> onNavigateToServiceHistoryDetails(effect.requestId)
            is HomeEffect.NavigateToActiveRequest -> onNavigateToActiveRequest(effect.requestId, effect.status)
            HomeEffect.NavigateToHistory -> onNavigateToHistory()
            HomeEffect.NavigateToAIChat -> onNavigateToAIChat()
            is HomeEffect.ShowToast -> {
                toastState.show(effect.message, effect.type)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreenContent(
            state = state,
            onEvent = viewModel::onEvent
        )
        ToastHost(state = toastState)
    }
}

@Composable
fun HomeScreenContent(
    state: HomeState,
    onEvent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
    ) {
        if (state.isLoading) {
            HomeShimmerLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        } else if (state.isError) {
            EmptyState(
                title = stringResource(R.string.home_error_title),
                description = state.errorMessage ?: stringResource(R.string.home_error_default_desc),
                actionLabel = stringResource(R.string.home_error_retry),
                onActionClick = { onEvent(HomeIntent.RetryClicked) },
                accentColor = Theme.colors.primary,
                modifier = Modifier.padding(vertical = 48.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    val greeting = rememberTimeBasedGreeting(state.user?.name)
                    HomeGreetingBar(
                        greetingText = greeting,
                        avatarUrl = state.user?.avatarUrl,
                    )
                }

                item {
                    HomeSearchBar(
                        query = state.searchQuery,
                        onQueryChange = { onEvent(HomeIntent.SearchQueryChanged(it)) },
                    )
                }

                item {
                    HomeAICard(
                        onStartChatClick = { onEvent(HomeIntent.StartAIChatClicked) }
                    )
                }

                item {
                    HomeServicesGrid(
                        services = state.filteredServices,
                        isSearchEmpty = state.isSearchEmpty,
                        onViewAllClick = { onEvent(HomeIntent.ViewAllServicesClicked) },
                        onServiceClick = { onEvent(HomeIntent.ServiceClicked(it)) }
                    )
                }

                item {
                    HomeHistoryHeader(
                        onManageClick = { onEvent(HomeIntent.ManageAllHistoryClicked) }
                    )
                }

                if (state.isHistoryEmpty) {
                    item {
                        HomeHistoryEmpty()
                    }
                } else {
                    items(state.upcomingBooking) { serviceHistory ->
                        HomeHistoryItem(
                            serviceHistory = serviceHistory,
                            onClick = { onEvent(HomeIntent.HistoryItemClicked(it)) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    SpTheme {
        HomeScreenContent(
            state = HomeState(
            user = com.carenest.domain.model.home.User(
                id = "1",
                firstName = "Elena",
                lastName = "Doe"
            ),
                searchQuery = "",
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
