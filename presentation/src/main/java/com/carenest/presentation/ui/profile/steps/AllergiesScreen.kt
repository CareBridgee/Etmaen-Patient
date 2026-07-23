package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.carenest.presentation.ui.profile.ProfileAllergyOption
import com.carenest.domain.model.profile.AllergyType

@Composable
fun AllergiesScreen(
    hasNoKnownAllergies: Boolean,
    allergies: List<ProfileAllergyOption>,
    selectedAllergyKeys: Set<String>,
    otherAllergies: String,
    onNoKnownAllergiesToggle: () -> Unit,
    onAllergyToggle: (String) -> Unit,
    onOtherAllergiesChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isSubmitting: Boolean = false
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = onBack
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
                .padding(
                    horizontal = Theme.spacing.space20,
                    vertical = Theme.spacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.space22)
        ) {
            ProfileProgressIndicator(step = 3, title = stringResource(R.string.allergies_progress_title))

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                BasicText(
                    text = stringResource(R.string.allergies_title),
                    style = Theme.typography.displayMedium.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 23.sp
                    )
                )
                BasicText(
                    text = stringResource(R.string.allergies_description),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }

            NoKnownAllergiesCard(
                checked = hasNoKnownAllergies,
                onCheckedChange = onNoKnownAllergiesToggle
            )

            Column(
                modifier = Modifier.alpha(if (hasNoKnownAllergies) 0.45f else 1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.space22)
            ) {
                AllergyCategory(
                    title = stringResource(R.string.allergies_drug_title),
                    icon = Icons.Outlined.Medication,
                    options = allergies.filter { it.type == AllergyType.DRUG },
                    selectedOptions = selectedAllergyKeys,
                    enabled = !hasNoKnownAllergies,
                    onOptionClick = onAllergyToggle
                )
                AllergyCategory(
                    title = stringResource(R.string.allergies_food_title),
                    icon = Icons.Outlined.Restaurant,
                    options = allergies.filter { it.type == AllergyType.FOOD },
                    selectedOptions = selectedAllergyKeys,
                    enabled = !hasNoKnownAllergies,
                    onOptionClick = onAllergyToggle
                )
                AllergyCategory(
                    title = stringResource(R.string.allergies_other_title),
                    icon = Icons.Outlined.HealthAndSafety,
                    options = allergies.filter { it.type == AllergyType.OTHER },
                    selectedOptions = selectedAllergyKeys,
                    enabled = !hasNoKnownAllergies,
                    onOptionClick = onAllergyToggle
                )
                CustomTextField(
                    text = otherAllergies,
                    onTextChange = onOtherAllergiesChange,
                    title = stringResource(R.string.allergies_other_title),
                    hint = stringResource(R.string.allergies_other_hint),
                    enabled = !hasNoKnownAllergies,
                    fieldHeight = 96.dp,
                    fieldVerticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            continueEnabled = !isSubmitting,
            isLoading = isSubmitting
        )
    }
}

@Composable
private fun NoKnownAllergiesCard(checked: Boolean, onCheckedChange: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.cardBackground)
            .border(1.dp, Theme.colors.onDisable.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .clickable(onClick = onCheckedChange)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.HealthAndSafety,
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(28.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = stringResource(R.string.allergies_none_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontSize = 17.sp
                )
            )
            BasicText(
                text = stringResource(R.string.allergies_none_description),
                style = Theme.typography.body.small.copy(color = Theme.colors.secondaryFont)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Theme.colors.onPrimary,
                checkedTrackColor = Theme.colors.primary,
                uncheckedThumbColor = Theme.colors.surface,
                uncheckedTrackColor = Theme.colors.disable,
                uncheckedBorderColor = Theme.colors.onDisable
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AllergyCategory(
    title: String,
    icon: ImageVector,
    options: List<ProfileAllergyOption>,
    selectedOptions: Set<String>,
    enabled: Boolean,
    onOptionClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(22.dp)
            )
            BasicText(
                text = title,
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontSize = 18.sp
                )
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
        ) {
            options.forEach { option ->
                AllergyChip(
                    text = option.label,
                    selected = option.localKey in selectedOptions,
                    enabled = enabled,
                    onClick = { onOptionClick(option.localKey) }
                )
            }
        }
    }
}

@Composable
private fun AllergyChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) Theme.colors.primary else Theme.colors.backGround
    val contentColor = if (selected) Theme.colors.onPrimary else Theme.colors.secondaryFont
    val borderColor = if (selected) Theme.colors.primary else Theme.colors.onDisable

    BasicText(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(100.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                horizontal = Theme.spacing.medium,
                vertical = Theme.spacing.space9
            ),
        style = Theme.typography.body.small.copy(
            color = contentColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun AllergiesScreenPreview() {
    SpTheme {
        AllergiesScreen(
            hasNoKnownAllergies = false,
            allergies = listOf(
                ProfileAllergyOption("penicillin", "Penicillin", AllergyType.DRUG, com.carenest.domain.model.profile.CatalogSource.FALLBACK),
                ProfileAllergyOption("peanuts", "Peanuts", AllergyType.FOOD, com.carenest.domain.model.profile.CatalogSource.FALLBACK)
            ),
            selectedAllergyKeys = setOf("penicillin"),
            otherAllergies = "",
            onNoKnownAllergiesToggle = {},
            onAllergyToggle = {},
            onOtherAllergiesChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
