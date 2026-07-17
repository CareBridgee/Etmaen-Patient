package com.carenest.presentation.ui.auth.login.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.button.SocialButton
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.login.LoginIntent
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun AuthLandingScreen(onEvent: (LoginIntent) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
    ) {
        val primaryColor = Theme.colors.primary
        

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.wrapContentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    Image(
                        painter = painterResource(DR.drawable.auth_logo),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy((-4).dp),
                        horizontalAlignment = Alignment.Start
                    ) {

                        BasicText(
                            text = stringResource(R.string.app_name_careconnect),
                            style = Theme.typography.display.copy(
                                color = Theme.colors.primary,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )
                        )

                        BasicText(
                            text = stringResource(R.string.app_name_healthcare),
                            style = Theme.typography.title.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SocialButton(
                    caption = stringResource(R.string.auth_continue_google),
                    iconPainter = painterResource(id = DR.drawable.ic_google),
                    onClick = { /* Simulated */ },
                    backgroundColor = Theme.colors.backGround,
                    contentColor = Theme.colors.primaryFont,
                    borderColor = Theme.colors.hint.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialButton(
                    caption = stringResource(R.string.auth_continue_apple),
                    iconPainter = painterResource(id = DR.drawable.ic_bank), // fallback for apple
                    onClick = { /* Simulated */ },
                    backgroundColor = Theme.colors.primaryFont,
                    contentColor = Theme.colors.backGround,
                    iconTint = Theme.colors.backGround
                )

                Spacer(modifier = Modifier.height(16.dp))

                SocialButton(
                    caption = stringResource(R.string.auth_continue_phone),
                    iconPainter = painterResource(id = DR.drawable.ic_call),
                    onClick = { onEvent(LoginIntent.ContinueWithPhoneClicked) },
                    backgroundColor = Theme.colors.primary.copy(alpha = 0.08f),
                    contentColor = Theme.colors.primary,
                    iconTint = Theme.colors.primary
                )

                Spacer(modifier = Modifier.height(48.dp))

                BasicText(
                    text = buildAnnotatedString {
                        val fullText = stringResource(R.string.auth_terms_agreement)
                        withStyle(style = SpanStyle(color = Theme.colors.hint)) {
                            append(fullText)
                        }
                    },
                    style = Theme.typography.body.medium.copy(
                        textAlign = TextAlign.Center
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthLandingScreenPreview() {
    SpTheme {
        AuthLandingScreen(onEvent = {})
    }
}
