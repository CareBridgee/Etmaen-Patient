package com.carenest.presentation.ui.auth.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.SocialButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.login.LoginIntent
import com.carenest.designsystem.R as DR

@Composable
fun AuthLandingScreen(onEvent: (LoginIntent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        BasicText(
            text = "CareConnect\nHealthcare",
            style = Theme.typography.display.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicText(
            text = "Reassuring care for you and your loved ones",
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        SocialButton(
            caption = "Continue with Google",
            iconPainter = painterResource(id = DR.drawable.ic_google),
            onClick = { /* Simulated */ }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        SocialButton(
            caption = "Continue with Apple",
            iconPainter = painterResource(id = DR.drawable.ic_bank),
            onClick = { /* Simulated */ }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        SocialButton(
            caption = "Continue with Phone",
            iconPainter = painterResource(id = DR.drawable.ic_call),
            onClick = { onEvent(LoginIntent.ContinueWithPhoneClicked) }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        BasicText(
            text = "joining our app means you agree with our Terms of use and privacy policy",
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.hint,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthLandingScreenPreview() {
    SpTheme {
        AuthLandingScreen(onEvent = {})
    }
}
