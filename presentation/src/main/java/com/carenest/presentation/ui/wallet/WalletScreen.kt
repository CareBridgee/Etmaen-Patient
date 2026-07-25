package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.ui.wallet.components.*

@Composable
fun WalletScreen(
    onAddFunds: () -> Unit,
    onAddPaymentMethod: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletContent(state, onAddFunds, onAddPaymentMethod)
}

@Composable
private fun WalletContent(state: WalletState, onAddFunds: () -> Unit, onAddPaymentMethod: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WalletBalanceCard(
            balance = stringResource(R.string.wallet_balance_value, state.balance),
            autoRefillText = stringResource(R.string.wallet_balance_auto_refill, stringResource(if (state.isAutoRefillEnabled) R.string.wallet_on else R.string.wallet_off)),
            addFundsText = stringResource(R.string.wallet_add_funds),
            onAddFunds = onAddFunds
        )
        SectionTitle(stringResource(R.string.wallet_payment_methods))
        Surface(color = Theme.colors.surface, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
            WalletActionRow(
                stringResource(R.string.wallet_cash),
                icon = painterResource(DR.drawable.ic_wallet_cash),
                onClick = onAddPaymentMethod,
                iconContainerShape = RoundedCornerShape(12.dp)
            )
        }
        OutlinedWalletAction(stringResource(R.string.wallet_add_payment_method), onAddPaymentMethod)
        SectionTitle(stringResource(R.string.wallet_ride_profiles))
        Surface(color = Theme.colors.surface, shape = RoundedCornerShape(18.dp), shadowElevation = 1.dp) {
            Column {
                WalletActionRow(stringResource(R.string.wallet_personal), icon = painterResource(DR.drawable.ic_wallet_personal), selected = true, showChevron = false)
                HorizontalDivider(Modifier.padding(start = 72.dp), color = Theme.colors.divider)
                WalletActionRow(stringResource(R.string.wallet_business), stringResource(R.string.wallet_business_subtitle), painterResource(DR.drawable.ic_wallet_business))
            }
        }
    }
}

@Composable private fun SectionTitle(text: String) {
    Text(text, color = Theme.colors.secondaryFont, style = Theme.typography.body.medium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp, start = 3.dp))
}

@Preview @Composable private fun WalletPreview() = SpTheme { WalletContent(WalletState(), {}, {}) }
