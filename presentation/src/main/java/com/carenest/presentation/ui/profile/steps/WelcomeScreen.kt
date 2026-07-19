package com.carenest.presentation.ui.profile.steps

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.presentation.navigation.ScreenTopBar

@Composable
fun WelcomeScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    ScreenTopBar(
        title = stringResource(R.string.welcome_topbar_title),
        showLeadingIcon = true,
        onLeadingClick = onBack
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeHero()

        Spacer(modifier = Modifier.height(36.dp))

        BasicText(
            text = stringResource(R.string.welcome_title),
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        BasicText(
            text = stringResource(R.string.welcome_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            ),
            modifier = Modifier.padding(horizontal = 18.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        WelcomeActionCard(
            title = stringResource(R.string.welcome_complete_profile_title),
            subtitle = stringResource(R.string.welcome_recommended),
            icon = Icons.Rounded.ChevronRight,
            containerColor = Theme.colors.primary,
            contentColor = Theme.colors.onPrimary,
            borderColor = Theme.colors.primary,
            onClick = onContinue
        )

        Spacer(modifier = Modifier.height(16.dp))

        WelcomeActionCard(
            title = stringResource(R.string.welcome_skip_title),
            subtitle = stringResource(R.string.welcome_skip_subtitle),
            icon = Icons.AutoMirrored.Rounded.ArrowForward,
            containerColor = Theme.colors.surface,
            contentColor = Theme.colors.primary,
            subtitleColor = Theme.colors.hint,
            borderColor = Theme.colors.primaryContainer,
            onClick = onSkip
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Theme.colors.cardBackground)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(21.dp)
            )
            BasicText(
                text = stringResource(R.string.welcome_skip_warning),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    lineHeight = 17.sp
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WelcomeFeature(Icons.Outlined.Lock, stringResource(R.string.welcome_feature_secure))
            WelcomeFeature(Icons.Outlined.Bolt, stringResource(R.string.welcome_feature_fast))
            WelcomeFeature(Icons.Outlined.VerifiedUser, stringResource(R.string.welcome_feature_verified))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun WelcomeHero() {
    Box(
        modifier = Modifier
            .size(192.dp)
            .clip(CircleShape)
            .background(Theme.colors.primaryContainer.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Theme.colors.primaryContainer.copy(alpha = 0.72f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MedicalInformation,
                contentDescription = null,
                tint = Theme.colors.primary,
                modifier = Modifier.size(76.dp)
            )
        }
    }
}

@Composable
private fun WelcomeActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    subtitleColor: Color = contentColor
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(24.dp))
            .clip(RoundedCornerShape(24.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicText(
                text = title,
                style = Theme.typography.title.copy(
                    color = contentColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
            BasicText(
                text = subtitle,
                style = Theme.typography.body.small.copy(
                    color = subtitleColor,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp
                )
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(34.dp)
        )
    }
}

@Composable
private fun WelcomeFeature(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Theme.colors.disable.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Theme.colors.hint.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
        }
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.hint,
                fontSize = 10.sp
            )
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun WelcomeScreenPreview() {
    SpTheme {
        WelcomeScreen(
            onBack = {},
            onContinue = {},
            onSkip = {}
        )
    }
}
