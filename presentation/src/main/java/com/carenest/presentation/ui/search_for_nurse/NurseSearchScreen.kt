package com.carenest.presentation.ui.search_for_nurse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.search_for_nurse.composables.ActiveNursesChip
import com.carenest.presentation.ui.search_for_nurse.composables.NurseOfferCard
import com.carenest.presentation.ui.search_for_nurse.composables.SearchingAnimation
import com.carenest.presentation.ui.tracking.components.CancelVisitConfirmationDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseSearchScreen(
    reservationId: String,
    serviceRequestId: String,
    onBack: () -> Unit,
    onMatched: (nurseId: String) -> Unit,
    viewModel: NurseSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is NurseSearchEffect.NavigateToEnRoute -> onMatched(effect.requestId)
            NurseSearchEffect.NavigateBack -> onBack()

            is NurseSearchEffect.ShowError -> Unit // TODO: wire to snackbar
        }
    }

    LaunchedEffect(reservationId) {
        viewModel.onIntent(NurseSearchIntent.StartSearching(reservationId, serviceRequestId))
    }

    BackHandler {
        viewModel.onIntent(NurseSearchIntent.CancelSearch)
    }

    ScreenTopBar(
        title = stringResource(R.string.searching_for_available),
        onLeadingClick = { viewModel.onIntent(NurseSearchIntent.CancelSearch) })

    NurseSearchContent(
        state = state,
        onAccept = { viewModel.onIntent(NurseSearchIntent.AcceptOffer(it)) },
        onDecline = { viewModel.onIntent(NurseSearchIntent.DeclineOffer(it)) })

    if (state.showCancelConfirmation) {
        CancelVisitConfirmationDialog(
            title = stringResource(R.string.searching_cancel_dialog_title),
            message = stringResource(R.string.searching_cancel_dialog_message),
            confirmText = stringResource(R.string.searching_cancel_dialog_confirm),
            dismissText = stringResource(R.string.searching_cancel_dialog_dismiss),
            onConfirm = { viewModel.onIntent(NurseSearchIntent.ConfirmCancelSearch) },
            onDismiss = { viewModel.onIntent(NurseSearchIntent.DismissCancelConfirmation) })
    }
}

@Composable
private fun NurseSearchContent(
    state: NurseSearchState,
    onAccept: (offerId: String) -> Unit,
    onDecline: (offerId: String) -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        SearchingAnimation()
        Spacer(Modifier.height(16.dp))

        Text(
            stringResource(R.string.searching_for_available),
            style = Theme.typography.title,
            color = colors.primaryFont
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.searching_describtion),
            style = Theme.typography.body.small,
            color = colors.secondaryFont,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))

        ActiveNursesChip(count = state.activeNursesCount)

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(state.offers, key = { it.id }) { offer ->
                NurseOfferCard(
                    offer = offer,
                    onAccept = { onAccept(offer.id) },
                    onDecline = { onDecline(offer.id) })
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}