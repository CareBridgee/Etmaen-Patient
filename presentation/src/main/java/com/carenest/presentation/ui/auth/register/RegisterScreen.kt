package com.carenest.presentation.ui.auth.register

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SegmentedControl
import com.carenest.designsystem.components.dialog.SPDatePickerDialog
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.domain.model.profile.ProfileField
import com.carenest.presentation.ui.profile_completion.validation.localizedMessage
import com.carenest.presentation.navigation.ScreenTopBar
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import com.carenest.presentation.ui.components.ProfileAvatarHeader
import com.carenest.presentation.util.readAvatar
import com.carenest.presentation.ui.auth.localizedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun RegisterScreen(
    mode: PersonalInformationMode = PersonalInformationMode.Registration,
    onNavigateBack: () -> Unit,
    onNavigateToWelcome: (profileId: String?) -> Unit,
    onNavigateHome: () -> Unit,
    onEditComplete: () -> Unit = onNavigateBack,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        coroutineScope.launch {
            runCatching { withContext(Dispatchers.IO) { context.readAvatar(uri) } }.onSuccess { image ->
                viewModel.onEvent(
                    RegisterIntent.AvatarSelected(
                        uri = uri.toString(),
                        fileName = image.fileName,
                        contentType = image.contentType,
                        bytes = image.bytes
                    )
                )
            }
        }
    }

    LaunchedEffect(mode) {
        viewModel.onEvent(RegisterIntent.ConfigureMode(mode))
    }

    BackHandler(enabled = mode == PersonalInformationMode.EditProfile) {
        viewModel.onEvent(RegisterIntent.BackClicked)
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            RegisterEffect.NavigateBack -> onNavigateBack()
            is RegisterEffect.NavigateToWelcome -> onNavigateToWelcome(effect.profileId)
            RegisterEffect.NavigateToHome -> onNavigateHome()
            RegisterEffect.NavigateAfterEdit -> onEditComplete()
            RegisterEffect.SelectAvatar -> avatarPicker.launch("image/*")
        }
    }

    RegisterScreenContent(
        state = state,
        mode = mode,
        onEvent = viewModel::onEvent
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun RegisterScreenContent(
    state: RegisterState,
    mode: PersonalInformationMode = PersonalInformationMode.Registration,
    onEvent: (RegisterIntent) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    ScreenTopBar(
        title = stringResource(
            if (mode == PersonalInformationMode.EditProfile) {
                R.string.personal_info_title
            } else {
                R.string.welcome_topbar_title
            }
        ),
        showLeadingIcon = mode == PersonalInformationMode.EditProfile,
        onLeadingClick = if (mode == PersonalInformationMode.EditProfile) {
            { onEvent(RegisterIntent.BackClicked) }
        } else {
            null
        }
    )

    if (state.isInitializing) {
        RegisterLoadingShimmer()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        if (mode == PersonalInformationMode.Registration) {
            RegisterProgressHeader()
            Spacer(modifier = Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, RoundedCornerShape(22.dp))
                .clip(RoundedCornerShape(22.dp))
                .background(Theme.colors.surface)
                .padding(20.dp)
        ) {
            BasicText(
                text = stringResource(R.string.personal_info_title),
                style = Theme.typography.title.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProfileAvatarHeader(
                    avatarUrl = state.avatarUri ?: state.profileImageUrl,
                    onEditAvatarClick = { onEvent(RegisterIntent.EditAvatarClicked) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomTextField(
                    text = state.firstName,
                    onTextChange = {
                        onEvent(RegisterIntent.FirstNameChanged(it))
                    },
                    title = stringResource(R.string.personal_info_first_name_title),
                    singleLine = true,
                    fieldHeight = 48.dp,
                    containerColor = Theme.colors.disable,
                    isError = state.validationErrors[ProfileField.FirstName] != null,
                    errorMessage = state.validationErrors[ProfileField.FirstName]
                        .localizedMessage(ProfileField.FirstName),
                    reserveErrorSpace = true,
                    errorSpaceHeight = 36.dp,
                    modifier = Modifier.weight(1f)
                )
                CustomTextField(
                    text = state.lastName,
                    onTextChange = {
                        onEvent(RegisterIntent.LastNameChanged(it))
                    },
                    title = stringResource(R.string.personal_info_last_name_title),
                    singleLine = true,
                    fieldHeight = 48.dp,
                    containerColor = Theme.colors.disable,
                    isError = state.validationErrors[ProfileField.LastName] != null,
                    errorMessage = state.validationErrors[ProfileField.LastName]
                        .localizedMessage(ProfileField.LastName),
                    reserveErrorSpace = true,
                    errorSpaceHeight = 36.dp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                text = state.dateOfBirth,
                onTextChange = {
                    onEvent(RegisterIntent.DateOfBirthChanged(it))
                },
                title = stringResource(R.string.personal_info_dob_title),
                hint = stringResource(R.string.personal_info_dob_hint),
                trailingIcon = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                onClickTrailingIcon = { showDatePicker = true },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                inputFormatter = ::formatDateInput,
                singleLine = true,
                fieldHeight = 48.dp,
                containerColor = Theme.colors.disable,
                isError = state.validationErrors[ProfileField.DateOfBirth] != null,
                errorMessage = state.validationErrors[ProfileField.DateOfBirth]
                    .localizedMessage(ProfileField.DateOfBirth),
                reserveErrorSpace = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))




            BasicText(
                text = stringResource(R.string.personal_info_gender_title),
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val genderOptions = listOf(
                stringResource(R.string.personal_info_gender_male),
                stringResource(R.string.personal_info_gender_female)
            )
            val genderValues = listOf("MALE", "FEMALE")
            SegmentedControl(
                items = genderOptions,
                selectedIndex = genderValues.indexOf(state.gender),
                onItemSelected = {
                    onEvent(RegisterIntent.GenderChanged(genderValues[it]))
                }
            )
            Box(modifier = Modifier.height(26.dp)) {
                state.validationErrors[ProfileField.Gender]
                    .localizedMessage(ProfileField.Gender)
                    ?.let { validationMessage ->
                        Text(text = validationMessage, color = Theme.colors.error)
                    }
            }

            Spacer(modifier = Modifier.height(8.dp))

            PrimaryButton(
                caption = stringResource(
                    if (mode == PersonalInformationMode.EditProfile) {
                        R.string.personal_info_save_btn
                    } else {
                        R.string.personal_info_continue_btn
                    }
                ),
                onClick = { onEvent(RegisterIntent.ContinueClicked) },
                modifier = Modifier.fillMaxWidth(),
                isDisabled = state.isSubmitting,
                isLoading = state.isSubmitting
            )
            state.errorMessage.localizedMessage()?.let { errorMessage ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = Theme.colors.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateOfBirth(state.dateOfBirth)
        )
        SPDatePickerDialog(
            state = datePickerState,
            onDismissRequest = { showDatePicker = false },
            confirmLabel = stringResource(R.string.personal_info_date_confirm),
            dismissLabel = stringResource(R.string.personal_info_date_cancel),
            onConfirm = { selectedDate ->
                selectedDate?.let {
                    onEvent(
                        RegisterIntent.DateOfBirthChanged(
                            formatDateOfBirth(it)
                        )
                    )
                }
                showDatePicker = false
            }
        )
    }
}

@Composable
private fun RegisterProgressHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            BasicText(
                text = stringResource(R.string.registration_progress_label),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
            )
            BasicText(
                text = stringResource(R.string.personal_info_step_title),
                style = Theme.typography.body.small.copy(color = Theme.colors.hint)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(Theme.colors.success)
        )
    }
}

private const val DateOfBirthPattern = "MM/dd/yyyy"

private fun parseDateOfBirth(value: String): Long? = runCatching {
    dateOfBirthFormatter().parse(value)?.time
}.getOrNull()

private fun formatDateOfBirth(dateMillis: Long): String =
    dateOfBirthFormatter().format(Date(dateMillis))

private fun dateOfBirthFormatter() = SimpleDateFormat(DateOfBirthPattern, Locale.US).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    SpTheme {
        RegisterScreenContent(
            state = RegisterState(
                firstName = "",
                lastName = "",
                dateOfBirth = "",
                gender = "",
                isInitializing = false
            ),
            onEvent = {}
        )
    }
}
