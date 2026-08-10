package com.carenest.presentation.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.home.HealthcareService
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import androidx.compose.ui.platform.LocalContext
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R

@Composable
fun HomeServicesGrid(
    services: List<HealthcareService>,
    isSearchEmpty: Boolean,
    onViewAllClick: () -> Unit,
    onServiceClick: (HealthcareService) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_services_title),
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )

            TextButton(onClick = onViewAllClick) {
                Text(
                    text = stringResource(R.string.home_services_view_all),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        if (isSearchEmpty) {
            EmptyState(
                title = stringResource(R.string.home_services_search_empty_title),
                description = stringResource(R.string.home_services_search_empty_desc),
                accentColor = Theme.colors.primary
            )
        } else if (services.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.home_services_empty_title),
                description = stringResource(R.string.home_services_empty_desc),
                accentColor = Theme.colors.primary
            )
        } else {
            val chunked = services.chunked(2)
            chunked.forEach { rowServices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowServices.forEach { service ->
                        ServiceCard(
                            service = service,
                            onClick = { onServiceClick(service) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowServices.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: HealthcareService,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUrl = service.iconResName?.startsWith("http") == true
    val displayName = service.name

    val fallbackIcon = when (service.iconResName) {
        "ic_syringe" -> RD.drawable.ic_syringe
        "ic_pill" -> RD.drawable.ic_pill
        "ic_physical_therapy" -> RD.drawable.ic_physical_therapy
        "ic_services" -> RD.drawable.ic_services
        "ic_tracking" -> RD.drawable.ic_location
        else -> RD.drawable.ic_heart_beat
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.surface)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (isUrl) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(service.iconResName)
                            .crossfade(true)
                            .build(),
                        contentDescription = displayName,
                        modifier = Modifier.size(28.dp),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = fallbackIcon),
                    )
                } else {
                    Icon(
                        painter = painterResource(id = fallbackIcon),
                        contentDescription = displayName,
                        tint = Theme.colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Text(
                text = displayName,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.primaryFont,
                    fontSize = 14.sp
                )
            )
        }
    }
}
