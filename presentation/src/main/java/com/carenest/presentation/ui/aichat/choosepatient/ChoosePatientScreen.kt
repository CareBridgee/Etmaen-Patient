package com.carenest.presentation.ui.aichat.choosepatient

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.components.button.ButtonIconPosition
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.designsystem.R as RD
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.home.components.HomeGreetingBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.verticalScroll

@Composable
fun ChoosePatientScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChoosePatientViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ChoosePatientEffect.NavigateToChat -> {
                onNavigateToChat(effect.patientId)
            }
        }
    }

    HideTopBar()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(androidx.compose.foundation.rememberScrollState())
                .padding(16.dp)
        ) {
            androidx.compose.material3.IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Theme.colors.primaryFont
                )
            }
            
            HomeGreetingBar(
                greetingText = stringResource(R.string.services_greeting), // "Good morning, Elena"
                avatarUrl = null,
                onNotificationClick = { }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.choose_patient_title),
                style = Theme.typography.display.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.choose_patient_description),
                style = Theme.typography.body.large,
                color = Theme.colors.secondaryFont
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Patients list
            state.patients.forEach { patient ->
                PatientCard(
                    patient = patient,
                    onClick = { viewModel.onEvent(ChoosePatientEvent.OnPatientSelected(patient.id)) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Family Member Dashed Button
            DashedButton(
                text = stringResource(R.string.add_family_member),
                onClick = { viewModel.onEvent(ChoosePatientEvent.OnAddFamilyMemberClicked) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info Box
            InfoBox(
                text = stringResource(R.string.add_family_member_info)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            PrimaryButton(
                caption = stringResource(R.string.continue_with_assessment),
                onClick = { viewModel.onEvent(ChoosePatientEvent.OnContinueClicked) },
                modifier = Modifier.fillMaxWidth(),
                iconPainter = painterResource(id = RD.drawable.ic_arrow),
                iconPosition = ButtonIconPosition.End
            )
        }
    }
}

@Composable
fun PatientCard(patient: PatientItem, onClick: () -> Unit) {
    val backgroundColor = if (patient.isSelected) Theme.colors.primary.copy(alpha = 0.1f) else Theme.colors.surfaceVariant
    val borderColor = if (patient.isSelected) Theme.colors.primary else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Initials circle
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            val initials = patient.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
            Text(
                text = initials,
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = patient.name,
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Bold),
                color = Theme.colors.primaryFont
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = patient.relationship,
                style = Theme.typography.body.medium,
                color = Theme.colors.secondaryFont
            )
        }

        if (patient.isSelected) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_check),
                contentDescription = "Selected",
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Icon(
                painter = painterResource(id = RD.drawable.ic_chevron_right),
                contentDescription = "Select",
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DashedButton(text: String, onClick: () -> Unit) {
    val strokeColor = Theme.colors.secondaryFont
    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = strokeColor,
                style = Stroke(width = 2.dp.toPx(), pathEffect = dashPathEffect),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_add),
                contentDescription = null,
                tint = Theme.colors.primaryFont,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = Theme.typography.body.large.copy(fontWeight = FontWeight.Medium),
                color = Theme.colors.primaryFont
            )
        }
    }
}

@Composable
fun InfoBox(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.colors.primary.copy(alpha = 0.1f))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(id = RD.drawable.ic_info),
            contentDescription = "Info",
            tint = Theme.colors.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = Theme.typography.body.medium,
            color = Theme.colors.primary,
            lineHeight = androidx.compose.ui.unit.TextUnit(20f, androidx.compose.ui.unit.TextUnitType.Sp)
        )
    }
}
