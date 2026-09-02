package com.carenest.presentation.ui.tracking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.domain.model.NurseTrackingInfo
import com.carenest.presentation.R
import com.carenest.presentation.ui.tracking.components.CancellationInfoBanner
import com.carenest.presentation.ui.tracking.components.NurseInfoCard
import com.carenest.presentation.ui.tracking.components.NurseOnTheWayActionButtons
import com.carenest.presentation.ui.tracking.components.StatInfoCard
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.core.util.dialPhoneNumber
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.tracking.components.CancelVisitConfirmationDialog

@Composable
fun NurseOnTheWayScreen(
    requestId: String,
    onNavigateBack: () -> Unit,
    onNavigateToQrCode: () -> Unit,
    onOpenChat: (nurseId: String) -> Unit,
    onVisitCompleted: (requestId: String) -> Unit,
    showSnackbar: (String) -> Unit,
    viewModel: NurseOnTheWayViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(requestId) {
        viewModel.handleIntent(NurseOnTheWayIntent.LoadNurseTrackingInfo(requestId))
    }

    ObserveEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            is NurseOnTheWayEffect.InitiateCall -> dialPhoneNumber(context, effect.phoneNumber)
            is NurseOnTheWayEffect.OpenChat -> onOpenChat(effect.nurseId)
            NurseOnTheWayEffect.NavigateToQrCode -> onNavigateToQrCode()
            NurseOnTheWayEffect.NavigateBackAfterCancel -> onNavigateBack()
            is NurseOnTheWayEffect.NavigateToVisitCompleted -> onVisitCompleted(effect.requestId)
            is NurseOnTheWayEffect.ShowCancellationFeeWarning -> showSnackbar(effect.message)
            is NurseOnTheWayEffect.ShowError -> showSnackbar(effect.message)

        }
    }

    ScreenTopBar(
        title = stringResource(R.string.nurse_on_the_way_title),
        onLeadingClick = onNavigateBack
    )

    NurseOnTheWayLanding(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@Composable
fun NurseOnTheWayLanding(
    state: NurseOnTheWayState,
    onIntent: (NurseOnTheWayIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> NurseOnTheWayLoadingShimmer()
            state.nurseInfo != null -> NurseOnTheWayContent(
                state = state,
                nurseInfo = state.nurseInfo,
                onIntent = onIntent,
            )
        }
    }
    if (state.showCancelConfirmationDialog) {
        CancelVisitConfirmationDialog(
            onConfirm = { onIntent(NurseOnTheWayIntent.OnConfirmCancelVisitClicked) },
            onDismiss = { onIntent(NurseOnTheWayIntent.OnDismissCancelDialogClicked) },
        )
    }

    if (state.showNurseCancelledDialog) {
        CancelVisitConfirmationDialog(
            title = stringResource(R.string.nurse_on_the_way_nurse_cancelled_title),
            message = stringResource(R.string.nurse_on_the_way_nurse_cancelled_message),
            confirmText = stringResource(R.string.ok),
            dismissText = "",
            onConfirm = { onIntent(NurseOnTheWayIntent.OnNurseCancelledDismissed) },
            onDismiss = { onIntent(NurseOnTheWayIntent.OnNurseCancelledDismissed) }
        )
    }
}

@Composable
private fun NurseOnTheWayContent(
    state: NurseOnTheWayState,
    nurseInfo: NurseTrackingInfo,
    onIntent: (NurseOnTheWayIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SuccessIcon()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.nurse_on_the_way_title),
            style = Theme.typography.title.copy(fontWeight = FontWeight.Bold),
            color = Theme.colors.primaryFont,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.nurse_on_the_way_subtitle),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        CancellationInfoBanner(
            message = stringResource(
                R.string.nurse_on_the_way_cancellation_notice,
                nurseInfo.cancellationWindowMinutes,
            ),
        )

        Spacer(modifier = Modifier.height(16.dp))

        NurseInfoCard(
            nurseName = nurseInfo.name,
            reviewsCount = nurseInfo.reviewsCount,
            rating = nurseInfo.rating,
            estimatedArrivalTime = nurseInfo.estimatedArrivalTime,
            isOnline = nurseInfo.isOnline,
            photoUrl = nurseInfo.photoUrl,
            onCallClick = { onIntent(NurseOnTheWayIntent.OnCallNurseClicked) },
            onMessageClick = { onIntent(NurseOnTheWayIntent.OnMessageNurseClicked) },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StatInfoCard(
                icon = Icons.Filled.LocationOn,
                label = stringResource(R.string.nurse_on_the_way_distance_label),
                value = stringResource(R.string.nurse_on_the_way_distance_value, nurseInfo.distanceKm),
                modifier = Modifier.weight(1f),
            )
            StatInfoCard(
                icon = Icons.Filled.MedicalServices,
                label = stringResource(R.string.nurse_on_the_way_specialty_label),
                value = nurseInfo.specialty,
                modifier = Modifier.weight(1f),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        NurseOnTheWayActionButtons(
            onShowQrCodeClick = { onIntent(NurseOnTheWayIntent.OnShowQrCodeClicked) },
            onCancelClick = { onIntent(NurseOnTheWayIntent.OnCancelVisitClicked(state.nurseInfo?.requestId
                ?: "".also { onIntent(NurseOnTheWayIntent.OnRequestIdNotFound) }))},
            isCancelling = state.isCancelling,
        )
    }
}

@Composable
private fun SuccessIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(84.dp)
            .background(color = Theme.colors.primary, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_check_white),
            contentDescription = null,
            tint = Theme.colors.onPrimary,
            modifier = Modifier.size(40.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        NurseOnTheWayLanding(
            state = NurseOnTheWayState(),
            onIntent = {},
        )
    }
}

@Composable
private fun NurseOnTheWayLoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShimmerPlaceholder(
            modifier = Modifier.size(84.dp),
            shape = CircleShape,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.58f)
                .height(26.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(15.dp),
        )
        Spacer(modifier = Modifier.height(20.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp),
            shape = RoundedCornerShape(24.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            repeat(2) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .weight(1f)
                        .height(96.dp),
                    shape = RoundedCornerShape(20.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        )
    }
}
