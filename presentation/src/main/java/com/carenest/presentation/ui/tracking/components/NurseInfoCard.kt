package com.carenest.presentation.ui.tracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun NurseInfoCard(
    nurseName: String,
    reviewsCount: Int,
    rating: Double,
    estimatedArrivalTime: String,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit,
    isOnline: Boolean = false,
    photoUrl: String? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Theme.colors.surface,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            NurseAvatar(isOnline = isOnline, photoUrl = photoUrl)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nurseName,
                    style = Theme.typography.title,
                    color = Theme.colors.primaryFont,
                    maxLines = 1,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(com.carenest.designsystem.R.drawable.ic_verified),
                        contentDescription = null,
                        tint = Theme.colors.primary,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.nurse_on_the_way_reviews_count, reviewsCount),
                        style = Theme.typography.body.small.copy(fontWeight = FontWeight.SemiBold),
                        color = Theme.colors.primary,
                    )
                }

            }
            RatingBadge(rating = rating)
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 14.dp),
            color = Theme.colors.divider,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.nurse_on_the_way_estimated_arrival_label),
                    style = Theme.typography.body.small,
                    color = Theme.colors.secondaryFont,
                )
                Text(
                    text = estimatedArrivalTime,
                    style = Theme.typography.title,
                    color = Theme.colors.primary,
                )
            }
            Row {
                ActionIconButton(
                    icon = painterResource(com.carenest.designsystem.R.drawable.ic_phone),
                    contentDescription = stringResource(R.string.nurse_on_the_way_call_nurse_content_description),
                    onClick = onCallClick,
                )
                Spacer(modifier = Modifier.width(10.dp))
                ActionIconButton(
                    icon = painterResource(com.carenest.designsystem.R.drawable.ic_message),
                    contentDescription = stringResource(R.string.nurse_on_the_way_message_nurse_content_description),
                    onClick = onMessageClick,
                )
            }
        }
    }
}

@Composable
private fun NurseAvatar(isOnline: Boolean, photoUrl: String?, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(56.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = Theme.colors.primaryContainer,
                    shape = RoundedCornerShape(14.dp),
                )
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                )
            }
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Theme.colors.success, CircleShape)
                    .border(2.dp, Theme.colors.surface, CircleShape)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun RatingBadge(rating: Double, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(
                color = Theme.colors.primaryContainer,
                shape = RoundedCornerShape(20.dp),
            )
            .padding(horizontal = Theme.spacing.space10, vertical = Theme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Theme.colors.hint,
            modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = rating.toString(),
            style = Theme.typography.body.small,
            color = Theme.colors.hint,
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(44.dp)
            .background(
                color = Theme.colors.disable,
                shape = RoundedCornerShape(12.dp),
            ),
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = Theme.colors.primary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        NurseInfoCard(
            nurseName = "Mark Harrison",
            reviewsCount = 5,
            rating = 4.0,
            estimatedArrivalTime = "9:05 am",
            onCallClick = {},
            onMessageClick = {},
        )
    }

}
