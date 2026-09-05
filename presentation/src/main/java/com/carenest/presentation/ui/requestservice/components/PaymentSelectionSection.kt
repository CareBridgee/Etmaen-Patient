package com.carenest.presentation.ui.requestservice.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R
import com.carenest.designsystem.components.cards.PaymentMethodCard
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.formatPrice
import com.carenest.domain.model.payment.ServicePaymentMethod
import com.carenest.presentation.ui.requestservice.WalletCreditUiState

@Composable
fun PaymentSelectionSection(
    paymentMethods: List<ServicePaymentMethod>,
    selectedPaymentMethod: ServicePaymentMethod,
    walletCreditState: WalletCreditUiState,
    servicePrice: Double?,
    onPaymentMethodSelected: (ServicePaymentMethod) -> Unit,
    onWalletRetryClick: () -> Unit,
    onAddWalletCreditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
    ) {
        BasicText(
            text = stringResource(R.string.request_service_payment_method_label),
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.SemiBold,
            ),
        )

        paymentMethods.forEach { method ->
            PaymentMethodCard(
                title = method.title(),
                description = method.description(),
                painter = painterResource(method.iconRes()),
                selected = selectedPaymentMethod == method,
                onClick = { onPaymentMethodSelected(method) },
                trailingContent = if (method == ServicePaymentMethod.Wallet) {
                    {
                        WalletCreditContent(
                            state = walletCreditState,
                            servicePrice = servicePrice,
                            isSelected = selectedPaymentMethod == ServicePaymentMethod.Wallet,
                            onRetryClick = onWalletRetryClick,
                            onAddCreditClick = onAddWalletCreditClick,
                        )
                    }
                } else {
                    null
                },
            )
        }
    }
}

@Composable
private fun WalletCreditContent(
    state: WalletCreditUiState,
    servicePrice: Double?,
    isSelected: Boolean,
    onRetryClick: () -> Unit,
    onAddCreditClick: () -> Unit,
) {
    if (!isSelected) return

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
        when (state) {
            WalletCreditUiState.NotRequested -> Unit
            WalletCreditUiState.Loading -> Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Theme.colors.primary,
                )
                StatusText(
                    text = stringResource(R.string.request_service_wallet_balance_loading),
                )
            }
            WalletCreditUiState.Empty -> {
                StatusText(
                    text = stringResource(R.string.request_service_wallet_balance_empty),
                    isError = true,
                )
                AddCreditButton(onAddCreditClick)
            }
            is WalletCreditUiState.Available -> {
                val formattedCredit = formatPrice(state.credit)
                StatusText(
                    text = stringResource(
                        R.string.request_service_wallet_balance_available,
                        formattedCredit,
                    ),
                )
                if (servicePrice != null && state.credit < servicePrice) {
                    InsufficientCreditHint(onAddCreditClick)
                }
            }
            is WalletCreditUiState.Failure -> Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatusText(
                    text = stringResource(state.messageRes),
                    modifier = Modifier.weight(1f),
                    isError = true,
                )
                TextButton(onClick = onRetryClick) {
                    Text(
                        text = stringResource(R.string.request_service_wallet_retry),
                        color = Theme.colors.primary,
                        style = Theme.typography.body.small,
                    )
                }
            }
        }
    }
}

@Composable
private fun InsufficientCreditHint(onAddCreditClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)) {
        StatusText(
            text = stringResource(R.string.request_service_wallet_balance_insufficient),
            isError = true,
        )
        AddCreditButton(onAddCreditClick)
    }
}

@Composable
private fun AddCreditButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(
            text = stringResource(R.string.request_service_wallet_add_credit),
            color = Theme.colors.primary,
            style = Theme.typography.body.small.copy(fontWeight = FontWeight.SemiBold),
        )
    }
}

@Composable
private fun StatusText(
    text: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = Theme.typography.body.small.copy(
            color = if (isError) Theme.colors.error else Theme.colors.secondaryFont,
        ),
    )
}

@Composable
private fun ServicePaymentMethod.title(): String =
    when (this) {
        ServicePaymentMethod.Cash -> stringResource(R.string.request_service_payment_cash_title)
        ServicePaymentMethod.Wallet -> stringResource(R.string.request_service_payment_wallet_title)
    }

@Composable
private fun ServicePaymentMethod.description(): String =
    when (this) {
        ServicePaymentMethod.Cash -> stringResource(R.string.request_service_payment_cash_description)
        ServicePaymentMethod.Wallet -> stringResource(R.string.request_service_payment_wallet_description)
    }

private fun ServicePaymentMethod.iconRes(): Int =
    when (this) {
        ServicePaymentMethod.Cash -> R.drawable.ic_wallet_cash
        ServicePaymentMethod.Wallet -> R.drawable.ic_wallet
    }
