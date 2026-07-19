package com.carenest.presentation.ui.profile.personalinfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SegmentedControl
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.ui.profile.components.ProfileProgressIndicator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBasicHealth: () -> Unit,
    viewModel: PersonalInfoViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            PersonalInfoEffect.NavigateBack -> onNavigateBack()
            PersonalInfoEffect.NavigateToBasicHealth -> onNavigateToBasicHealth()
        }
    }
    PersonalInfoScreenContent(state = state, onEvent = viewModel::onEvent)
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
internal fun PersonalInfoScreenContent(
    state: PersonalInfoState,
    onEvent: (PersonalInfoIntent) -> Unit
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = { onEvent(PersonalInfoIntent.BackClicked) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        ProfileProgressIndicator(step = 1, title = stringResource(R.string.personal_info_step_title))

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp))
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
                    onTextChange = { onEvent(PersonalInfoIntent.FirstNameChanged(it)) },
                    title = stringResource(R.string.personal_info_first_name_title),
                    hint = stringResource(R.string.personal_info_first_name_hint),
                    singleLine = true,
                    fieldHeight = 48.dp,
                    containerColor = Theme.colors.disable,
                    modifier = Modifier.weight(1f)
                )
                CustomTextField(
                    text = state.lastName,
                    onTextChange = { onEvent(PersonalInfoIntent.LastNameChanged(it)) },
                    title = stringResource(R.string.personal_info_last_name_title),
                    hint = stringResource(R.string.personal_info_last_name_hint),
                    singleLine = true,
                    fieldHeight = 48.dp,
                    containerColor = Theme.colors.disable,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomTextField(
                text = state.dateOfBirth,
                onTextChange = { onEvent(PersonalInfoIntent.DateOfBirthChanged(it)) },
                title = stringResource(R.string.personal_info_dob_title),
                hint = stringResource(R.string.personal_info_dob_hint),
                trailingIcon = rememberVectorPainter(Icons.Outlined.CalendarMonth),
                onClickTrailingIcon = { showDatePicker = true },
                singleLine = true,
                fieldHeight = 48.dp,
                containerColor = Theme.colors.disable,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                text = state.nationalId,
                onTextChange = { onEvent(PersonalInfoIntent.NationalIdChanged(it.filter(Char::isDigit).take(16))) },
                title = stringResource(R.string.personal_info_national_id_title),
                hint = stringResource(R.string.personal_info_national_id_hint),
                singleLine = true,
                fieldHeight = 48.dp,
                containerColor = Theme.colors.disable,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicText(
                text = stringResource(R.string.personal_info_gender_title),
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val genderOptions = listOf(stringResource(R.string.personal_info_gender_male), stringResource(R.string.personal_info_gender_female))
            SegmentedControl(
                items = genderOptions,
                selectedIndex = genderOptions.indexOf(state.gender),
                onItemSelected = { onEvent(PersonalInfoIntent.GenderChanged(genderOptions[it])) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicText(
                text = stringResource(R.string.personal_info_account_type_title),
                style = Theme.typography.body.medium.copy(color = Theme.colors.primaryFont)
            )
            Spacer(modifier = Modifier.height(8.dp))

            val accountTypes = listOf(
                stringResource(R.string.personal_info_account_type_family),
                stringResource(R.string.personal_info_account_type_personal)
            )
            SegmentedControl(
                items = accountTypes,
                selectedIndex = accountTypes.indexOf(state.accountType),
                onItemSelected = { onEvent(PersonalInfoIntent.AccountTypeChanged(accountTypes[it])) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                caption = stringResource(R.string.personal_info_continue_btn),
                onClick = { onEvent(PersonalInfoIntent.ContinueClicked) },
                modifier = Modifier.fillMaxWidth()
            )

        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = parseDateOfBirth(state.dateOfBirth)
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            onEvent(PersonalInfoIntent.DateOfBirthChanged(formatDateOfBirth(selectedDate)))
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
                colors = DatePickerDefaults.colors(
                    containerColor = Theme.colors.surface,
                    titleContentColor = Theme.colors.primaryFont,
                    headlineContentColor = Theme.colors.primaryFont,
                    weekdayContentColor = Theme.colors.secondaryFont,
                    dayContentColor = Theme.colors.primaryFont,
                    selectedDayContainerColor = Theme.colors.primary,
                    selectedDayContentColor = Theme.colors.onPrimary,
                    todayContentColor = Theme.colors.primary,
                    todayDateBorderColor = Theme.colors.primary
                )
            )
        }
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
private fun PersonalInfoScreenPreview() {
    SpTheme {
        PersonalInfoScreenContent(
            state = PersonalInfoState(
                firstName = "Jane",
                lastName = "Doe",
                dateOfBirth = "01/15/1990",
                gender = "Female"
            ),
            onEvent = {}
        )
    }
}
