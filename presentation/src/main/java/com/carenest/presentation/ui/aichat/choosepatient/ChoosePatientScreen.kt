package com.carenest.presentation.ui.aichat.choosepatient

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.ScreenTopBar
import com.carenest.designsystem.R as RD

@Composable
fun ChoosePatientScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ChoosePatientViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ScreenTopBar(
        title = "Choose Patient",
        onLeadingClick = onNavigateBack
    )

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ChoosePatientEffect.NavigateToChat -> {
                onNavigateToChat(effect.patientId)
            }
        }
    }

    ChoosePatientContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ChoosePatientContent(
    state: ChoosePatientState,
    onEvent: (ChoosePatientEvent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 88.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            GreetingSection()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.choose_patient_title),
                style = Theme.typography.display.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = Theme.colors.primaryFont
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.choose_patient_description),
                style = Theme.typography.body.large.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = Theme.colors.secondaryFont
            )

            Spacer(modifier = Modifier.height(32.dp))

            state.patients.forEach { patient ->
                PatientCard(
                    patient = patient,
                    onClick = { onEvent(ChoosePatientEvent.OnPatientSelected(patient.id)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            AddFamilyMemberCard(
                onClick = { onEvent(ChoosePatientEvent.OnAddFamilyMemberClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            InfoCard()

            Spacer(modifier = Modifier.height(32.dp))
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            BottomCTA(
                onClick = { onEvent(ChoosePatientEvent.OnContinueClicked) }
            )
        }
    }
}

@Composable
fun GreetingSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_profile),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = "Good morning, Elena",
            style = Theme.typography.body.large.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = Theme.colors.primary
        )
    }
}

@Composable
fun PatientCard(
    patient: PatientItem,
    onClick: () -> Unit
) {
    val isSelected = patient.isSelected
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary.copy(alpha = 0.06f) else Theme.colors.surface,
        animationSpec = tween(300)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PatientAvatar(isSelected = isSelected)
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = patient.name,
                    style = Theme.typography.body.large.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    color = Theme.colors.primaryFont
                )
                Spacer(modifier = Modifier.height(8.dp))
                RelationshipChip(
                    relationship = patient.relationship,
                    isSelected = isSelected
                )
            }
            
            Icon(
                painter = painterResource(id = RD.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PatientAvatar(isSelected: Boolean) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary else Color.Transparent,
        animationSpec = tween(300)
    )

    Box(modifier = Modifier.size(64.dp)) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Theme.colors.primary.copy(alpha = 0.12f))
                .border(2.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = RD.drawable.ic_profile),
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        
        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.surface)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Theme.colors.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Theme.colors.surface,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RelationshipChip(
    relationship: String,
    isSelected: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.primary else Theme.colors.primary.copy(alpha = 0.12f),
        animationSpec = tween(300)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Theme.colors.surface else Theme.colors.primary,
        animationSpec = tween(300)
    )
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = relationship,
            style = Theme.typography.body.small.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun AddFamilyMemberCard(onClick: () -> Unit) {
    val strokeColor = Theme.colors.secondaryFont.copy(alpha = 0.3f)
    val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = strokeColor,
                style = Stroke(width = 4.dp.toPx(), pathEffect = dashPathEffect),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.add_family_member),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Theme.colors.primary
            )
        }
    }
}

@Composable
fun InfoCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Theme.colors.primary.copy(alpha = 0.08f))
            .padding(24.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = Theme.colors.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.add_family_member_info),
            style = Theme.typography.body.small.copy(
                fontSize = 13.sp,
                lineHeight = 20.sp
            ),
            color = Theme.colors.secondaryFont
        )
    }
}

@Composable
fun BottomCTA(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(32.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.continue_with_assessment),
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Theme.colors.surface
            )
            Spacer(modifier = Modifier.width(12.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Theme.colors.surface,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun ChoosePatientScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        ChoosePatientContent(
            state = ChoosePatientState(
                patients = listOf(
                    PatientItem("1", "Elena Rodriguez", "Self", false),
                    PatientItem("2", "Robert Chen", "Dad", false),
                    PatientItem("3", "Margaret Chen", "Mom", true)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun ChoosePatientScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ChoosePatientContent(
            state = ChoosePatientState(
                patients = listOf(
                    PatientItem("1", "Elena Rodriguez", "Self", true),
                    PatientItem("2", "Robert Chen", "Dad", false),
                    PatientItem("3", "Margaret Chen", "Mom", false)
                )
            ),
            onEvent = {}
        )
    }
}



