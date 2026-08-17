package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar

@Composable
fun AddFundsScreen(
    onNavigateBack: () -> Unit,
    onTermsClick: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    ScreenTopBar(
        title = stringResource(R.string.wallet_add_funds),
        onLeadingClick = onNavigateBack
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is WalletEffect.ShowMessage -> onShowMessage(context.getString(effect.messageRes))
            is WalletEffect.ShowTextMessage -> onShowMessage(effect.message)
        }
    }

    AddFundsContent(
        state = state,
        onEvent = viewModel::onEvent,
        onTermsClick = onTermsClick,
    )
}

@Composable
private fun AddFundsContent(
    state: WalletState,
    onEvent: (WalletIntent) -> Unit,
    onTermsClick: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 22.dp),
        ) {
            Text(
                stringResource(R.string.wallet_top_up_title),
                color = Theme.colors.primaryFont,
                style = Theme.typography.title.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.wallet_top_up_description),
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.medium.copy(fontSize = 17.sp, lineHeight = 24.sp)
            )
            Spacer(Modifier.height(28.dp))
            AmountInput(
                amount = state.topUpAmount,
                hasError = state.topUpAmountError,
                onAmountChanged = { onEvent(WalletIntent.TopUpAmountChanged(it)) }
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(DR.drawable.ic_wallet_info),
                    contentDescription = null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = stringResource(R.string.wallet_amount_helper),
                    color = Theme.colors.secondaryFont,
                    style = Theme.typography.body.medium.copy(
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(onClick = onTermsClick, contentPadding = PaddingValues(vertical = 18.dp)) {
                Text(
                    stringResource(R.string.wallet_terms_apply),
                    color = Theme.colors.primary,
                    style = Theme.typography.body.small.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Medium
                )
            }
            if (state.hasPendingCreditAdd) {
                PendingCreditAddSection(
                    isRetrying = state.isRetryingCreditAdd,
                    onRetry = { onEvent(WalletIntent.RetryPendingCreditAdd) },
                )
            }
        }
        Surface(color = Theme.colors.surface, shadowElevation = 4.dp) {
            PrimaryButton(
                caption = stringResource(R.string.wallet_add_funds),
                onClick = { onEvent(WalletIntent.AddFundsClicked) },
                isDisabled = !state.canAddFunds,
                isLoading = state.isAddingFunds,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(54.dp),
            )
        }
    }
}

@Composable
private fun PendingCreditAddSection(
    isRetrying: Boolean,
    onRetry: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Theme.colors.surface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Theme.colors.primary.copy(alpha = 0.35f)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.wallet_pending_credit_title),
                color = Theme.colors.primaryFont,
                style = Theme.typography.body.medium.copy(fontSize = 16.sp),
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.wallet_pending_credit_description),
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.medium.copy(fontSize = 14.sp, lineHeight = 20.sp),
            )
            SecondaryButton(
                caption = stringResource(R.string.wallet_retry_credit_add),
                onClick = onRetry,
                isLoading = isRetrying,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun AmountInput(
    amount: String,
    hasError: Boolean,
    onAmountChanged: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(Theme.colors.surface, RoundedCornerShape(13.dp))
            .border(
                1.5.dp,
                if (hasError) Theme.colors.error else Theme.colors.onDisable,
                RoundedCornerShape(13.dp),
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.wallet_currency),
            color = Theme.colors.secondaryFont,
            style = Theme.typography.title.copy(fontSize = 20.sp),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(12.dp))
        BasicTextField(
            value = amount,
            onValueChange = onAmountChanged,
            modifier = Modifier.weight(1f),
            textStyle = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = androidx.compose.ui.graphics.SolidColor(Theme.colors.primary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (amount.isEmpty()) {
                        Text(
                            stringResource(R.string.wallet_zero_amount),
                            color = Theme.colors.onDisable,
                            style = Theme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview
@Composable
private fun AddFundsPreview() =
    SpTheme { AddFundsContent(WalletState(), {}, {}) }
