package com.carenest.presentation.ui.request_service.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R
import com.carenest.designsystem.components.cards.PaymentMethodCard
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.PaymentMethod

@Composable
fun PaymentMethodSection(
    paymentMethods: List<PaymentMethod>,
    onMethodSelected: (PaymentMethod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        BasicText(
            text = stringResource(id = R.string.request_service_payment_method_label),
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold
            )
        )

        Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
            paymentMethods.forEach { method ->
                PaymentMethodCard(
                    title = method.title,
                    description = method.description,
                    painter = painterResource(id = R.drawable.ic_wallet),
                    selected = method.isSelected,
                    onClick = { onMethodSelected(method) },
                    subDescription = method.subDescription.ifBlank { null }
                )
            }
        }
    }
}

@Preview
@Composable
private fun PaymentMethodSectionPreview() {
    SpTheme {
        Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
            PaymentMethodSection(
                paymentMethods = emptyList(),
                onMethodSelected = {}
            )
        }
    }
}
