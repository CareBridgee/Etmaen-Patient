package com.carenest.presentation.ui.servicedetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun ServiceChecklistItem(
    text: String,
    checkIcon: Painter,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            Theme.spacing.space12,
        ),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = checkIcon,
                contentDescription = null,
                tint = Theme.colors.primaryVariant,
                modifier = Modifier.size(14.dp),
            )
        }

        BasicText(
            text = text,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
            ),
            modifier = Modifier.weight(1f),
        )
    }
}