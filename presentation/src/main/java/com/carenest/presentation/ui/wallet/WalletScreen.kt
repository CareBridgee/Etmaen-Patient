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
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.wallet.components.*

@Composable
fun WalletScreen(
    onNavigateBack: () -> Unit,
    onAddFunds: () -> Unit,
    onAddPaymentMethod: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    ScreenTopBar(
        title = stringResource(R.string.wallet_title),
        onLeadingClick = onNavigateBack
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    WalletContent(state, onAddFunds, onAddPaymentMethod)
}

@Composable
private fun WalletContent(
    state: WalletState,
    onAddFunds: () -> Unit,
    onAddPaymentMethod: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WalletBalanceCard(
            balanceLabel = stringResource(R.string.wallet_balance),
            balance = stringResource(
                R.string.wallet_balance_value,
                state.balance
            ),
            autoRefillText = stringResource(
                if (state.isAutoRefillEnabled) {
                    R.string.wallet_auto_refill_on
                } else {
                    R.string.wallet_auto_refill_off
                }
            ),
            addFundsText = stringResource(R.string.wallet_add_funds),
            onAddFunds = onAddFunds
        )
        SectionTitle(stringResource(R.string.wallet_payment_methods))
        Surface(
            color = Theme.colors.surface,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 2.dp
        ) {
            WalletActionRow(
                stringResource(R.string.wallet_cash),
                icon = painterResource(DR.drawable.ic_wallet_cash),
                onClick = onAddPaymentMethod,
                iconContainerShape = RoundedCornerShape(12.dp)
            )
        }
        OutlinedWalletAction(stringResource(R.string.wallet_add_payment_method), onAddPaymentMethod)
        SectionTitle(stringResource(R.string.wallet_ride_profiles))
        Surface(
            color = Theme.colors.surface,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 2.dp
        ) {
            Column {
                WalletActionRow(
                    stringResource(R.string.wallet_personal),
                    icon = painterResource(DR.drawable.ic_wallet_personal),
                    selected = true,
                    showChevron = false
                )
                HorizontalDivider(Modifier.padding(start = 72.dp), color = Theme.colors.divider)
                WalletActionRow(
                    stringResource(R.string.wallet_business),
                    stringResource(R.string.wallet_business_subtitle),
                    painterResource(DR.drawable.ic_wallet_business)
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text,
        color = Theme.colors.secondaryFont,
        style = Theme.typography.body.medium.copy(fontSize = 17.sp),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 12.dp, start = 3.dp, bottom = 2.dp)
    )
}

@Preview
@Composable
private fun WalletPreview() = SpTheme { WalletContent(WalletState(), {}, {}) }
