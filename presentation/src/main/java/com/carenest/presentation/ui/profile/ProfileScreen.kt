package com.carenest.presentation.ui.profile

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.carenest.presentation.navigation.ScreenTopBar

@Composable
fun ProfileScreen(
    onNavigateToFamilyMembers: () -> Unit = {},
    onNavigateToHealthProfile: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    ScreenTopBar(
        title = stringResource(R.string.profile_screen_title),
        showLeadingIcon = false
    )
    val state by viewModel.state.collectAsState()

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is ProfileEffect.NavigateToFamilyMembers -> onNavigateToFamilyMembers()
            is ProfileEffect.NavigateToHealthProfile -> onNavigateToHealthProfile()
            is ProfileEffect.NavigateToSettings -> onNavigateToSettings()
            is ProfileEffect.NavigateToLogout -> onLogout()
            else -> {}
        }
    }

    ProfileContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun ProfileContent(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
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
            Spacer(modifier = Modifier.height(20.dp))

            ProfileHeaderSection(
                greeting = state.greeting,
                onNotificationClick = { onEvent(ProfileEvent.OnNotificationClicked) }
            )

            Spacer(modifier = Modifier.height(28.dp))

            ProfileAvatarSection(
                userName = state.userName,
                userRole = state.userRole,
                onEditAvatarClick = { onEvent(ProfileEvent.OnEditAvatarClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileMenuListSection(
                state = state,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileLogoutCard(
                onClick = { onEvent(ProfileEvent.OnLogoutClicked) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = state.appVersion,
                style = Theme.typography.body.small.copy(
                    fontSize = 12.sp
                ),
                color = Theme.colors.secondaryFont.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileHeaderSection(
    greeting: String,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = greeting,
            style = Theme.typography.body.large.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            ),
            color = Theme.colors.primary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onNotificationClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_notification),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(22.dp)
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
        }
    }
}

@Composable
fun ProfileAvatarSection(
    userName: String,
    userRole: String,
    onEditAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8E5FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_profile),
                    contentDescription = null,
                    tint = Color(0xFF7168F6),
                    modifier = Modifier.size(48.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEditAvatarClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            style = Theme.typography.display.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            color = Theme.colors.primaryFont
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = userRole,
            style = Theme.typography.body.medium.copy(
                fontSize = 15.sp
            ),
            color = Theme.colors.secondaryFont
        )
    }
}

@Composable
fun ProfileMenuListSection(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileMenuItemCard(
            title = stringResource(R.string.profile_personal_info_title),
            subtitle = stringResource(R.string.profile_personal_info_subtitle),
            iconRes = RD.drawable.ic_profile,
            iconBackgroundColor = Theme.colors.primary.copy(alpha = 0.12f),
            iconTint = Theme.colors.primary,
            onClick = { onEvent(ProfileEvent.OnPersonalInfoClicked) }
        )

        ProfileMenuItemCard(
            title = stringResource(R.string.profile_health_profile_title),
            subtitle = stringResource(R.string.profile_health_profile_subtitle),
            iconRes = RD.drawable.ic_id_card,
            iconBackgroundColor = Theme.colors.primary,
            iconTint = Theme.colors.surface,
            onClick = { onEvent(ProfileEvent.OnHealthProfileClicked) }
        )

        ProfileMenuItemCard(
            title = stringResource(R.string.profile_family_members_title),
            subtitle = stringResource(
                R.string.profile_family_members_subtitle,
                state.activeDependentsCount
            ),
            iconRes = RD.drawable.ic_elderly,
            iconBackgroundColor = Theme.colors.primary.copy(alpha = 0.12f),
            iconTint = Theme.colors.primary,
            onClick = { onEvent(ProfileEvent.OnFamilyMembersClicked) }
        )

        ProfileMenuItemCard(
            title = stringResource(R.string.profile_addresses_title),
            subtitle = stringResource(R.string.profile_addresses_subtitle),
            iconRes = RD.drawable.ic_location,
            iconBackgroundColor = Theme.colors.primary.copy(alpha = 0.12f),
            iconTint = Theme.colors.primary,
            onClick = { onEvent(ProfileEvent.OnAddressesClicked) }
        )

        ProfileMenuItemCard(
            title = stringResource(R.string.profile_payment_title),
            subtitle = state.paymentMethodInfo,
            iconRes = RD.drawable.ic_payment_method,
            iconBackgroundColor = Theme.colors.primary.copy(alpha = 0.12f),
            iconTint = Theme.colors.primary,
            onClick = { onEvent(ProfileEvent.OnPaymentClicked) }
        )

        ProfileMenuItemCard(
            title = stringResource(R.string.profile_settings_title),
            subtitle = stringResource(R.string.profile_settings_subtitle),
            imageVector = Icons.Outlined.Settings,
            iconBackgroundColor = Theme.colors.primary.copy(alpha = 0.12f),
            iconTint = Theme.colors.primary,
            onClick = { onEvent(ProfileEvent.OnSettingsClicked) }
        )
    }
}

@Composable
fun ProfileMenuItemCard(
    title: String,
    subtitle: String,
    iconRes: Int? = null,
    imageVector: ImageVector? = null,
    iconBackgroundColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = Theme.colors.surface,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (imageVector != null) {
                    Icon(
                        imageVector = imageVector,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Theme.typography.body.large.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = Theme.colors.primaryFont
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = Theme.typography.body.small.copy(
                        fontSize = 13.sp
                    ),
                    color = Theme.colors.secondaryFont
                )
            }

            Icon(
                painter = painterResource(id = RD.drawable.ic_chevron_right),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ProfileLogoutCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        color = Theme.colors.errorContainer.copy(alpha = 0.25f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFDAD6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_logout),
                    contentDescription = null,
                    tint = Theme.colors.error,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.profile_logout_title),
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                color = Theme.colors.error
            )
        }
    }
}

@Preview(showBackground = true, name = "Profile Light Mode", widthDp = 390, heightDp = 900)
@Composable
fun ProfileScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        ProfileContent(
            state = ProfileState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Dark Mode", widthDp = 390, heightDp = 900)
@Composable
fun ProfileScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        ProfileContent(
            state = ProfileState(),
            onEvent = {}
        )
    }
}
