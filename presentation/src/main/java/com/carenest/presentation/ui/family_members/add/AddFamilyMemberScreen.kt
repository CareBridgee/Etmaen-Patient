package com.carenest.presentation.ui.family_members.add

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.SegmentedControl
import com.carenest.designsystem.components.dialog.SPDatePickerDialog
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.model.family_members.FamilyRelationship
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.presentation.ui.profile_completion.components.ProfileScreenNavigation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddFamilyMemberScreenRoute(
    memberId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AddFamilyMemberViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    androidx.compose.runtime.LaunchedEffect(memberId) {
        if (!memberId.isNullOrBlank() && memberId != "null") {
            viewModel.initMemberId(memberId)
        }
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AddFamilyMemberEffect.NavigateBack -> onNavigateBack()
            is AddFamilyMemberEffect.ShowError -> {
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
            AddFamilyMemberEffect.ShowSuccess -> {
                Toast.makeText(context, "Family member saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AddFamilyMemberContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFamilyMemberContent(
    state: AddFamilyMemberState,
    onEvent: (AddFamilyMemberEvent) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val screenTitle = if (state.isEditMode) {
        stringResource(R.string.edit_family_member_title)
    } else {
        stringResource(R.string.family_members_screen_title)
    }

    val buttonCaption = if (state.isEditMode) {
        stringResource(R.string.save_changes_button)
    } else {
        stringResource(R.string.add_family_member_button)
    }

    ScreenTopBar(
        title = screenTitle,
        showLeadingIcon = true,
        onLeadingClick = { onEvent(AddFamilyMemberEvent.BackClicked) }
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
                    text = "Family Member Info",
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                RelationshipDropdown(
                    relationship = state.relationship,
                    enabled = !state.isSubmitting && !state.isLoadingData,
                    errorMessage = state.relationshipError,
                    onRelationshipSelected = { onEvent(AddFamilyMemberEvent.RelationshipSelected(it)) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
                ) {
                    CustomTextField(
                        text = state.firstName,
                        onTextChange = { onEvent(AddFamilyMemberEvent.FirstNameChanged(it)) },
                        title = "First Name",
                        hint = "e.g. Sarah",
                        borderColor = Theme.colors.cardBackground,
                        containerColor = Theme.colors.cardBackground,
                        enabled = !state.isSubmitting && !state.isLoadingData,
                        isError = state.firstNameError != null,
                        errorMessage = state.firstNameError,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    CustomTextField(
                        text = state.lastName,
                        onTextChange = { onEvent(AddFamilyMemberEvent.LastNameChanged(it)) },
                        title = "Last Name",
                        hint = "e.g. Jenkins",
                        borderColor = Theme.colors.cardBackground,
                        containerColor = Theme.colors.cardBackground,
                        enabled = !state.isSubmitting && !state.isLoadingData,
                        isError = state.lastNameError != null,
                        errorMessage = state.lastNameError,
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                CustomTextField(
                    text = state.dateOfBirth,
                    onTextChange = { onEvent(AddFamilyMemberEvent.DateOfBirthChanged(it)) },
                    title = "Date of birth",
                    hint = "YYYY-MM-DD",
                    trailingIcon = rememberVectorPainter(Icons.Outlined.CalendarToday),
                    onClickTrailingIcon = { if (!state.isSubmitting && !state.isLoadingData) showDatePicker = true },
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = !state.isSubmitting && !state.isLoadingData,
                    isError = state.dateOfBirthError != null,
                    errorMessage = state.dateOfBirthError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    BasicText(
                        text = "Gender",
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )

                    val genderOptions = listOf("Male", "Female")
                    val genderValues = listOf("MALE", "FEMALE")
                    val selectedIdx = genderValues.indexOf(state.gender).coerceAtLeast(0)

                    SegmentedControl(
                        items = genderOptions,
                        selectedIndex = selectedIdx,
                        onItemSelected = { idx ->
                            onEvent(AddFamilyMemberEvent.GenderSelected(genderValues[idx]))
                        }
                    )
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            SPDatePickerDialog(
                state = datePickerState,
                onDismissRequest = { showDatePicker = false },
                confirmLabel = stringResource(R.string.personal_info_date_confirm),
                dismissLabel = stringResource(R.string.personal_info_date_cancel),
                onConfirm = { millis ->
                    millis?.let {
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                        val formattedDate = formatter.format(Date(it))
                        onEvent(AddFamilyMemberEvent.DateOfBirthChanged(formattedDate))
                    }
                    showDatePicker = false
                }
            )
        }

        val isInputValid = state.relationship != null &&
                state.firstName.trim().isNotBlank() &&
                state.lastName.trim().isNotBlank() &&
                state.dateOfBirth.trim().isNotBlank()

        ProfileScreenNavigation(
            onBack = { onEvent(AddFamilyMemberEvent.BackClicked) },
            onContinue = { onEvent(AddFamilyMemberEvent.SaveClicked) },
            continueCaption = buttonCaption,
            stackButtons = true,
            continueEnabled = !state.isSubmitting && !state.isLoadingData && isInputValid,
            isLoading = state.isSubmitting
        )
    }
}

@Composable
private fun RelationshipDropdown(
    relationship: FamilyRelationship?,
    enabled: Boolean,
    errorMessage: String? = null,
    onRelationshipSelected: (FamilyRelationship) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = FamilyRelationship.entries

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
                    text = relationship?.localizedLabel() ?: "e.g. Mother, Father, Spouse",
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
private fun FamilyRelationship.localizedLabel(): String = when (this) {
    FamilyRelationship.Father -> stringResource(R.string.relationship_father)
    FamilyRelationship.Mother -> stringResource(R.string.relationship_mother)
    FamilyRelationship.Brother -> stringResource(R.string.relationship_brother)
    FamilyRelationship.Sister -> stringResource(R.string.relationship_sister)
    FamilyRelationship.Son -> stringResource(R.string.relationship_son)
    FamilyRelationship.Daughter -> stringResource(R.string.relationship_daughter)
    FamilyRelationship.Husband -> stringResource(R.string.relationship_husband)
    FamilyRelationship.Wife -> stringResource(R.string.relationship_wife)
    FamilyRelationship.Spouse -> stringResource(R.string.relationship_spouse)
    FamilyRelationship.Friend -> stringResource(R.string.relationship_friend)
    FamilyRelationship.Relative -> stringResource(R.string.relationship_relative)
    FamilyRelationship.Guardian -> stringResource(R.string.relationship_guardian)
    FamilyRelationship.Parent -> stringResource(R.string.relationship_parent)
    FamilyRelationship.Sibling -> stringResource(R.string.relationship_sibling)
    FamilyRelationship.AdultChild -> stringResource(R.string.relationship_adult_child)
    FamilyRelationship.FriendOrNeighbor -> stringResource(R.string.relationship_friend_neighbor)
    FamilyRelationship.Other -> stringResource(R.string.relationship_other)
}

@Preview(showBackground = true)
@Composable
private fun AddFamilyMemberContentPreview() {
    SpTheme {
        AddFamilyMemberContent(
            state = AddFamilyMemberState(
                firstName = "Sarah",
                lastName = "Jenkins",
                relationship = FamilyRelationship.Mother,
                dateOfBirth = "1990-05-15",
                gender = "FEMALE"
            ),
            onEvent = {}
        )
    }
}
