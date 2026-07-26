package com.carenest.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.theme.SpTheme
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.home.components.HomeAICard
import com.carenest.presentation.ui.home.components.HomeBookingCard
import com.carenest.presentation.ui.home.components.HomeGreetingBar
import com.carenest.presentation.ui.home.components.HomeSearchBar
import com.carenest.presentation.ui.home.components.HomeServicesGrid
import com.carenest.presentation.ui.home.components.HomeShimmerLoading
import com.carenest.presentation.model.HealthcareServiceUiModel

import com.carenest.designsystem.components.toast.ToastHost
import com.carenest.designsystem.components.toast.rememberToastState

@Composable
fun HomeScreen(
    onNavigateToServices: () -> Unit,
    onNavigateToServiceDetails: (HealthcareServiceUiModel) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToAIChat: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val toastState = rememberToastState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToServices -> onNavigateToServices()
            is HomeEffect.NavigateToServiceDetails -> onNavigateToServiceDetails(effect.service)
            HomeEffect.NavigateToBookings -> onNavigateToBookings()
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
    HideTopBar()

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top AppBar / Greeting
                HomeGreetingBar(
                    greetingText = state.greetingName,
                    avatarUrl = state.user?.avatarUrl,
                    onNotificationClick = { onEvent(HomeIntent.NotificationClicked) }
                )

                // Search Bar
                HomeSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onEvent(HomeIntent.SearchQueryChanged(it)) },
                    onClearClick = { onEvent(HomeIntent.ClearSearch) }
                )

                // AI Hero Assessment Card
                HomeAICard(
                    onStartChatClick = { onEvent(HomeIntent.StartAIChatClicked) }
                )

                // Healthcare Services Grid
                HomeServicesGrid(
                    services = state.filteredServices,
                    isSearchEmpty = state.isSearchEmpty,
                    onViewAllClick = { onEvent(HomeIntent.ViewAllServicesClicked) },
                    onServiceClick = { onEvent(HomeIntent.ServiceClicked(it)) }
                )

                // Upcoming Booking Preview
                HomeBookingCard(
                    booking = state.upcomingBooking,
                    onManageClick = { onEvent(HomeIntent.ManageBookingsClicked) },
                    onBookingClick = { onEvent(HomeIntent.BookingClicked(it)) }
                )
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
                user = com.carenest.domain.model.home.User("1", "Elena Doe", null),
                searchQuery = "",
                isLoading = false
            ),
            onEvent = {}
        )
    }
}
