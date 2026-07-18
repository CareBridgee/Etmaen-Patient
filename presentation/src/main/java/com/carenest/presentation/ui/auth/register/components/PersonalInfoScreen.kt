package com.carenest.presentation.ui.auth.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.button.SegmentedControl
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.register.RegisterIntent
import com.carenest.presentation.ui.auth.register.RegisterState
import com.carenest.presentation.ui.auth.register.RegisterStep
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun PersonalInfoScreen(state: RegisterState, onEvent: (RegisterIntent) -> Unit) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = { onEvent(RegisterIntent.BackClicked) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicText(
                    text = stringResource(R.string.personal_info_step),
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
                BasicText(
                    text = stringResource(R.string.personal_info_step_title),
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.hint
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Progress bar mockup
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Theme.colors.disable, Theme.shapes.small)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .fillMaxHeight()
                        .background(Theme.colors.primary, Theme.shapes.small)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BasicText(
                text = stringResource(R.string.personal_info_title),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    text = state.firstName,
                    onTextChange = { onEvent(RegisterIntent.FirstNameChanged(it)) },
                    title = stringResource(R.string.personal_info_first_name_title),
                    hint = stringResource(R.string.personal_info_first_name_hint),
                    modifier = Modifier.weight(1f)
                )
                CustomTextField(
                    text = state.lastName,
                    onTextChange = { onEvent(RegisterIntent.LastNameChanged(it)) },
                    title = stringResource(R.string.personal_info_last_name_title),
                    hint = stringResource(R.string.personal_info_last_name_hint),
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CustomTextField(
                text = state.dateOfBirth,
                onTextChange = { onEvent(RegisterIntent.DobChanged(it)) },
                title = stringResource(R.string.personal_info_dob_title),
                hint = stringResource(R.string.personal_info_dob_hint),
                trailingIcon = painterResource(id = DR.drawable.ic_time), // Use time/calendar as placeholder
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            BasicText(
                text = stringResource(R.string.personal_info_gender_title),
                style = Theme.typography.body.large.copy(color = Theme.colors.primaryFont)
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val genderOptions = listOf(stringResource(R.string.personal_info_gender_male), stringResource(R.string.personal_info_gender_female))
            SegmentedControl(
                items = genderOptions,
                selectedIndex = genderOptions.indexOf(state.gender),
                onItemSelected = { onEvent(RegisterIntent.GenderChanged(genderOptions[it])) }
            )
            
            if (state.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                BasicText(
                    text = state.errorMessage,
                    style = Theme.typography.body.medium.copy(color = Theme.colors.error)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PrimaryButton(
                caption = stringResource(R.string.personal_info_continue_btn),
                onClick = { onEvent(RegisterIntent.ContinueClicked) },
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonalInfoScreenPreview() {
    SpTheme {
        PersonalInfoScreen(
            state = RegisterState(
                currentStep = RegisterStep.PERSONAL_INFO,
                firstName = "Jane",
                lastName = "Doe",
                gender = "Female"
            ),
            onEvent = {}
        )
    }
}
