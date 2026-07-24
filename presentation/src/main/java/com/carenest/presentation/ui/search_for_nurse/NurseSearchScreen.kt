package com.carenest.presentation.ui.search_for_nurse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.presentation.ui.request_service.components.PaymentMethodSection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.ui.search_for_nurse.composables.ActiveNursesChip
import com.carenest.presentation.ui.search_for_nurse.composables.NurseOfferCard
import com.carenest.presentation.ui.search_for_nurse.composables.SearchingAnimation


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseSearchScreen(
    onBack: () -> Unit,
    onMatched: (nurseId: String) -> Unit,
    viewModel: NurseSearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NurseSearchEffect.NavigateToEnRoute -> onMatched(effect.nurseId)
                NurseSearchEffect.NavigateBack -> onBack()
            }
        }
    }

    NurseSearchContent(
        state = state,
        onBack = { viewModel.onIntent(NurseSearchIntent.CancelSearch) },
        onAccept = { viewModel.onIntent(NurseSearchIntent.AcceptOffer(it)) },
        onDecline = { viewModel.onIntent(NurseSearchIntent.DeclineOffer(it)) })

    if (state.showPaymentSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { viewModel.onIntent(NurseSearchIntent.DismissPaymentSheet) },
            sheetState = sheetState,
            containerColor = Theme.colors.backGround
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                PaymentMethodSection(
                    paymentMethods = state.paymentMethods,
                    onMethodSelected = { viewModel.onIntent(NurseSearchIntent.PaymentMethodSelected(it)) }
                )
                Spacer(Modifier.height(24.dp))
                PrimaryButton(
                    caption = "Confirm Payment",
                    onClick = { viewModel.onIntent(NurseSearchIntent.ConfirmPayment) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NurseSearchContent(
    state: NurseSearchState,
    onBack: () -> Unit,
    onAccept: (id: String) -> Unit,
    onDecline: (id: String) -> Unit
) {
    val colors = Theme.colors

    Column(
        modifier = Modifier.statusBarsPadding()
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
            items(state.nearbyNurses, key = { it.id }) { nurse ->
                NurseOfferCard(
                    nurse = nurse,
                    onAccept = { onAccept(nurse.id) },
                    onDecline = { onDecline(nurse.id) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        NurseSearchContent(
            state = NurseSearchState(nearbyNurses = listOf(
                NearbyNurse(
                    id = "1",
                    name = "TODO()",
                    title = "TODO()",
                    price = 1.0,
                    rating = 1.0,
                    reviewCount = 1,
                    area = "TODO()",
                    distanceKm = 1.0,
                    avatarUrl = "TODO()"
                ),
                NearbyNurse(
                    id = "2",
                    name = "TODO()",
                    title = "TODO()",
                    price = 1.0,
                    rating = 1.0,
                    reviewCount = 1,
                    area = "TODO()",
                    distanceKm = 1.0,
                    avatarUrl = "TODO()"
                ),
                NearbyNurse(
                    id = "3",
                    name = "TODO()",
                    title = "TODO()",
                    price = 1.0,
                    rating = 1.0,
                    reviewCount = 1,
                    area = "TODO()",
                    distanceKm = 1.0,
                    avatarUrl = "TODO()"
                )
            )),
            onBack = { },
            onAccept = { },
            onDecline = { })
    }
}