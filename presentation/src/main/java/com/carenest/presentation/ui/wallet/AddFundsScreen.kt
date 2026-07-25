package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.ui.wallet.components.WalletActionRow

@Composable
fun AddFundsScreen(
    onAddPaymentMethod: () -> Unit,
    onTermsClick: () -> Unit,
    onAddFunds: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AddFundsContent(state, viewModel::onEvent, onAddPaymentMethod, onTermsClick, onAddFunds)
}

@Composable
private fun AddFundsContent(
    state: WalletState,
    onEvent: (WalletIntent) -> Unit,
    onAddPaymentMethod: () -> Unit,
    onTermsClick: () -> Unit,
    onAddFunds: () -> Unit,
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
                style = Theme.typography.title,
                fontWeight = FontWeight.Bold
            )
            Text(
                stringResource(R.string.wallet_top_up_description),
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.medium
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = state.topUpAmount,
                onValueChange = { onEvent(WalletIntent.TopUpAmountChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp),
                prefix = {
                    Text(
                        stringResource(R.string.wallet_currency),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                placeholder = {
                    Text(
                        stringResource(R.string.wallet_zero_amount),
                        color = Theme.colors.onDisable,
                        style = Theme.typography.displayMedium
                    )
                },
                textStyle = Theme.typography.displayMedium.copy(color = Theme.colors.primaryFont),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Theme.colors.primary,
                    unfocusedBorderColor = Theme.colors.onDisable,
                    cursorColor = Theme.colors.primary
                )
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    painterResource(DR.drawable.ic_info),
                    null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    stringResource(R.string.wallet_amount_helper),
                    color = Theme.colors.secondaryFont,
                    style = Theme.typography.body.small
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(155, 310, 620).forEach { amount ->
                    OutlinedButton(
                        onClick = { onEvent(WalletIntent.SuggestedAmountSelected(amount)) },
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Theme.colors.onDisable),
                        contentPadding = PaddingValues(vertical = 11.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Theme.colors.primary)
                    ) {
                        Text(
                            stringResource(R.string.wallet_suggested_amount, amount),
                            style = Theme.typography.body.small,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            TextButton(onClick = onTermsClick, contentPadding = PaddingValues(vertical = 18.dp)) {
                Text(
                    stringResource(R.string.wallet_terms_apply),
                    color = Theme.colors.primary,
                    style = Theme.typography.body.small,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(48.dp))
            Text(
                stringResource(R.string.wallet_payment_method_label),
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.small,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.5.dp, Theme.colors.error.copy(alpha = .55f)),
                        RoundedCornerShape(16.dp)
                    ),
                color = Theme.colors.errorContainer.copy(alpha = .35f),
                shape = RoundedCornerShape(16.dp)
            ) {
                WalletActionRow(
                    title = state.selectedPaymentMethod?.let { stringResource(R.string.wallet_cash) }
                        ?: stringResource(R.string.wallet_add_payment_method),
                    subtitle = if (state.selectedPaymentMethod == null) stringResource(R.string.wallet_required) else null,
                    icon = painterResource(if (state.selectedPaymentMethod == null) DR.drawable.ic_payment_method else DR.drawable.ic_wallet),
                    onClick = onAddPaymentMethod
                )
            }
        }
        Surface(color = Theme.colors.surface, shadowElevation = 4.dp) {
            Button(
                onClick = onAddFunds,
                enabled = state.canAddFunds,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Theme.colors.primary,
                    contentColor = Theme.colors.onPrimary,
                    disabledContainerColor = Theme.colors.onDisable,
                    disabledContentColor = Theme.colors.hint
                )
            ) { Text(stringResource(R.string.wallet_add_funds), fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Preview
@Composable
private fun AddFundsPreview() =
    SpTheme { AddFundsContent(WalletState(selectedPaymentMethod = null), {}, {}, {}, {}) }
