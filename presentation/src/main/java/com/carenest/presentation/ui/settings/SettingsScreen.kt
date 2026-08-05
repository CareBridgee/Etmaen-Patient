package com.carenest.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.R as RD
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is SettingsEffect.NavigateBack -> onNavigateBack()
            else -> {}
        }
    }

    SettingsContent(
        state = state,
        onEvent = viewModel::onEvent
    )

    if (state.isLanguagePickerDialogVisible) {
        LanguagePickerDialog(
            currentLanguageCode = state.languageCode,
            onLanguageSelected = { viewModel.onEvent(SettingsEvent.OnLanguageSelected(it)) },
            onDismiss = { viewModel.onEvent(SettingsEvent.OnDismissLanguagePicker) }
        )
    }
}

@Composable
fun SettingsContent(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    HideTopBar()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            SettingsHeader(
                patientName = state.patientName,
                onBackClick = { onEvent(SettingsEvent.OnBackClicked) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.settings_screen_title),
                style = Theme.typography.display.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = Theme.colors.primaryFont
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.settings_screen_subtitle),
                style = Theme.typography.body.large.copy(
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                ),
                color = Theme.colors.secondaryFont
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App Preferences Section
            SettingsSectionTitle(text = stringResource(R.string.settings_section_app_preferences))
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Theme.colors.surface,
                shadowElevation = 0.dp
            ) {
                Column {
                    SettingsRowClickable(
                        iconRes = RD.drawable.ic_language,
                        title = stringResource(R.string.settings_language_title),
                        trailingText = if (state.languageCode == "ar") "العربية" else "English",
                        onClick = { onEvent(SettingsEvent.OnLanguageClicked) }
                    )
                    HorizontalDivider(color = Theme.colors.divider, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRowSwitch(
                        iconRes = RD.drawable.ic_dark_mode,
                        title = stringResource(R.string.settings_dark_mode_title),
                        checked = state.isDarkMode,
                        onCheckedChange = { onEvent(SettingsEvent.OnDarkModeToggled(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications Section
            SettingsSectionTitle(text = stringResource(R.string.settings_section_notifications))
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Theme.colors.surface,
                shadowElevation = 0.dp
            ) {
                Column {
                    SettingsRowSwitch(
                        iconRes = RD.drawable.ic_outline_email,
                        title = stringResource(R.string.settings_email_updates_title),
                        checked = state.emailUpdatesEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.OnEmailUpdatesToggled(it)) }
                    )
                    HorizontalDivider(color = Theme.colors.divider, modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsRowSwitch(
                        iconRes = RD.drawable.comment_sms,
                        title = stringResource(R.string.settings_sms_alerts_title),
                        checked = state.smsAlertsEnabled,
                        onCheckedChange = { onEvent(SettingsEvent.OnSmsAlertsToggled(it)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security & Privacy Section
            SettingsSectionTitle(text = stringResource(R.string.settings_section_security_privacy))
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Theme.colors.surface,
                shadowElevation = 0.dp
            ) {
                Column {
                    SettingsRowClickable(
                        iconRes = RD.drawable.ic_verified,
                        title = stringResource(R.string.settings_privacy_policy_title),
                        onClick = { onEvent(SettingsEvent.OnPrivacyPolicyClicked) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsHeader(
    patientName: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.surface)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBackClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_profile),
                    contentDescription = null,
                    tint = Theme.colors.surface,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = patientName,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = Theme.colors.primary
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = Theme.typography.body.medium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        ),
        color = Theme.colors.primary
    )
}

@Composable
fun SettingsRowClickable(
    iconRes: Int,
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = Theme.typography.body.large.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = Theme.colors.primaryFont,
            modifier = Modifier.weight(1f)
        )

        if (trailingText != null) {
            Text(
                text = trailingText,
                style = Theme.typography.body.medium.copy(fontSize = 14.sp),
                color = Theme.colors.secondaryFont
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Icon(
            painter = painterResource(id = RD.drawable.ic_chevron_right),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsRowSwitch(
    iconRes: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = Theme.typography.body.large.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = Theme.colors.primaryFont,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Theme.colors.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Theme.colors.secondaryFont.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
fun LanguagePickerDialog(
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_select_language_title),
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("en") }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguageCode == "en",
                        onClick = { onLanguageSelected("en") },
                        colors = RadioButtonDefaults.colors(selectedColor = Theme.colors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.settings_language_english),
                        style = Theme.typography.body.large,
                        color = Theme.colors.primaryFont
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected("ar") }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguageCode == "ar",
                        onClick = { onLanguageSelected("ar") },
                        colors = RadioButtonDefaults.colors(selectedColor = Theme.colors.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.settings_language_arabic),
                        style = Theme.typography.body.large,
                        color = Theme.colors.primaryFont
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Theme.colors.primary
                )
            }
        },
        containerColor = Theme.colors.surface
    )
}

@Preview(showBackground = true, name = "Settings Light Mode", widthDp = 390, heightDp = 844)
@Composable
fun SettingsScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        SettingsContent(
            state = SettingsState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Settings Dark Mode", widthDp = 390, heightDp = 844)
@Composable
fun SettingsScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        SettingsContent(
            state = SettingsState(),
            onEvent = {}
        )
    }
}
