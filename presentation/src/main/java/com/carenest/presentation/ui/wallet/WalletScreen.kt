package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.formatPrice
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.wallet.components.*

@Composable
fun WalletScreen(
    onNavigateBack: () -> Unit,
    onAddFunds: () -> Unit,
    onShowMessage: (String) -> Unit,
    viewModel: WalletViewModel = hiltViewModel(),
) {
    ScreenTopBar(
        title = stringResource(R.string.wallet_title),
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

    WalletContent(
        state = state,
        onAddFunds = onAddFunds,
        onRetry = { viewModel.onEvent(WalletIntent.RefreshBalance) },
    )
}

@Composable
private fun WalletContent(
    state: WalletState,
    onAddFunds: () -> Unit,
    onRetry: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .padding(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val balance = when (val balanceState = state.balanceState) {
            WalletBalanceState.Loading -> stringResource(R.string.wallet_balance_loading)
            WalletBalanceState.Empty -> formatPrice(0.0)
            is WalletBalanceState.Available -> formatPrice(balanceState.credit)
            is WalletBalanceState.Failure -> stringResource(R.string.wallet_balance_unavailable)
        }
        val status = when (state.balanceState) {
            WalletBalanceState.Loading -> stringResource(R.string.wallet_balance_loading_description)
            WalletBalanceState.Empty -> stringResource(R.string.wallet_balance_empty)
            is WalletBalanceState.Available -> stringResource(R.string.wallet_balance_available)
            is WalletBalanceState.Failure -> stringResource(R.string.wallet_balance_load_failed)
        }

        WalletBalanceCard(
            balanceLabel = stringResource(R.string.wallet_balance),
            balance = balance,
            autoRefillText = status,
            addFundsText = stringResource(R.string.wallet_add_funds),
            onAddFunds = onAddFunds
        )

        if (state.balanceState is WalletBalanceState.Failure) {
            SecondaryButton(
                caption = stringResource(R.string.retry),
                onClick = onRetry,
                modifier = Modifier.fillMaxWidth(),
            )
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
