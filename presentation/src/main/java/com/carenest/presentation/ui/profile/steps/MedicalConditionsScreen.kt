package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Bloodtype
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.R
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

private data class ConditionOption(val label: String, val icon: ImageVector)

@Composable
fun MedicalConditionsScreen(
    selectedConditions: Set<String>,
    otherConditions: String,
    onConditionToggle: (String) -> Unit,
    onOtherConditionsChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = onBack
    )

    val conditions = listOf(
        ConditionOption(stringResource(R.string.condition_diabetes), Icons.Outlined.Bloodtype),
        ConditionOption(stringResource(R.string.condition_hypertension), Icons.Outlined.MonitorHeart),
        ConditionOption(stringResource(R.string.condition_heart_disease), Icons.Outlined.FavoriteBorder),
        ConditionOption(stringResource(R.string.condition_asthma), Icons.Outlined.Air),
        ConditionOption(stringResource(R.string.condition_copd), Icons.Outlined.HealthAndSafety),
        ConditionOption(stringResource(R.string.condition_epilepsy), Icons.Outlined.Psychology),
        ConditionOption(stringResource(R.string.condition_liver_disease), Icons.Outlined.MedicalServices)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            ProfileProgressIndicator(
                step = 2,
                title = stringResource(R.string.medical_conditions_progress_title)
            )

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.space6)) {
                BasicText(
                    text = stringResource(R.string.medical_conditions_title),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 18.sp
                    )
                )
                BasicText(
                    text = stringResource(R.string.medical_conditions_description),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
                conditions.chunked(2).forEach { rowConditions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
                    ) {
                        rowConditions.forEach { condition ->
                            ConditionCard(
                                option = condition,
                                selected = condition.label in selectedConditions,
                                onClick = { onConditionToggle(condition.label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowConditions.size == 1) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                BasicText(
                    text = stringResource(R.string.medical_conditions_other_title),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 17.sp
                    )
                )
                CustomTextField(
                    text = otherConditions,
                    onTextChange = onOtherConditionsChange,
                    hint = stringResource(R.string.medical_conditions_other_hint),
                    fieldHeight = 88.dp,
                    fieldVerticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue
        )
    }
}

@Composable
private fun ConditionCard(
    option: ConditionOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) Theme.colors.primary else Theme.colors.onDisable
    val backgroundColor = if (selected) Theme.colors.primaryContainer else Theme.colors.surface

    Column(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(
                horizontal = Theme.spacing.space14,
                vertical = Theme.spacing.space10
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(19.dp)
        )
        BasicText(
            text = option.label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicalConditionsScreenPreview() {
    SpTheme {
        MedicalConditionsScreen(
            selectedConditions = setOf("Hypertension", "Asthma"),
            otherConditions = "",
            onConditionToggle = {},
            onOtherConditionsChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
