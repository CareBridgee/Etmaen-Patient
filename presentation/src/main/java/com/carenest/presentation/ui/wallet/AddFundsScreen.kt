package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.ui.wallet.components.WalletActionRow
import com.carenest.presentation.ui.wallet.components.RequiredPaymentMethodRow
import com.carenest.presentation.ui.wallet.components.dashedRoundedBorder

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
            Spacer(Modifier.height(26.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(155, 310, 620).forEach { amount ->
                    OutlinedButton(
                        onClick = {
                            onEvent(WalletIntent.SuggestedAmountSelected(amount))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp),
                        shape = CircleShape,
                        border = BorderStroke(
                            width = 1.dp,
                            color = Theme.colors.onDisable
                        ),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Theme.colors.primary
                        )
                    ) {
                        Text(
                            text = stringResource(
                                R.string.wallet_suggested_amount,
                                amount
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            style = Theme.typography.body.small.copy(
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            ),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            TextButton(onClick = onTermsClick, contentPadding = PaddingValues(vertical = 18.dp)) {
                Text(
                    stringResource(R.string.wallet_terms_apply),
                    color = Theme.colors.primary,
                    style = Theme.typography.body.small.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.wallet_payment_method_label),
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.small.copy(fontSize = 12.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .dashedRoundedBorder(Theme.colors.error.copy(alpha = .55f), 16.dp),
                color = Theme.colors.errorContainer.copy(alpha = .55f),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (state.selectedPaymentMethod == null) {
                    RequiredPaymentMethodRow(
                        title = stringResource(R.string.wallet_add_payment_method),
                        requiredText = stringResource(R.string.wallet_required),
                        onClick = onAddPaymentMethod
                    )
                } else {
                    WalletActionRow(
                        title = stringResource(R.string.wallet_cash),
                        icon = painterResource(DR.drawable.ic_wallet_cash),
                        onClick = onAddPaymentMethod,
                        iconContainerShape = RoundedCornerShape(12.dp)
                    )
                }
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
            ) {
                Text(
                    stringResource(R.string.wallet_add_funds),
                    style = Theme.typography.body.medium.copy(fontSize = 16.sp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun AmountInput(amount: String, onAmountChanged: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .background(Theme.colors.surface, RoundedCornerShape(13.dp))
            .border(1.5.dp, Theme.colors.onDisable, RoundedCornerShape(13.dp))
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
    SpTheme { AddFundsContent(WalletState(selectedPaymentMethod = null), {}, {}, {}, {}) }
