package com.carenest.presentation.ui.profile.steps

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.FamilyRestroom
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Security
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile.EmergencyRelationship
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile.components.ProfileScreenNavigation

@Composable
fun EmergencyContactScreen(
    contactName: String,
    relationship: EmergencyRelationship?,
    phoneNumber: String,
    dataLoaded: Boolean = true,
    editingUnavailable: Boolean = false,
    onContactNameChange: (String) -> Unit,
    onRelationshipSelected: (EmergencyRelationship) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.extraLarge)
        ) {
            ProfileProgressIndicator(
                step = 7,
                title = stringResource(R.string.emergency_contact_progress_title)
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
                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    BasicText(
                        text = stringResource(R.string.emergency_contact_title),
                        style = Theme.typography.title.copy(
                            color = Theme.colors.primaryFont
                        )
                    )
                    BasicText(
                        text = stringResource(R.string.emergency_contact_description),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.secondaryFont,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    )
                }

                if (editingUnavailable) {
                    BasicText(
                        text = stringResource(R.string.emergency_contact_multiple_unavailable),
                        style = Theme.typography.body.medium.copy(color = Theme.colors.error)
                    )
                }

                CustomTextField(
                    text = contactName,
                    onTextChange = onContactNameChange,
                    title = stringResource(R.string.emergency_contact_name),
                    hint = stringResource(R.string.emergency_contact_name_hint),
                    leadingIcon = rememberVectorPainter(Icons.Outlined.PersonOutline),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = dataLoaded && !editingUnavailable,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                RelationshipDropdown(
                    relationship = relationship,
                    enabled = dataLoaded && !editingUnavailable,
                    onRelationshipSelected = onRelationshipSelected
                )

                CustomTextField(
                    text = phoneNumber,
                    onTextChange = onPhoneNumberChange,
                    title = stringResource(R.string.emergency_contact_phone),
                    hint = stringResource(R.string.emergency_contact_phone_hint),
                    leadingIcon = rememberVectorPainter(Icons.Outlined.Call),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = dataLoaded && !editingUnavailable,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Theme.colors.primaryContainer.copy(alpha = 0.25f))
                        .padding(Theme.spacing.space12),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = Theme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    BasicText(
                        text = stringResource(R.string.emergency_contact_security),
                        modifier = Modifier.weight(1f),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.Outlined.Security,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(64.dp)
                    .alpha(0.14f)
            )
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            continueCaption = stringResource(R.string.emergency_contact_continue),
            stackButtons = true,
            continueEnabled = dataLoaded && !isSubmitting,
            isLoading = isSubmitting
        )
    }
}

@Composable
private fun RelationshipDropdown(
    relationship: EmergencyRelationship?,
    enabled: Boolean,
    onRelationshipSelected: (EmergencyRelationship) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = EmergencyRelationship.entries

    Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
        BasicText(
            text = stringResource(R.string.emergency_contact_relationship),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Theme.colors.cardBackground)
                    .clickable(enabled = enabled) { expanded = true }
                    .padding(horizontal = Theme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(Theme.spacing.space12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FamilyRestroom,
                    contentDescription = null,
                    tint = Theme.colors.hint,
                    modifier = Modifier.size(22.dp)
                )
                BasicText(
                    text = relationship?.localizedLabel()
                        ?: stringResource(R.string.emergency_contact_relationship_hint),
                    modifier = Modifier.weight(1f),
                    style = Theme.typography.body.medium.copy(
                        color = if (relationship == null) {
                            Theme.colors.hint
                        } else {
                            Theme.colors.primaryFont
                        },
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
    }
}

@Composable
private fun EmergencyRelationship.localizedLabel(): String = when (this) {
    EmergencyRelationship.Spouse -> stringResource(R.string.relationship_spouse)
    EmergencyRelationship.Parent -> stringResource(R.string.relationship_parent)
    EmergencyRelationship.Sibling -> stringResource(R.string.relationship_sibling)
    EmergencyRelationship.AdultChild -> stringResource(R.string.relationship_adult_child)
    EmergencyRelationship.FriendOrNeighbor -> stringResource(R.string.relationship_friend_neighbor)
    EmergencyRelationship.Other -> stringResource(R.string.relationship_other)
}

@Preview(showBackground = true)
@Composable
private fun EmergencyContactScreenPreview() {
    SpTheme {
        EmergencyContactScreen(
            contactName = "Mona Adel",
            relationship = EmergencyRelationship.Sibling,
            phoneNumber = "0100 000 0000",
            onContactNameChange = {},
            onRelationshipSelected = {},
            onPhoneNumberChange = {},
            onBack = {},
            onContinue = {}
        )
    }
}
