package com.carenest.presentation.ui.search_for_nurse.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SecondaryButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.ui.search_for_nurse.NearbyNurse
import com.carenest.designsystem.R as RD

@Composable
fun NurseOfferCard(
    nurse: NearbyNurse, onAccept: () -> Unit, onDecline: () -> Unit
) {
    val colors = Theme.colors
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            1.dp, Theme.colors.tint.copy(alpha = 0.15f)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top
            ) {

                Image(
                    painter = painterResource(RD.drawable.nurse_image),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .border(
                            2.dp, Theme.colors.primaryVariant.copy(alpha = 0.40f), CircleShape
                        )
                        .padding(2.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = stringResource(R.string.nurse_name_format, nurse.name),
                        style = Theme.typography.title.copy(
                            fontWeight = FontWeight.SemiBold,
                        ), color = Theme.colors.primaryFont
                    )

                    Text(
                        text = nurse.title,
                        style = Theme.typography.body.medium,
                        color = Theme.colors.primaryFont
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    Theme.colors.success, CircleShape
                                )
                        )

                        Spacer(Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = colors.warning,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = "${nurse.rating}", style = Theme.typography.body.medium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = stringResource(
                                R.string.nurse_review_format,
                                nurse.reviewCount,
                                stringResource(R.string.reviews)
                            ),
                            style = Theme.typography.body.medium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.nurse_price_format, nurse.price),
                    style = Theme.typography.title.copy(
                        fontWeight = FontWeight.SemiBold
                    ), color = Theme.colors.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                color = Theme.colors.hint.copy(0.30f)
            )

            Spacer(Modifier.height(16.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(RD.drawable.ic_location),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = stringResource(
                        R.string.nurse_area_format,
                        nurse.area,
                        nurse.distanceKm,
                        stringResource(R.string.km)
                    ),
                    style = Theme.typography.body.medium,
                    color = colors.secondaryFont
                )
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(
                color = Theme.colors.hint.copy(0.30f)
            )

            Spacer(Modifier.height(20.dp))


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
            nurse = NearbyNurse(
            id = "1",
            name = "Jane Doe",
            title = "Nurse",
            area = "Area",
            price = 58.0,
            rating = 4.1,
            reviewCount = 5,
            distanceKm = 1.5,
            avatarUrl = ""
        ), onAccept = { }, onDecline = { })
    }
}