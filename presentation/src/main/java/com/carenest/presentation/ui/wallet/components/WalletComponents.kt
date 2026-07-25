package com.carenest.presentation.ui.wallet.components

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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.Theme
import com.google.common.math.LinearTransformation.horizontal

@Composable
fun WalletBalanceCard(balance: String, autoRefillText: String, addFundsText: String, onAddFunds: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().background(
            Brush.linearGradient(listOf(Theme.colors.primary, Theme.colors.primaryVariant)),
            RoundedCornerShape(22.dp)
        ).padding(22.dp)
    ) {
        Text(autoRefillText.substringBefore(":"), color = Theme.colors.onPrimary.copy(alpha = .85f), style = Theme.typography.body.small, fontWeight = FontWeight.Medium)
        Text(balance, color = Theme.colors.onPrimary, style = Theme.typography.display, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Surface(color = Theme.colors.onPrimary.copy(alpha = .12f), shape = CircleShape) {
            Text(autoRefillText.substringAfter(":", autoRefillText), color = Theme.colors.onPrimary, style = Theme.typography.body.small, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
        }
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onAddFunds,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.surface, contentColor = Theme.colors.primary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                painter = painterResource(DR.drawable.ic_wallet_plus),
                contentDescription = null,
                modifier = Modifier.size(21.dp)
            )
            Spacer(Modifier.width(9.dp))
            Text(addFundsText, style = Theme.typography.body.medium, fontWeight = FontWeight.SemiBold)
        }
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
    iconContainerShape: Shape = CircleShape,
    iconContainerColor: Color = Theme.colors.cardBackground,
    iconTint: Color = Theme.colors.primary,
    subtitleColor: Color = Theme.colors.secondaryFont,
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 72.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = iconContainerShape, color = iconContainerColor, modifier = Modifier.size(48.dp)) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.padding(13.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Theme.colors.primaryFont, style = Theme.typography.body.medium, fontWeight = FontWeight.Medium)
            if (subtitle != null) Text(subtitle, color = subtitleColor, style = Theme.typography.body.small)
        }
        if (selected) Box(Modifier.size(8.dp).background(Theme.colors.primary, CircleShape))
        else if (showChevron) Icon(
            painterResource(DR.drawable.ic_wallet_chevron),
            contentDescription = null,
            tint = Theme.colors.hint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun OutlinedWalletAction(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .dashedRoundedBorder(Theme.colors.onDisable, 18.dp)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(DR.drawable.ic_wallet_plus),
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(21.dp)
        )
        Spacer(Modifier.width(9.dp))
        Text(text, color = Theme.colors.primary, style = Theme.typography.body.medium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun PaymentOptionRow(title: String, icon: Painter, onClick: () -> Unit, showDivider: Boolean) {
    Column {
        WalletActionRow(title = title, icon = icon, onClick = onClick)
        if (showDivider) HorizontalDivider(Modifier.padding(start = 62.dp), color = Theme.colors.divider)
    }
}

@Composable
fun RequiredPaymentMethodRow(title: String, requiredText: String, onClick: () -> Unit) {
    WalletActionRow(
        title = title,
        subtitle = requiredText,
        icon = painterResource(DR.drawable.ic_wallet_plus),
        onClick = onClick,
        iconContainerColor = Theme.colors.error.copy(alpha = .10f),
        iconTint = Theme.colors.error,
        subtitleColor = Theme.colors.error
    )
}

fun Modifier.dashedRoundedBorder(
    color: Color,
    cornerRadius: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 1.5.dp,
    dashLength: androidx.compose.ui.unit.Dp = 7.dp,
    gapLength: androidx.compose.ui.unit.Dp = 5.dp,
): Modifier = drawWithCache {
    val stroke = strokeWidth.toPx()
    val radius = cornerRadius.toPx()
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(dashLength.toPx(), gapLength.toPx())
    )
    onDrawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius),
            style = Stroke(width = stroke, pathEffect = pathEffect)
        )
    }
}
