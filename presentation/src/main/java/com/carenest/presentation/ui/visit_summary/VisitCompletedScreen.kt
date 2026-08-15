package com.carenest.presentation.ui.visit_summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.domain.model.visit_summary.VisitSummary
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.visit_summary.components.TotalAmountCard
import com.carenest.presentation.ui.visit_summary.components.VisitRatingDialogContent
import com.carenest.presentation.ui.visit_summary.components.VisitSummaryCard

@Composable
fun VisitCompletedScreen(
    requestId: String,
    onNavigateHome: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    viewModel: VisitCompletedViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(requestId) {
        viewModel.handleIntent(VisitCompletedIntent.LoadVisitSummary(requestId))
    }

    ObserveEffect(effect = viewModel.effect) { effect ->
        when (effect) {
            VisitCompletedEffect.NavigateHome -> onNavigateHome()
            VisitCompletedEffect.RatingSubmitted -> onShowSnackbar("Thanks for your feedback!")
            is VisitCompletedEffect.ShowError -> onShowSnackbar(effect.message)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> VisitCompletedLoadingShimmer()
            state.summary != null -> VisitCompletedContent(
                summary = state.summary ?: VisitSummary(
                    requestId = "",
                    professionalName = "Professional Name",
                    professionalImageUrl = null,
                    serviceType = "Service Type",
                    serviceIconUrl = null,
                    durationMinutes = 60,
                    completedDate = "30 jun",
                    totalAmount = 85.0,
                    isVerified = true,
                ),
                onIntent = viewModel::handleIntent,
            )
        }

        if (state.showRatingDialog) {
            Dialog(onDismissRequest = { viewModel.handleIntent(VisitCompletedIntent.OnDismissRatingDialogClicked) }) {
                VisitRatingDialogContent(
                    selectedRating = state.selectedRating,
                    reviewText = state.reviewText,
                    isAnonymous = state.isAnonymous,
                    isSubmitting = state.isSubmittingRating,
                    onStarSelected = { viewModel.handleIntent(VisitCompletedIntent.OnStarSelected(it)) },
                    onReviewTextChanged = { viewModel.handleIntent(VisitCompletedIntent.OnReviewTextChanged(it)) },
                    onAnonymousChanged = { viewModel.handleIntent(VisitCompletedIntent.OnAnonymousChanged(it)) },
                    onSubmit = { viewModel.handleIntent(VisitCompletedIntent.OnSubmitRatingClicked) },
                    onDismiss = { viewModel.handleIntent(VisitCompletedIntent.OnDismissRatingDialogClicked) },
                )
            }
        }
    }
}

@Composable
private fun VisitCompletedContent(
    summary: VisitSummary,
    onIntent: (VisitCompletedIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Theme.spacing.space20, vertical = Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .background(Theme.colors.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Theme.colors.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(com.carenest.designsystem.R.drawable.ic_check),
                    contentDescription = null,
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(30.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.visit_completed_title),
            style = Theme.typography.displayMedium,
            color = Theme.colors.primaryFont,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.visit_completed_subtitle),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(24.dp))

        VisitSummaryCard(summary = summary)

        Spacer(modifier = Modifier.height(16.dp))

        TotalAmountCard(amount = summary.totalAmount)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onIntent(VisitCompletedIntent.OnBackToHomeClicked) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Theme.colors.primary,
                contentColor = Theme.colors.onPrimary,
            ),
        ) {
            Text(
                text = stringResource(R.string.visit_completed_back_home_button),
                style = Theme.typography.body.medium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        VisitCompletedContent(
            summary = VisitSummary(
                requestId = "",
                professionalName = "Sarah Mitchell",
                professionalImageUrl = null,
                serviceType = "General Nursing",
                serviceIconUrl = null,
                durationMinutes = 60,
                completedDate = "Oct 24",
                totalAmount = 85.0,
                isVerified = true,
            ), onIntent = {})
    }
}

@Composable
private fun VisitCompletedLoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Theme.spacing.space20, vertical = Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ShimmerPlaceholder(
            modifier = Modifier.size(90.dp),
            shape = CircleShape,
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.58f)
                .height(28.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .height(15.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp),
            shape = RoundedCornerShape(24.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp),
            shape = RoundedCornerShape(20.dp),
        )
        Spacer(modifier = Modifier.height(24.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
        )
    }
}
