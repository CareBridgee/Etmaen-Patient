package com.carenest.presentation.ui.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.home.components.HomeHistoryItem
import com.carenest.presentation.ui.home.components.HomeShimmerLoading

@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToServices: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            HistoryEffect.NavigateBack -> onNavigateBack()
            is HistoryEffect.NavigateToHistoryDetails -> onNavigateToDetails(effect.historyId)
            HistoryEffect.NavigateToServices -> onNavigateToServices()
        }
    }

    HistoryScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun HistoryScreenContent(
    state: HistoryState,
    onEvent: (HistoryIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    ScreenTopBar(
        title = stringResource(R.string.home_history_title),
        onLeadingClick = { onEvent(HistoryIntent.BackClicked) }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading) {
            HomeShimmerLoading(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Theme.spacing.medium, vertical = Theme.spacing.space12)
            )
        } else if (state.error != null) {
            EmptyState(
                title = stringResource(R.string.home_error_title),
                description = state.error,
                actionLabel = stringResource(R.string.home_error_retry),
                onActionClick = { onEvent(HistoryIntent.RetryClicked) },
                accentColor = Theme.colors.primary,
                modifier = Modifier.padding(vertical = Theme.spacing.extraLarge + Theme.spacing.medium)
            )
        } else if (state.historyItems.isEmpty()) {
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { isVisible = true }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600)
                )
            ) {
                EmptyState(
                    title = stringResource(R.string.history_empty_title),
                    description = stringResource(R.string.history_empty_description),
                    icon = ImageVector.vectorResource(com.carenest.designsystem.R.drawable.ic_order_history),
                    actionLabel = stringResource(R.string.history_explore_services),
                    onActionClick = { onEvent(HistoryIntent.ExploreServicesClicked) },
                    accentColor = Theme.colors.primary,
                    modifier = Modifier.padding(vertical = Theme.spacing.extraLarge + Theme.spacing.medium)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(Theme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
            ) {
                items(state.historyItems) { item ->
                    HomeHistoryItem(
                        serviceHistory = item,
                        onClick = { onEvent(HistoryIntent.HistoryItemClicked(it.serviceRequestId)) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    SpTheme {
        HistoryScreenContent(
            state = HistoryState(),
            onEvent = {}
        )
    }
}
