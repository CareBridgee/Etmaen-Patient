package com.carenest.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.designsystem.R as RD
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun ProfileAvatarHeader(
    avatarUrl: Any?,
    onEditAvatarClick: () -> Unit,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 120.dp,
    badgeSize: Dp = 36.dp,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val model = when (avatarUrl) {
                is String -> avatarUrl.takeIf { it.isNotBlank() }
                else -> avatarUrl
            }
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = stringResource(R.string.profile_avatar_content_description),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(id = RD.drawable.ic_profile),
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(avatarSize * 0.4f)
                )
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Theme.colors.surface,
                        strokeWidth = 2.5.dp
                    )
                }
            }
        }

        if (!isLoading) {
            Box(
                modifier = Modifier
                    .size(badgeSize)
                    .clip(CircleShape)
                    .background(Theme.colors.primary)
                    .clickable(
                        enabled = enabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onEditAvatarClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.profile_avatar_content_description),
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(badgeSize * 0.5f)
                )
            }
        }
    }
}