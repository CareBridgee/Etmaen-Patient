package com.carenest.presentation.ui.profilecompletion.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

private const val ProfileStepCount = 7

@Composable
fun ProfileProgressIndicator(
    step: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    val currentStep = step.coerceIn(1, ProfileStepCount)
    val progress = currentStep.toFloat() / ProfileStepCount

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicText(
                text = stringResource(
                    R.string.profile_step_format,
                    currentStep,
                    ProfileStepCount
                ),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            BasicText(
                text = title,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.hint
                )
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Theme.colors.disable)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(100.dp))
                    .background(Theme.colors.success)
            )
        }
    }
}
