package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

@Composable
fun EmergencyContactScreen(
    contactName: String,
    relationship: EmergencyRelationship?,
    phoneNumber: String,
    dataLoaded: Boolean = true,
    editingUnavailable: Boolean = false,
    contactNameError: String? = null,
    relationshipError: String? = null,
    phoneNumberError: String? = null,
    onContactNameChange: (String) -> Unit,
    onRelationshipSelected: (EmergencyRelationship) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isSubmitting: Boolean = false
) {
    ScreenTopBar(
        title = "Emergency Contact",
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
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraLarge)
        ) {
            ProfileProgressIndicator(
                step = 7,
                title = "Emergency Contact"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Theme.colors.surface)
                    .padding(Theme.spacing.large),
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.large)
            ) {
                BasicText(
                    text = "Emergency Contact Details",
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                RelationshipDropdown(
                    relationship = relationship,
                    enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                    errorMessage = relationshipError,
                    onRelationshipSelected = onRelationshipSelected
                )

                CustomTextField(
                    text = contactName,
                    onTextChange = onContactNameChange,
                    title = "Contact Name",
                    hint = "Enter contact name",
                    leadingIcon = rememberVectorPainter(Icons.Outlined.PersonOutline),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                    isError = contactNameError != null,
                    errorMessage = contactNameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                CustomTextField(
                    text = phoneNumber,
                    onTextChange = onPhoneNumberChange,
                    title = "Phone Number",
                    hint = "+1234567890",
                    leadingIcon = rememberVectorPainter(Icons.Outlined.Call),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                    isError = phoneNumberError != null,
                    errorMessage = phoneNumberError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            continueCaption = "Continue",
            stackButtons = true,
            continueEnabled = !isSubmitting && dataLoaded && !editingUnavailable &&
                    relationship != null && contactName.isNotBlank() && phoneNumber.isNotBlank(),
            isLoading = isSubmitting
        )
    }
}

@Composable
private fun RelationshipDropdown(
    relationship: EmergencyRelationship?,
    enabled: Boolean,
    errorMessage: String? = null,
    onRelationshipSelected: (EmergencyRelationship) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = EmergencyRelationship.entries

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
        Row {
            BasicText(
                text = "Relationship ",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )
            BasicText(
                text = "*",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Theme.colors.cardBackground)
                    .then(
                        if (errorMessage != null) {
                            Modifier.border(1.dp, Theme.colors.error, RoundedCornerShape(14.dp))
                        } else {
                            Modifier
                        }
                    )
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = Theme.spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicText(
                    text = relationship?.localizedLabel() ?: "Select relationship",
                    modifier = Modifier.weight(1f),
                    style = Theme.typography.body.medium.copy(
                        color = if (relationship == null) Theme.colors.hint else Theme.colors.primaryFont,
                        fontSize = 16.sp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Theme.colors.hint,
                    modifier = Modifier.size(24.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Theme.colors.surface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            BasicText(
                                text = option.localizedLabel(),
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.primaryFont
                                )
                            )
                        },
                        onClick = {
                            onRelationshipSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        errorMessage?.let {
            BasicText(
                text = it,
                style = Theme.typography.body.small.copy(color = Theme.colors.error)
            )
        }
    }
}

@Composable
private fun EmergencyRelationship.localizedLabel(): String = when (this) {
    EmergencyRelationship.Father -> stringResource(R.string.relationship_father)
    EmergencyRelationship.Mother -> stringResource(R.string.relationship_mother)
    EmergencyRelationship.Brother -> stringResource(R.string.relationship_brother)
    EmergencyRelationship.Sister -> stringResource(R.string.relationship_sister)
    EmergencyRelationship.Son -> stringResource(R.string.relationship_son)
    EmergencyRelationship.Daughter -> stringResource(R.string.relationship_daughter)
    EmergencyRelationship.Husband -> stringResource(R.string.relationship_husband)
    EmergencyRelationship.Wife -> stringResource(R.string.relationship_wife)
    EmergencyRelationship.Spouse -> stringResource(R.string.relationship_spouse)
    EmergencyRelationship.Friend -> stringResource(R.string.relationship_friend)
    EmergencyRelationship.Relative -> stringResource(R.string.relationship_relative)
    EmergencyRelationship.Guardian -> stringResource(R.string.relationship_guardian)
    EmergencyRelationship.Parent -> stringResource(R.string.relationship_parent)
    EmergencyRelationship.Sibling -> stringResource(R.string.relationship_sibling)
    EmergencyRelationship.AdultChild -> stringResource(R.string.relationship_adult_child)
    EmergencyRelationship.FriendOrNeighbor -> stringResource(R.string.relationship_friend_neighbor)
    EmergencyRelationship.Other -> stringResource(R.string.relationship_other)
}
