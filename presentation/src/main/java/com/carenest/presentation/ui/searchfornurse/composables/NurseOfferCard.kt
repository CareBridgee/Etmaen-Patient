package com.carenest.presentation.ui.searchfornurse.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.socket.model.NurseOfferResponse
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD

@Composable
fun NurseOfferCard(
    offer: NurseOfferResponse,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val colors = Theme.colors

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Theme.colors.tint.copy(alpha = 0.15f)),
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nurse avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .border(2.dp, Theme.colors.primaryVariant.copy(alpha = 0.40f), CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.surface),
                    contentAlignment = Alignment.Center
                ) {
                    if (offer.nurse?.photoUrl != null) {
                        AsyncImage(
                            model = offer.nurse?.photoUrl ?: RD.drawable.nurse_image,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Theme.colors.hint,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val nurseName = offer.nurse?.let { "${it.firstName} ${it.lastName}" } ?: "Nurse"
                    Text(
                        text = nurseName,
                        style = Theme.typography.title.copy(fontWeight = FontWeight.SemiBold),
                        color = Theme.colors.primaryFont
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        offer.nurse?.let { nurse ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Theme.colors.warning,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "${nurse.ratingAvg} (${nurse.totalReviews})",
                                style = Theme.typography.body.small,
                                color = Theme.colors.secondaryFont
                            )
                        }
                    }
                    Text(
                        text = offer.message ?: "",
                        style = Theme.typography.body.medium,
                        color = Theme.colors.secondaryFont
                    )
                }

                Text(
                    text = stringResource(R.string.nurse_price_format, offer.proposedPrice),
                    style = Theme.typography.title.copy(fontWeight = FontWeight.SemiBold),
                    color = Theme.colors.primary
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Theme.colors.hint.copy(0.30f))
            Spacer(Modifier.height(16.dp))

            // Date & Time row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(RD.drawable.ic_location),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${offer.proposedDate}  ${offer.proposedTime}",
                    style = Theme.typography.body.medium,
                    color = colors.secondaryFont
                )
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Theme.colors.hint.copy(0.30f))
            Spacer(Modifier.height(20.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SecondaryButton(
                    caption = stringResource(R.string.decline),
                    onClick = onDecline,
                    modifier = Modifier.weight(1f)
                )
                PrimaryButton(
                    caption = stringResource(R.string.accept),
                    onClick = onAccept,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        NurseOfferCard(
            offer = NurseOfferResponse(
                id = "offer-1",
                serviceRequestId = "req-123",
                nurseId = "nurse-456",
                nurse = com.carenest.domain.socket.model.NurseInfo(
                    id = "nurse-456",
                    firstName = "Sarah",
                    lastName = "Mitchell",
                    ratingAvg = 4.8,
                    totalReviews = 124
                ),
                proposedPrice = 58.0,
                proposedDate = "2025-08-01",
                proposedTime = "10:00 AM",
                message = "I can help you with the service.",
                status = "PENDING",
                createdAt = "2025-08-01T07:00:00Z",
                updatedAt = "2025-08-01T07:00:00Z"
            ),
            onAccept = {},
            onDecline = {}
        )
    }
}