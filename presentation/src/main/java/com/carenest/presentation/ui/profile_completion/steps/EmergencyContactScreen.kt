package com.carenest.presentation.ui.profile_completion.steps

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.validation.SupportedPhoneCountry
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.auth.login.components.PhoneInputField
import com.carenest.presentation.ui.auth.login.countries
import com.carenest.presentation.ui.profile_completion.components.ProfileProgressIndicator
import com.carenest.presentation.ui.profile_completion.components.ProfileScreenNavigation

@Composable
fun EmergencyContactScreen(
    contactName: String,
    relationship: EmergencyRelationship?,
    phoneNumber: String,
    phoneCountry: SupportedPhoneCountry = SupportedPhoneCountry.EGYPT,
    dataLoaded: Boolean = true,
    editingUnavailable: Boolean = false,
    contactNameError: String? = null,
    relationshipError: String? = null,
    phoneNumberError: String? = null,
    onContactNameChange: (String) -> Unit,
    onRelationshipSelected: (EmergencyRelationship) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onPhoneCountryChange: (SupportedPhoneCountry) -> Unit,
    onBack: () -> Unit,
    onContinue: () -> Unit,
    isSubmitting: Boolean = false
) {
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    val selectedCountry = countries.firstOrNull { it.phoneConfig == phoneCountry }
        ?: countries.first()

    ScreenTopBar(
        title = stringResource(R.string.emergency_contact_title),
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
                verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
            ) {
                BasicText(
                    text = stringResource(R.string.emergency_contact_title),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                if (editingUnavailable) {
                    BasicText(
                        text = stringResource(R.string.emergency_contact_multiple_unavailable),
                        style = Theme.typography.body.medium.copy(color = Theme.colors.error)
                    )
                }

                RelationshipDropdown(
                    relationship = relationship,
                    enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                    errorMessage = relationshipError,
                    onRelationshipSelected = onRelationshipSelected
                )

                CustomTextField(
                    text = contactName,
                    onTextChange = onContactNameChange,
                    title = stringResource(R.string.emergency_contact_name),
                    hint = stringResource(R.string.emergency_contact_name_hint),
                    leadingIcon = rememberVectorPainter(Icons.Outlined.PersonOutline),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                    isError = contactNameError != null,
                    errorMessage = contactNameError,
                    singleLine = true,
                    reserveErrorSpace = true,
                    errorSpaceHeight = 18.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    BasicText(
                        text = stringResource(R.string.emergency_contact_phone),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )
                    PhoneInputField(
                        phone = phoneNumber,
                        onPhoneChange = onPhoneNumberChange,
                        selectedCountry = selectedCountry,
                        isDropdownExpanded = countryDropdownExpanded,
                        onCountryClick = {
                            countryDropdownExpanded = !countryDropdownExpanded
                        },
                        onCountrySelect = { country ->
                            countryDropdownExpanded = false
                            onPhoneCountryChange(country.phoneConfig)
                        },
                        fieldHeight = 56.dp,
                        isError = phoneNumberError != null,
                        enabled = !isSubmitting && dataLoaded && !editingUnavailable,
                        containerColor = Theme.colors.cardBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(modifier = Modifier.height(18.dp)) {
                        phoneNumberError?.let { errorMessage ->
                            BasicText(
                                text = errorMessage,
                                style = Theme.typography.body.small.copy(
                                    color = Theme.colors.error
                                )
                            )
                        }
                    }
                }
            }
        }

        ProfileScreenNavigation(
            onBack = onBack,
            onContinue = onContinue,
            continueCaption = stringResource(R.string.emergency_contact_continue),
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
                text = stringResource(R.string.emergency_contact_relationship),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                )
            )
            BasicText(
                text = " *",
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
                    text = relationship?.localizedLabel() ?: stringResource(R.string.emergency_contact_relationship_hint),
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
                modifier = Modifier.background(
                    color = Theme.colors.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                    .widthIn(min = 240.dp, max = 320.dp)
                    .heightIn(max = 280.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            BasicText(
                                text = option.localizedLabel(),
                                style = Theme.typography.body.medium.copy(
                                    color = if (option == relationship) {
                                        Theme.colors.primary
                                    } else {
                                        Theme.colors.primaryFont
                                    },
                                    fontWeight = if (option == relationship) {
                                        FontWeight.SemiBold
                                    } else {
                                        FontWeight.Normal
                                    }
                                )
                            )
                        },
                        trailingIcon = if (option == relationship) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = Theme.colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            null
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .background(
                                if (option == relationship) {
                                    Theme.colors.primaryContainer
                                } else {
                                    Theme.colors.surface
                                }
                            ),
                        onClick = {
                            onRelationshipSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        Box(modifier = Modifier.height(18.dp)) {
            errorMessage?.let {
                BasicText(
                    text = it,
                    style = Theme.typography.body.small.copy(color = Theme.colors.error)
                )
            }
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
