package com.carenest.presentation.ui.servicedetails.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun ServiceInformationNote(
    text: String,
    infoIcon: Painter,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(
                Theme.colors.primaryContainer.copy(alpha = 0.72f),
            )
            .border(
                width = 1.dp,
                color = Theme.colors.primaryVariant.copy(alpha = 0.28f),
                shape = Theme.shapes.large,
            )
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(
            Theme.spacing.space12,
        ),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            painter = infoIcon,
            contentDescription = null,
            tint = Theme.colors.primaryVariant,
            modifier = Modifier.size(22.dp),
        )

        BasicText(
            text = text,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primary,
            ),
            modifier = Modifier.weight(1f),
        )
    }
}