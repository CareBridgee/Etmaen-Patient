package com.carenest.presentation.ui.requestservice.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.domain.model.home.HealthcareService

@Composable
fun ChosenServiceCard(
    service: HealthcareService?,
    onDetailsClick: () -> Unit,
    onChangeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, Theme.shapes.medium)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.backGround)
            .noRippleClickable(onClick = onDetailsClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_physical_therapy), // Placeholder
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = stringResource(id = com.carenest.designsystem.R.string.request_service_chosen_service),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
            BasicText(
                text = service?.name ?: "",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.extraSmall)
        ) {
            BasicText(
                text = stringResource(id = com.carenest.designsystem.R.string.request_service_change_service),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryVariant,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.noRippleClickable(onClick = onChangeClick)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Theme.colors.hint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
private fun ChosenServiceCardPreview() {
    com.carenest.designsystem.theme.SpTheme {
        Box(modifier = Modifier.background(Color.White).padding(16.dp)) {
            ChosenServiceCard(
                service = null,
                onDetailsClick = {},
                onChangeClick = {}
            )
        }
    }
}
