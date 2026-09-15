package com.carenest.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.ServiceHistory
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD

@Composable
fun HomeHistoryHeader(
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_history_title),
            style = Theme.typography.title.copy(
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                fontSize = 18.sp
            )
        )

        TextButton(onClick = onManageClick) {
            Text(
                text = stringResource(R.string.home_bookings_manage),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Composable
fun HomeHistoryEmpty(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .padding(Theme.spacing.space20),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.home_bookings_empty),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            )
        )
    }
}

@Composable
fun HomeHistoryItem(
    serviceHistory: ServiceHistory,
    onClick: (ServiceHistory) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.large)
            .background(Theme.colors.surface)
            .clickable { onClick(serviceHistory) }
            .padding(Theme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space14),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Theme.size.large - Theme.spacing.small)
                    .clip(Theme.shapes.large)
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_profile),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(Theme.spacing.extraLarge)
                )

                serviceHistory.nurseProfileImageUrl
                    ?.takeIf(String::isNotBlank)
                    ?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = serviceHistory.nurseName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = serviceHistory.nurseName ?: stringResource(R.string.home_history_unassigned),
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primaryFont
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(Theme.shapes.medium)
                            .background(Theme.colors.primaryContainer)
                            .padding(horizontal = Theme.spacing.small, vertical = Theme.spacing.extraSmall / 2)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Theme.spacing.space6)
                                    .clip(CircleShape)
                                    .background(Theme.colors.primary)
                            )
                            Text(
                                text = serviceHistory.status,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = serviceHistory.serviceName,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 13.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_time),
                        contentDescription = null,
                        tint = Theme.colors.hint,
                        modifier = Modifier.size(Theme.spacing.space14)
                    )
                    Text(
                        text = "${serviceHistory.preferredDate}, ${serviceHistory.preferredTime.hour}:${serviceHistory.preferredTime.minute.toString().padStart(2, '0')}",
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.hint,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

