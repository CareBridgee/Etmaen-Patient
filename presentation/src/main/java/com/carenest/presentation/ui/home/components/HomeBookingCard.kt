package com.carenest.presentation.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.home.Booking
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R

@Composable
fun HomeBookingCard(
    booking: Booking?,
    onManageClick: () -> Unit,
    onBookingClick: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HomeBookingHeader(onManageClick = onManageClick)

        if (booking == null) {
            HomeBookingEmpty()
        } else {
            HomeBookingItem(
                booking = booking,
                onClick = onBookingClick
            )
        }
    }
}

@Composable
fun HomeBookingHeader(
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
            style = Theme.typography.body.large.copy(
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
fun HomeBookingEmpty(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .padding(20.dp),
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
fun HomeBookingItem(
    booking: Booking,
    onClick: (Booking) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .clickable { onClick(booking) }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = RD.drawable.img_placeholder),
                contentDescription = booking.providerName,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = booking.providerName,
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primaryFont
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Theme.colors.primaryContainer)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Theme.colors.primary)
                            )
                            Text(
                                text = booking.statusText,
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
                    text = booking.serviceName,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 13.sp
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = RD.drawable.ic_time),
                        contentDescription = null,
                        tint = Theme.colors.hint,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = booking.timeText,
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

