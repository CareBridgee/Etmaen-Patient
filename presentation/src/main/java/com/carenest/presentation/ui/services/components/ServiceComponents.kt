package com.carenest.presentation.ui.services.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@Composable
fun ServiceCategoryCard(
    title: String,
    subtitle: String,
    icon: Painter,
    height: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(2.dp, Theme.shapes.large)
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Theme.colors.primaryVariant,
                modifier = Modifier.size(24.dp),
            )
        }
        Spacer(Modifier.height(Theme.spacing.space12))
        BasicText(
            text = title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Normal,
            ),
        )
        Spacer(Modifier.height(2.dp))
        BasicText(
            text = subtitle,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
        )
    }
}

@Composable
fun ServiceSurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.large)
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.large),
        content = content,
    )
}

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
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont),
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

@Composable
fun ServiceChecklistItem(
    text: String,
    checkIcon: Painter,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
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
            style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            modifier = Modifier.weight(1f),
        )
    }
}

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
            .background(Theme.colors.primaryContainer.copy(alpha = 0.72f))
            .border(1.dp, Theme.colors.primaryVariant.copy(alpha = 0.28f), Theme.shapes.large)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
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
            style = Theme.typography.body.small.copy(color = Theme.colors.primary),
            modifier = Modifier.weight(1f),
        )
    }
}
