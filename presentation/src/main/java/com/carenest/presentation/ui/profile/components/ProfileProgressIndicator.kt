package com.carenest.presentation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

private const val ProfileStepCount = 9

@Composable
fun ProfileProgressIndicator(
    step: Int,
    title: String,
    modifier: Modifier = Modifier,
    showHealthProfileHeading: Boolean = false
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showHealthProfileHeading) {
            BasicText(
                text = stringResource(R.string.profile_onboarding_label),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                BasicText(
                    text = stringResource(R.string.profile_health_profile),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 18.sp
                    )
                )
                BasicText(
                    text = stringResource(R.string.profile_step_format, step, ProfileStepCount),
                    style = Theme.typography.body.small.copy(color = Theme.colors.hint)
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicText(
                    text = stringResource(R.string.profile_step_format, step, ProfileStepCount),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                BasicText(
                    text = title,
                    style = Theme.typography.body.small.copy(color = Theme.colors.hint)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Theme.colors.disable)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(step.toFloat() / ProfileStepCount)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(Theme.colors.success)
            )
        }
    }
}
