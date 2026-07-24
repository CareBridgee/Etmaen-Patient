package com.carenest.presentation.ui.request_service.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.carenest.designsystem.R
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.domain.model.LocationDetails

@Composable
fun AddressSection(
    location: LocationDetails?,
    onEditClick: () -> Unit,
    onMapClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, Theme.shapes.medium)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.backGround)
    ) {
        // Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(Theme.colors.surfaceVariant)
                .noRippleClickable(onClick = onMapClick),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = R.drawable.map_img_placeholder,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            
            // Precise tag
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Theme.spacing.medium)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.colors.primaryVariant)
                    .padding(horizontal = Theme.spacing.small, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    BasicText(
                        text = stringResource(id = R.string.request_service_precise),
                        style = Theme.typography.body.medium.copy(
                            color = Color.White, 
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }

        // Address Details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_location),
                contentDescription = null,
                tint = Theme.colors.primaryVariant,
                modifier = Modifier.size(32.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = location?.address ?: "No address selected",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
                location?.let {
                    BasicText(
                        text = listOfNotNull(it.apartment, it.district)
                            .filter { it.isNotBlank() }
                            .joinToString(", "),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.secondaryFont,
                            fontSize = 16.sp
                        )
                    )
                }
            }
            
            BasicText(
                text = stringResource(id = R.string.request_service_edit_address),
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryVariant,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                modifier = Modifier.noRippleClickable(onClick = onEditClick)
            )
        }
    }
}

@Preview
@Composable
private fun AddressSectionPreview() {
    SpTheme {
        Box(modifier = Modifier.background(Theme.colors.surfaceVariant).padding(16.dp)) {
            AddressSection(
                location = LocationDetails(
                    address = "123 Serenity Lane",
                    apartment = "Apt 4B",
                    district = "Health District",
                    latitude = 0.0,
                    longitude = 0.0
                ),
                onEditClick = {},
                onMapClick = {}
            )
        }
    }
}
