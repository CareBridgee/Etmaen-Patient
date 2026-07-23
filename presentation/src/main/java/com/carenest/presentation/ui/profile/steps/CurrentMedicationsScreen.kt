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
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.profile.MedicationInput
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation
import com.carenest.domain.model.profile.MedicationValidationErrors
import com.carenest.presentation.ui.profile.validation.localizedMessage

@Composable
fun CurrentMedicationsScreen(
    hasNoCurrentMedications: Boolean,
    medications: List<MedicationInput>,
    selectionError: String? = null,
    medicationErrors: Map<Long, MedicationValidationErrors> = emptyMap(),
    onNoCurrentMedicationsToggle: () -> Unit,
    onMedicationNameChange: (Int, String) -> Unit,
    onAddMedication: () -> Unit,
    onRemoveMedication: (Int) -> Unit,
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
                    vertical = Theme.spacing.large
                ),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
        ) {
            ProfileProgressIndicator(
                step = 4,
                title = stringResource(R.string.current_medications_progress)
            )

            Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                BasicText(
                    text = stringResource(R.string.current_medications_title),
                    style = Theme.typography.displayMedium.copy(
                        color = Theme.colors.primaryFont
                    )
                )
                BasicText(
                    text = stringResource(R.string.current_medications_description),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                )
            }

            NoCurrentMedicationsCard(
                checked = hasNoCurrentMedications,
                onCheckedChange = onNoCurrentMedicationsToggle
            )
            selectionError?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.small.copy(color = Theme.colors.error)
                )
            }

            Column(
                modifier = Modifier.alpha(if (hasNoCurrentMedications) 0.4f else 1f),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.space12)
            ) {
                medications.forEachIndexed { index, medication ->
                    MedicationEntry(
                        medication = medication,
                        validationErrors = medicationErrors[medication.uiKey],
                        enabled = !hasNoCurrentMedications,
                        onNameChange = { onMedicationNameChange(index, it) },
                        onRemove = { onRemoveMedication(index) }
                    )
                }

                AddMedicationButton(
                    enabled = !hasNoCurrentMedications && medications.size < MAX_MEDICATIONS,
                    onClick = onAddMedication
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
private fun NoCurrentMedicationsCard(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.cardBackground)
            .clickable(onClick = onCheckedChange)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = stringResource(R.string.current_medications_none_title),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            )
            BasicText(
                text = stringResource(R.string.current_medications_none_description),
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
                uncheckedTrackColor = Theme.colors.onDisable,
                uncheckedBorderColor = Theme.colors.onDisable
            )
        )
    }
}

@Composable
private fun MedicationEntry(
    medication: MedicationInput,
    validationErrors: MedicationValidationErrors? = null,
    enabled: Boolean,
    onNameChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.colors.surface)
            .padding(Theme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            text = medication.name,
            onTextChange = onNameChange,
            hint = stringResource(R.string.current_medications_name_hint),
            enabled = enabled,
            borderColor = Theme.colors.surfaceVariant,
            containerColor = Theme.colors.cardBackground,
            isError = validationErrors?.name != null,
            errorMessage = validationErrors?.name.localizedMessage(),
            singleLine = false,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Outlined.DeleteOutline,
            contentDescription = stringResource(R.string.current_medications_delete),
            tint = Theme.colors.error,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(enabled = enabled, onClick = onRemove)
                .padding(Theme.spacing.space10)
        )
    }
}

@Composable
private fun AddMedicationButton(enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Theme.colors.onDisable,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.AddCircleOutline,
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        BasicText(
            text = stringResource(R.string.current_medications_add),
            modifier = Modifier.padding(start = Theme.spacing.space12),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

private const val MAX_MEDICATIONS = 10

@Preview(showBackground = true)
@Composable
private fun CurrentMedicationsScreenPreview() {
    SpTheme {
        CurrentMedicationsScreen(
            hasNoCurrentMedications = false,
            medications = listOf(MedicationInput(uiKey = 0L, name = "Lisinopril 10mg")),
            onNoCurrentMedicationsToggle = {},
            onMedicationNameChange = { _, _ -> },
            onAddMedication = {},
            onRemoveMedication = {},
            onBack = {},
            onContinue = {}
        )
    }
}
