
package com.carenest.presentation.ui.family_members.add

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import com.carenest.presentation.ui.components.ProfileAvatarHeader
import com.carenest.presentation.util.readAvatar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.domain.validation.EgyptianPhoneNumberValidator
import com.carenest.domain.validation.PhoneNumberValidationError
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
    onMemberSaved: () -> Unit = {},
    onNavigateToCompleteProfile: (String) -> Unit = {},
    onShowMessage: (String) -> Unit = {},
    viewModel: AddFamilyMemberViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val androidContext = LocalContext.current
    val saveFailedMessage = stringResource(R.string.family_member_save_failed)
    val loadFailedMessage = stringResource(R.string.family_member_load_failed)
    val savedMessage = stringResource(R.string.family_member_saved)

    androidx.compose.runtime.LaunchedEffect(memberId) {
        if (!memberId.isNullOrBlank() && memberId != "null") {
            viewModel.initMemberId(memberId)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        coroutineScope.launch {
            runCatching { withContext(Dispatchers.IO) { androidContext.readAvatar(uri) } }.onSuccess { image ->
                viewModel.onEvent(
                    AddFamilyMemberEvent.AvatarSelected(
                        uri = uri.toString(),
                        fileName = image.fileName,
                        contentType = image.contentType,
                        bytes = image.bytes
                    )
                )
            }
        }
    }

    val fillRequiredFieldsMessage = stringResource(R.string.error_fill_required_fields)

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            AddFamilyMemberEffect.NavigateBack -> onNavigateBack()
            is AddFamilyMemberEffect.NavigateToCompleteProfile -> {
                onMemberSaved()
                onNavigateToCompleteProfile(effect.memberId)
            }
            is AddFamilyMemberEffect.ShowError -> {
                val message = when (effect.message) {
                    "family_member_load_failed" -> loadFailedMessage
                    "family_member_save_failed" -> saveFailedMessage
                    "Please fill out all required fields correctly" -> fillRequiredFieldsMessage
                    else -> effect.message
                }
                onShowMessage(message)
            }
            AddFamilyMemberEffect.ShowSuccess -> {
                onMemberSaved()
                onShowMessage(savedMessage)
            }
            AddFamilyMemberEffect.SelectAvatar -> avatarPicker.launch("image/*")
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

    val buttonCaption = stringResource(R.string.personal_info_continue_btn)

    ScreenTopBar(
        title = screenTitle,
        showLeadingIcon = true,
        onLeadingClick = { onEvent(AddFamilyMemberEvent.BackClicked) }
    )

    if (state.isLoadingData) {
        AddFamilyMemberLoadingShimmer()
        return
    }

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
                    text = stringResource(R.string.family_member_info_section),
                    style = Theme.typography.title.copy(
                        color = Theme.colors.primaryFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ProfileAvatarHeader(
                        avatarUrl = state.avatarUri ?: state.profileImageUrl,
                        onEditAvatarClick = { onEvent(AddFamilyMemberEvent.EditAvatarClicked) }
                    )
                }

                RelationshipDropdown(
                    relationship = state.relationship,
                    enabled = !state.isSubmitting && !state.isLoadingData,
                    errorMessage = state.relationshipError.toLocalizedErrorMessage(),
                    onRelationshipSelected = { onEvent(AddFamilyMemberEvent.RelationshipSelected(it)) }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                ) {
                    CustomTextField(
                        text = state.firstName,
                        onTextChange = { onEvent(AddFamilyMemberEvent.FirstNameChanged(it)) },
                        title = stringResource(R.string.personal_info_first_name_title),
                        hint = stringResource(R.string.personal_info_first_name_hint),
                        borderColor = Theme.colors.cardBackground,
                        containerColor = Theme.colors.cardBackground,
                        enabled = !state.isSubmitting && !state.isLoadingData,
                        isError = state.firstNameError != null,
                        errorMessage = state.firstNameError.toLocalizedErrorMessage(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    CustomTextField(
                        text = state.lastName,
                        onTextChange = { onEvent(AddFamilyMemberEvent.LastNameChanged(it)) },
                        title = stringResource(R.string.personal_info_last_name_title),
                        hint = stringResource(R.string.personal_info_last_name_title), // simplified hint
                        borderColor = Theme.colors.cardBackground,
                        containerColor = Theme.colors.cardBackground,
                        enabled = !state.isSubmitting && !state.isLoadingData,
                        isError = state.lastNameError != null,
                        errorMessage = state.lastNameError.toLocalizedErrorMessage(),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                CustomTextField(
                    text = state.dateOfBirth,
                    onTextChange = { onEvent(AddFamilyMemberEvent.DateOfBirthChanged(it)) },
                    title = stringResource(R.string.personal_info_dob_title),
                    hint = stringResource(R.string.personal_info_dob_hint),
                    trailingIcon = rememberVectorPainter(Icons.Outlined.CalendarToday),
                    onClickTrailingIcon = { if (!state.isSubmitting && !state.isLoadingData) showDatePicker = true },
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = !state.isSubmitting && !state.isLoadingData,
                    isError = state.dateOfBirthError != null,
                    errorMessage = state.dateOfBirthError.toLocalizedErrorMessage(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                CustomTextField(
                    text = state.phoneNumber,
                    onTextChange = { onEvent(AddFamilyMemberEvent.PhoneNumberChanged(it)) },
                    title = stringResource(R.string.family_member_phone_optional),
                    hint = stringResource(R.string.family_member_phone_hint),
                    borderColor = Theme.colors.cardBackground,
                    containerColor = Theme.colors.cardBackground,
                    enabled = !state.isSubmitting && !state.isLoadingData,
                    isError = state.phoneNumberError != null,
                    errorMessage = state.phoneNumberError?.let {
                        when (it) {
                            PhoneNumberValidationError.Required -> stringResource(R.string.phone_validation_required)
                            PhoneNumberValidationError.InvalidLength -> stringResource(R.string.phone_validation_invalid_length)
                            PhoneNumberValidationError.InvalidFormat -> stringResource(R.string.phone_validation_invalid_format)
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Column(verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)) {
                    BasicText(
                        text = stringResource(R.string.personal_info_gender_title),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.primaryFont,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )

                    val genderOptions = listOf(
                        stringResource(R.string.personal_info_gender_male),
                        stringResource(R.string.personal_info_gender_female)
                    )
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

        val isPhoneValid = state.phoneNumber.isBlank() || EgyptianPhoneNumberValidator.validate(state.phoneNumber) == null
        val isInputValid = state.relationship != null &&
                state.firstName.trim().isNotBlank() &&
                state.lastName.trim().isNotBlank() &&
                state.dateOfBirth.trim().isNotBlank() &&
                isPhoneValid

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
private fun AddFamilyMemberLoadingShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = Theme.spacing.space20,
                    vertical = Theme.spacing.large,
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Theme.colors.surface)
                    .padding(Theme.spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .fillMaxWidth(0.48f)
                        .height(24.dp),
                )
                Spacer(modifier = Modifier.height(24.dp))
                ShimmerPlaceholder(
                    modifier = Modifier.size(104.dp),
                    shape = CircleShape,
                )
                Spacer(modifier = Modifier.height(24.dp))

                repeat(4) { index ->
                    ShimmerPlaceholder(
                        modifier = Modifier
                            .align(Alignment.Start)
                            .fillMaxWidth(if (index == 0) 0.36f else 0.48f)
                            .height(14.dp),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (index == 1) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                        ) {
                            repeat(2) {
                                ShimmerPlaceholder(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                )
                            }
                        }
                    } else {
                        ShimmerPlaceholder(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.surface)
                .padding(Theme.spacing.space20),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            repeat(2) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                )
            }
        }
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
                text = stringResource(R.string.family_member_relationship_label) + " ",
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
@Composable
private fun String?.toLocalizedErrorMessage(): String? {
    if (this == null) return null
    return when (this) {
        "Please select a relationship", "error_select_relationship" -> stringResource(R.string.relationship_required_error)
        "First name is required", "error_first_name_required" -> stringResource(R.string.error_first_name_required)
        "Last name is required", "error_last_name_required" -> stringResource(R.string.error_last_name_required)
        "Date of birth is required", "error_dob_required" -> stringResource(R.string.error_dob_required)
        "Please fill out all required fields correctly", "error_fill_required_fields" -> stringResource(R.string.error_fill_required_fields)
        "Height must be a number", "error_height_number" -> stringResource(R.string.error_height_number)
        "Weight must be a number", "error_weight_number" -> stringResource(R.string.error_weight_number)
        else -> this
    }
}
