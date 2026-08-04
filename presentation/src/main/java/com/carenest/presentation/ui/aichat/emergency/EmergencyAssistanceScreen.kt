package com.carenest.presentation.ui.aichat.emergency

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.R as RD
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar
import com.carenest.presentation.ui.aichat.chat.ChatInputBar

@Composable
fun EmergencyAssistanceScreen(
    onNavigateBack: () -> Unit,
    onCallAmbulance: () -> Unit = {},
    onCallFamilyMember: () -> Unit = {},
    onDismiss: () -> Unit = {},
    viewModel: EmergencyAssistanceViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            EmergencyAssistanceEffect.NavigateBack -> onNavigateBack()
            EmergencyAssistanceEffect.CallAmbulance -> onCallAmbulance()
            EmergencyAssistanceEffect.CallFamilyMember -> onCallFamilyMember()
            EmergencyAssistanceEffect.DismissEmergency -> onDismiss()
        }
    }

    EmergencyAssistanceContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun EmergencyAssistanceContent(
    state: EmergencyAssistanceState,
    onEvent: (EmergencyAssistanceEvent) -> Unit
) {
    HideTopBar()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            EmergencyAssistanceCard(
                onCallAmbulanceClick = { onEvent(EmergencyAssistanceEvent.OnCallAmbulanceClicked) },
                onCallEmergencyContactClick = { onEvent(EmergencyAssistanceEvent.OnCallFamilyMemberClicked) },
                onDismissClick = { onEvent(EmergencyAssistanceEvent.OnDismissClicked) }
            )
        }

        ChatInputBar(
            inputText = state.inputText,
            onInputChange = { onEvent(EmergencyAssistanceEvent.OnInputTextChanged(it)) },
            onSendClick = { onEvent(EmergencyAssistanceEvent.OnSendMessage) }
        )
    }
}

@Composable
fun EmergencyAssistanceCard(
    onCallAmbulanceClick: () -> Unit,
    onCallEmergencyContactClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = Theme.colors.surface,
        border = BorderStroke(1.dp, Theme.colors.error.copy(alpha = 0.05f)),
        shadowElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color(0xFFFFDAD6).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 20.dp, y = (-20).dp)
                        .clip(CircleShape)
                        .background(Theme.colors.error.copy(alpha = 0.04f))
                )

                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-20).dp, y = 20.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.error.copy(alpha = 0.04f))
                )

                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFFFDAD6))
                        .border(4.dp, Color.White, RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    EmergencyAsteriskIcon(
                        modifier = Modifier.size(44.dp),
                        tint = Theme.colors.error
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Theme.colors.error)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.emergency_urgent_badge),
                        style = Theme.typography.body.small.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 0.8.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.emergency_assistance_required),
                    style = Theme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        lineHeight = 28.sp
                    ),
                    color = Theme.colors.primaryFont
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.emergency_description),
                    style = Theme.typography.body.medium.copy(
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    ),
                    color = Theme.colors.secondaryFont
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCallAmbulanceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.error)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        MedicalKitIcon(
                            modifier = Modifier.size(20.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.emergency_call_ambulance),
                            style = Theme.typography.body.large.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onCallEmergencyContactClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFDAD6))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = RD.drawable.ic_call),
                            contentDescription = null,
                            tint = Theme.colors.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.emergency_call_contact),
                            style = Theme.typography.body.large.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Theme.colors.error
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.emergency_dismiss),
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = Theme.colors.secondaryFont,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onDismissClick)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmergencyAsteriskIcon(
    modifier: Modifier = Modifier,
    tint: Color = Theme.colors.error
) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / 36f
        val scaleY = size.height / 36f
        val path = Path().apply {
            moveTo(13.8f * scaleX, 36f * scaleY)
            lineTo(13.8f * scaleX, 24.1f * scaleY)
            lineTo(3.5f * scaleX, 30.05f * scaleY)
            lineTo(0f * scaleX, 24f * scaleY)
            lineTo(10.3f * scaleX, 18f * scaleY)
            lineTo(0f * scaleX, 12.05f * scaleY)
            lineTo(3.5f * scaleX, 6f * scaleY)
            lineTo(13.8f * scaleX, 11.95f * scaleY)
            lineTo(13.8f * scaleX, 0f * scaleY)
            lineTo(20.8f * scaleX, 0f * scaleY)
            lineTo(20.8f * scaleX, 11.95f * scaleY)
            lineTo(31.1f * scaleX, 6f * scaleY)
            lineTo(34.6f * scaleX, 12.05f * scaleY)
            lineTo(24.3f * scaleX, 18f * scaleY)
            lineTo(34.6f * scaleX, 24f * scaleY)
            lineTo(31.1f * scaleX, 30.05f * scaleY)
            lineTo(20.8f * scaleX, 24.1f * scaleY)
            lineTo(20.8f * scaleX, 36f * scaleY)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Composable
fun MedicalKitIcon(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        val path = Path().apply {
            moveTo(8f * scaleX, 6f * scaleY)
            lineTo(8f * scaleX, 4f * scaleY)
            lineTo(16f * scaleX, 4f * scaleY)
            lineTo(16f * scaleX, 6f * scaleY)
            lineTo(20f * scaleX, 6f * scaleY)
            lineTo(20f * scaleX, 20f * scaleY)
            lineTo(4f * scaleX, 20f * scaleY)
            lineTo(4f * scaleX, 6f * scaleY)
            close()

            moveTo(11f * scaleX, 10f * scaleY)
            lineTo(13f * scaleX, 10f * scaleY)
            lineTo(13f * scaleX, 12f * scaleY)
            lineTo(15f * scaleX, 12f * scaleY)
            lineTo(15f * scaleX, 14f * scaleY)
            lineTo(13f * scaleX, 14f * scaleY)
            lineTo(13f * scaleX, 16f * scaleY)
            lineTo(11f * scaleX, 16f * scaleY)
            lineTo(11f * scaleX, 14f * scaleY)
            lineTo(9f * scaleX, 14f * scaleY)
            lineTo(9f * scaleX, 12f * scaleY)
            lineTo(11f * scaleX, 12f * scaleY)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Emergency Assistance - Light Mode")
@Composable
fun EmergencyAssistanceScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        EmergencyAssistanceContent(
            state = EmergencyAssistanceState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844, name = "Emergency Assistance - Dark Mode")
@Composable
fun EmergencyAssistanceScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        EmergencyAssistanceContent(
            state = EmergencyAssistanceState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390, name = "Component - Emergency Card")
@Composable
fun EmergencyAssistanceCardPreview() {
    SpTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EmergencyAssistanceCard(
                onCallAmbulanceClick = {},
                onCallEmergencyContactClick = {},
                onDismissClick = {}
            )
        }
    }
}
