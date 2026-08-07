package com.carenest.presentation.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.wallet.components.PaymentOptionRow

@Composable
fun AddPaymentMethodScreen(
    onNavigateBack: () -> Unit,
    onCreditCardClick: () -> Unit,
    onPayPalClick: () -> Unit,
    onFawryCashClick: () -> Unit,
    onMeezaCardClick: () -> Unit,
    onMobileWalletClick: () -> Unit,
) {
    ScreenTopBar(
        title = stringResource(R.string.wallet_add_payment_method_title),
        onLeadingClick = onNavigateBack
    )
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp),
    ) {
        Text(
            stringResource(R.string.wallet_payment_description),
            color = Theme.colors.secondaryFont,
            style = Theme.typography.body.medium.copy(fontSize = 17.sp, lineHeight = 25.sp)
        )
        Spacer(Modifier.height(30.dp))
        Surface(color = Theme.colors.surface, shape = RoundedCornerShape(18.dp), shadowElevation = 2.dp) {
            Column {
                val options = listOf(
                    Triple(R.string.wallet_credit_card, DR.drawable.ic_wallet_card, onCreditCardClick),
                    Triple(R.string.wallet_paypal, DR.drawable.ic_bank, onPayPalClick),
                    Triple(R.string.wallet_fawry_cash, DR.drawable.ic_wallet_cash, onFawryCashClick),
                    Triple(R.string.wallet_meeza_card, DR.drawable.paymob_filled, onMeezaCardClick),
                    Triple(R.string.wallet_mobile_wallets, DR.drawable.ic_phone, onMobileWalletClick),
                )
                options.forEachIndexed { index, option ->
                    PaymentOptionRow(stringResource(option.first), painterResource(option.second), option.third, index < options.lastIndex)
                }
            }
        }
        Spacer(Modifier.height(48.dp))
        Row(Modifier.align(Alignment.CenterHorizontally), verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(DR.drawable.ic_verified), null, tint = Theme.colors.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(stringResource(R.string.wallet_secure_payments), color = Theme.colors.primary, style = Theme.typography.body.small.copy(fontSize = 13.sp), fontWeight = FontWeight.Medium)
        }
        Text(
            stringResource(R.string.wallet_secure_payments_description),
            color = Theme.colors.hint,
            style = Theme.typography.body.small.copy(fontSize = 12.sp, lineHeight = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 8.dp)
        )
    }
}

@Preview @Composable private fun AddPaymentPreview() = SpTheme { AddPaymentMethodScreen({}, {}, {}, {}, {}, {}) }
