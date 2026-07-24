package com.carenest.presentation.ui.auth.register

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SegmentedControl
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.domain.model.profile.ProfileField
import com.carenest.presentation.ui.profile.validation.localizedMessage
import com.carenest.presentation.navigation.ScreenTopBar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onNavigateToWelcome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            RegisterEffect.NavigateBack -> onNavigateBack()
            RegisterEffect.NavigateToWelcome -> onNavigateToWelcome()
        }
    }

    RegisterScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun RegisterScreenContent(
    state: RegisterState,
    onEvent: (RegisterIntent) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = false
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
        RegisterProgressHeader()

        Spacer(modifier = Modifier.height(24.dp))

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
                    errorMessage = state.validationErrors[ProfileField.FirstName].localizedMessage(),
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
                    errorMessage = state.validationErrors[ProfileField.LastName].localizedMessage(),
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
                singleLine = true,
                fieldHeight = 48.dp,
                containerColor = Theme.colors.disable,
                isError = state.validationErrors[ProfileField.DateOfBirth] != null,
                errorMessage = state.validationErrors[ProfileField.DateOfBirth].localizedMessage(),
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
            state.validationErrors[ProfileField.Gender].localizedMessage()?.let { validationMessage ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = validationMessage, color = Theme.colors.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                caption = stringResource(R.string.personal_info_continue_btn),
                onClick = { onEvent(RegisterIntent.ContinueClicked) },
                modifier = Modifier.fillMaxWidth(),
                isDisabled = state.isSubmitting,
                isLoading = state.isSubmitting
            )
            state.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = Theme.colors.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateOfBirth(state.dateOfBirth)
        )
        val datePickerColors = DatePickerDefaults.colors(
            containerColor = Theme.colors.surface,
            titleContentColor = Theme.colors.primaryFont,
            headlineContentColor = Theme.colors.primaryFont,
            weekdayContentColor = Theme.colors.secondaryFont,
            subheadContentColor = Theme.colors.primaryFont,
            navigationContentColor = Theme.colors.primary,
            yearContentColor = Theme.colors.primaryFont,
            disabledYearContentColor = Theme.colors.onDisable,
            currentYearContentColor = Theme.colors.primary,
            selectedYearContentColor = Theme.colors.onPrimary,
            disabledSelectedYearContentColor = Theme.colors.onDisable,
            selectedYearContainerColor = Theme.colors.primary,
            disabledSelectedYearContainerColor = Theme.colors.disable,
            dayContentColor = Theme.colors.primaryFont,
            disabledDayContentColor = Theme.colors.onDisable,
            selectedDayContentColor = Theme.colors.onPrimary,
            disabledSelectedDayContentColor = Theme.colors.onDisable,
            selectedDayContainerColor = Theme.colors.primary,
            disabledSelectedDayContainerColor = Theme.colors.disable,
            todayContentColor = Theme.colors.primary,
            todayDateBorderColor = Theme.colors.primary,
            dayInSelectionRangeContainerColor = Theme.colors.primaryContainer,
            dayInSelectionRangeContentColor = Theme.colors.onPrimaryContainer,
            dividerColor = Theme.colors.divider
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            colors = datePickerColors,
            tonalElevation = 0.dp,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            onEvent(
                                RegisterIntent.DateOfBirthChanged(
                                    formatDateOfBirth(selectedDate)
                                )
                            )
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null,
                    colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
                ) {
                    Text(stringResource(R.string.personal_info_date_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
                ) {
                    Text(stringResource(R.string.personal_info_date_cancel))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = datePickerColors
            )
        }
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
