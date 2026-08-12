package com.carenest.presentation.ui.visit_summary.components

import androidx.compose.foundation.background
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.visit_summary.VisitSummary
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD

@Composable
fun VisitSummaryCard(summary: VisitSummary, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.surface, RoundedCornerShape(20.dp))
            .padding(24.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.visit_completed_summary_title),
                style = Theme.typography.hint.large.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                ),
                color = Theme.colors.primary,
            )
            if (summary.isVerified) {
                Text(
                    text = stringResource(R.string.visit_completed_verified_badge),
                    style = Theme.typography.body.small.copy(fontWeight = FontWeight.Bold),
                    color = Theme.colors.primary,
                    modifier = Modifier
                        .background(
                            Theme.colors.primary.copy(alpha = 0.1f), RoundedCornerShape(50.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Theme.spacing.medium),
            color = Theme.colors.divider.copy(alpha = 0.5f)
        )

        // Nurse Info Row with Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = summary.professionalImageUrl,
                contentDescription = null,
                placeholder = painterResource(RD.drawable.nurse_image),
                error = painterResource(RD.drawable.nurse_image),
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            SummaryField(
                label = stringResource(R.string.visit_completed_professional_label),
                value = summary.professionalName,
                modifier = Modifier.weight(1f),
                icon = RD.drawable.ic_nurse_name
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            SummaryField(
                label = stringResource(R.string.visit_completed_service_type_label),
                value = summary.serviceType,
                modifier = Modifier.weight(1f),
                icon = summary.serviceIconUrl ?: RD.drawable.ic_service
            )
            SummaryField(
                label = stringResource(R.string.visit_completed_duration_label),
                value = stringResource(
                    R.string.visit_completed_duration_value,
                    summary.durationMinutes
                ),
                modifier = Modifier.weight(1f),
                icon = RD.drawable.ic_clock
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SummaryField(
            label = stringResource(R.string.visit_completed_date_label),
            value = summary.completedDate,
            modifier = Modifier.fillMaxWidth(),
            icon = RD.drawable.ic_date
        )
    }
}

@Composable
private fun SummaryField(
    label: String,
    value: String,
    icon: Any,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = Theme.typography.body.small,
            color = Theme.colors.secondaryFont,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            AsyncImage(
                model = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(Theme.colors.primary),
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = value,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Theme.colors.primaryFont,
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpTheme {
        VisitSummaryCard(
            summary = VisitSummary(
                requestId = "",
                professionalName = "Sarah Jenkins",
                professionalImageUrl = null,
                serviceType = "General Nursing",
                serviceIconUrl = null,
                durationMinutes = 60,
                completedDate = "30 Jun",
                totalAmount = 85.0,
                isVerified = true,
            )
        )
    }
}
