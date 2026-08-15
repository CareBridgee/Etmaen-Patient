package com.carenest.presentation.ui.servicelist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.designsystem.R
import com.carenest.designsystem.theme.Theme

@Composable
fun ServiceCategoryCard(
    title: String,
    subtitle: String,
    icon: String?,
    height: Dp,
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
            if (icon != null) {
                AsyncImage(
                    model = icon,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.ic_services),
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_services),
                    contentDescription = null,
                    tint = Theme.colors.primaryVariant,
                    modifier = Modifier.size(24.dp),
                    )
            }
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
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
            ),
        )
    }
}