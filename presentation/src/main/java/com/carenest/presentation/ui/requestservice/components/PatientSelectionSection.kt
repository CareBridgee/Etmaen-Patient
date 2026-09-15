package com.carenest.presentation.ui.requestservice.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R
import com.carenest.designsystem.components.chip.AppChip
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.bounceClick
import com.carenest.designsystem.util.noRippleClickable
import com.carenest.domain.model.Patient

@Composable
fun PatientSelectionSection(
    patients: List<Patient>,
    selectedPatientId: String?,
    onPatientSelected: (Patient) -> Unit,
    onEditProfileClick: () -> Unit,
    onAddPatientClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicText(
                text = stringResource(id = R.string.request_service_patient_label),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            BasicText(
                text = stringResource(id = R.string.request_service_edit_profile),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryVariant,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.noRippleClickable(onClick = onEditProfileClick)
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(patients, key = { it.id }) { patient ->
                val name = listOfNotNull(patient.firstName, patient.lastName).joinToString(" ")
                AppChip(
                    label = if (name.isBlank()) stringResource(id = R.string.common_guest) else name,
                    selected = patient.id == selectedPatientId,
                    onClick = { onPatientSelected(patient) }
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, Theme.colors.hint), CircleShape)
                        .bounceClick(shape = CircleShape, onClick = onAddPatientClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Theme.colors.hint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PatientSelectionSectionPreview() {
    SpTheme {
        Box(modifier = Modifier
            .background(Color.White)
            .padding(16.dp)) {
            PatientSelectionSection(
                patients = listOf(
                    Patient(
                        id = "1",
                        phoneNumber = "+20123456789",
                        firstName = "Mai",
                        lastName = "",
                        dateOfBirth = "1995-01-01",
                        gender = "Female",
                        profileImageUrl = "",
                        isDeleted = false,
                        createdAt = "2023-01-01T10:00:00Z",
                        updatedAt = "2023-01-01T10:00:00Z",
                        lastLoginAt = "2023-01-01T10:00:00Z",
                        defaultProfileId = "1"
                    ),
                    Patient(
                        id = "2",
                        phoneNumber = "+20123456789",
                        firstName = "Mai",
                        lastName = "",
                        dateOfBirth = "1995-01-01",
                        gender = "Female",
                        profileImageUrl = "",
                        isDeleted = false,
                        createdAt = "2023-01-01T10:00:00Z",
                        updatedAt = "2023-01-01T10:00:00Z",
                        lastLoginAt = "2023-01-01T10:00:00Z",
                        defaultProfileId = "2"
                    )
                ),

                selectedPatientId = "2",
                onPatientSelected = {},
                onEditProfileClick = {},
                onAddPatientClick = {}
            )
        }
    }
}
