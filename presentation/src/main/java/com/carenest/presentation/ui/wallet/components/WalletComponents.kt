package com.carenest.presentation.ui.wallet.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun WalletBalanceCard(
    balance: String,
    autoRefillText: String,
    addFundsText: String,
    onAddFunds: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(Theme.colors.primary, Theme.colors.primaryVariant)),
                RoundedCornerShape(22.dp)
            )
            .padding(22.dp)
    ) {
        Text(
            autoRefillText.substringBefore(":"),
            color = Theme.colors.onPrimary.copy(alpha = .85f),
            style = Theme.typography.body.small,
            fontWeight = FontWeight.Medium
        )
        Text(
            balance,
            color = Theme.colors.onPrimary,
            style = Theme.typography.display,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Surface(color = Theme.colors.onPrimary.copy(alpha = .12f), shape = CircleShape) {
            Text(
                autoRefillText.substringAfter(":", autoRefillText),
                color = Theme.colors.onPrimary,
                style = Theme.typography.body.small,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onAddFunds,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Theme.colors.surface,
                contentColor = Theme.colors.primary
            ),
            shape = RoundedCornerShape(14.dp)
        ) { Text("+  $addFundsText", fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
fun WalletActionRow(
    title: String,
    subtitle: String? = null,
    icon: Painter,
    onClick: (() -> Unit)? = null,
    showChevron: Boolean = true,
    selected: Boolean = false,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Theme.colors.cardBackground,
            modifier = Modifier.size(42.dp)
        ) {
            Icon(icon, null, tint = Theme.colors.primary, modifier = Modifier.padding(11.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Theme.colors.primaryFont,
                style = Theme.typography.body.medium,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) Text(
                subtitle,
                color = Theme.colors.secondaryFont,
                style = Theme.typography.body.small
            )
        }
        if (selected) Box(
            Modifier
                .size(7.dp)
                .background(Theme.colors.primary, CircleShape)
        )
        else if (showChevron) Text("›", color = Theme.colors.hint, style = Theme.typography.title)
    }
}

@Composable
fun OutlinedWalletAction(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        border = BorderStroke(1.5.dp, Theme.colors.onDisable),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Theme.colors.primary)
    ) { Text("+  $text", fontWeight = FontWeight.SemiBold) }
}

@Composable
fun PaymentOptionRow(title: String, icon: Painter, onClick: () -> Unit, showDivider: Boolean) {
    Column {
        WalletActionRow(title = title, icon = icon, onClick = onClick)
        if (showDivider) HorizontalDivider(
            Modifier.padding(start = 62.dp),
            color = Theme.colors.divider
        )
    }
}
