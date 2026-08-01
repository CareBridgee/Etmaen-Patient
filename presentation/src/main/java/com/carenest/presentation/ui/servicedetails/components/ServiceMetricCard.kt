package com.carenest.presentation.ui.servicedetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun ServiceMetricCard(
    label: String,
    value: String,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .shadow(1.dp, Theme.shapes.large)
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Theme.colors.primaryVariant,
            modifier = Modifier.size(24.dp),
        )

        Column {
            BasicText(
                text = label,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                ),
            )

            BasicText(
                text = value,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                ),
            )
        }
    }
}