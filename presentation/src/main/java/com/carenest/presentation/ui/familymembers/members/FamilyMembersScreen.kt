package com.carenest.presentation.ui.familymembers.members

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Refresh
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.carenest.designsystem.R as RD
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.components.button.PrimaryButton
import com.carenest.designsystem.components.emptystate.EmptyState
import com.carenest.presentation.R
import com.carenest.presentation.core.mvi.ObserveEffect
import com.carenest.presentation.navigation.HideTopBar

import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.carenest.designsystem.components.dialog.CareNestDialog
import com.carenest.designsystem.components.shimmer.ShimmerPlaceholder

@Composable
fun FamilyMembersScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToAddMember: (String?) -> Unit = {},
    onNavigateToEditHealthProfile: (String) -> Unit = {},
    reloadTrigger: Int = 0,
    onShowMessage: (String) -> Unit = {},
    viewModel: FamilyMembersViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val deletedMessage = stringResource(R.string.family_member_deleted)
    val deleteFailedMessage = stringResource(R.string.family_member_delete_failed)
    val notificationsUnavailable = stringResource(R.string.profile_notifications_unavailable)

    androidx.compose.runtime.LaunchedEffect(reloadTrigger) {
        viewModel.loadFamilyMembers()
    }

    ObserveEffect(viewModel.effect) { effect ->
        when (effect) {
            is FamilyMembersEffect.NavigateBack -> onNavigateBack()
            is FamilyMembersEffect.NavigateToAddFamilyMember -> onNavigateToAddMember(null)
            is FamilyMembersEffect.NavigateToEditPersonalInfo -> onNavigateToAddMember(effect.memberId)
            is FamilyMembersEffect.NavigateToEditHealthProfile -> onNavigateToEditHealthProfile(effect.memberId)
            is FamilyMembersEffect.ShowMessage -> {
                val message = when (effect.message) {
                    FamilyMembersMessage.Deleted -> deletedMessage
                    FamilyMembersMessage.DeleteFailed -> deleteFailedMessage
                    FamilyMembersMessage.NotificationsUnavailable -> notificationsUnavailable
                }
                onShowMessage(message)
            }
        }
    }

    if (state.deleteConfirmationMemberId != null) {
        CareNestDialog(
            title = stringResource(R.string.delete_family_member_title),
            message = stringResource(R.string.delete_family_member_confirmation),
            confirmText = stringResource(R.string.remove),
            dismissText = stringResource(R.string.cancel),
            confirmColor = Theme.colors.error,
            onConfirm = { viewModel.onEvent(FamilyMembersEvent.OnConfirmDeleteClicked) },
            onDismiss = { viewModel.onEvent(FamilyMembersEvent.OnDismissDeleteDialogClicked) }
        )
    }

    FamilyMembersContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun FamilyMembersContent(
    state: FamilyMembersState,
    onEvent: (FamilyMembersEvent) -> Unit
) {
    HideTopBar()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            FamilyMembersHeader(
                greeting = stringResource(R.string.family_members_screen_title),
                onBackClick = { onEvent(FamilyMembersEvent.OnBackClicked) },
                onNotificationClick = { onEvent(FamilyMembersEvent.OnNotificationClicked) }
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        when {
            state.isLoading && state.members.isEmpty() -> {
                FamilyMembersLoadingShimmer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            state.loadFailed -> {
                FamilyMembersLoadError(
                    onRetry = { onEvent(FamilyMembersEvent.OnRetryClicked) },
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.family_members_screen_title),
                        style = Theme.typography.display.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Theme.colors.primaryFont
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.family_members_screen_subtitle),
                        style = Theme.typography.body.large.copy(
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        ),
                        color = Theme.colors.secondaryFont
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    state.members.forEach { member ->
                        FamilyMemberCard(
                            member = member,
                            onEditPersonalClick = { onEvent(FamilyMembersEvent.OnEditPersonalInfoClicked(member.id)) },
                            onEditHealthClick = { onEvent(FamilyMembersEvent.OnEditHealthProfileClicked(member.id)) },
                            onDeleteClick = { onEvent(FamilyMembersEvent.OnDeleteMemberClicked(member.id)) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    AddFamilyMemberDashedCard(
                        onClick = { onEvent(FamilyMembersEvent.OnAddFamilyMemberClicked) }
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun FamilyMembersLoadingShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.58f)
                .height(34.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(16.dp),
        )
        Spacer(modifier = Modifier.height(28.dp))

        repeat(3) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Theme.colors.surface,
                shadowElevation = 0.dp,
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ShimmerPlaceholder(
                            modifier = Modifier.size(60.dp),
                            shape = CircleShape,
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            ShimmerPlaceholder(
                                modifier = Modifier
                                    .fillMaxWidth(0.68f)
                                    .height(18.dp),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ShimmerPlaceholder(
                                modifier = Modifier
                                    .width(74.dp)
                                    .height(24.dp),
                                shape = CircleShape,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    ShimmerPlaceholder(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FamilyMembersLoadError(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyState(
                title = stringResource(R.string.family_members_load_failed),
                description = stringResource(R.string.family_members_load_failed_description),
                icon = Icons.Outlined.Refresh,
                accentColor = Theme.colors.primary
            )

            Spacer(modifier = Modifier.height(Theme.spacing.space28))

            PrimaryButton(
                caption = stringResource(R.string.retry),
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.spacing.extraLarge)
            )
        }
    }
}

@Composable
fun FamilyMembersHeader(
    greeting: String,
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
                    contentDescription = stringResource(R.string.back),
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
                text = greeting,
                style = Theme.typography.body.large.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Theme.colors.primary
            )
        }

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
    }
}

@Composable
fun FamilyMemberCard(
    member: FamilyMemberItem,
    onEditPersonalClick: () -> Unit,
    onEditHealthClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isSelf = member.relationship.equals("Self", ignoreCase = true) ||
            member.relationship.equals("PRIMARY", ignoreCase = true) ||
            member.relationship.equals("Primary", ignoreCase = true) ||
            member.relationship.isBlank()
    val displayRelationship = if (isSelf) stringResource(R.string.relationship_self) else member.relationship

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Theme.colors.surface,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Theme.colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarUrl = member.profileImageUrl?.takeIf { it.isNotBlank() }
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = member.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = RD.drawable.ic_profile),
                            contentDescription = null,
                            tint = Theme.colors.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.name,
                        style = Theme.typography.body.large.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = Theme.colors.primaryFont
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Theme.colors.primary.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayRelationship,
                            style = Theme.typography.body.small.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp
                            ),
                            color = Theme.colors.primary
                        )
                    }
                }

                if (!isSelf) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Theme.colors.primary.copy(alpha = 0.08f))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onDeleteClick
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = Theme.colors.secondaryFont,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
            }

            if (member.lastCheckup.isNotBlank() || member.upcomingService.isNotBlank()) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
                ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.family_members_last_checkup),
                        style = Theme.typography.body.small.copy(fontSize = 12.sp),
                        color = Theme.colors.secondaryFont
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = member.lastCheckup,
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        ),
                        color = Theme.colors.primaryFont
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.family_members_upcoming),
                        style = Theme.typography.body.small.copy(fontSize = 12.sp),
                        color = Theme.colors.secondaryFont
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = member.upcomingService,
                        style = Theme.typography.body.medium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = Theme.colors.primary
                    )
                }
                }
            }

            if (!isSelf) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onEditPersonalClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.family_members_edit_personal),
                            style = Theme.typography.body.small.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = Theme.colors.surface
                        )
                    }

                    Button(
                        onClick = onEditHealthClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Theme.colors.primary.copy(alpha = 0.08f))
                    ) {
                        Text(
                            text = stringResource(R.string.family_members_edit_health),
                            style = Theme.typography.body.small.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            color = Theme.colors.primaryFont
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddFamilyMemberDashedCard(
    onClick: () -> Unit
) {
    val strokeColor = Theme.colors.primary.copy(alpha = 0.4f)
    val dashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Theme.colors.primary.copy(alpha = 0.04f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = strokeColor,
                style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Theme.colors.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    tint = Theme.colors.surface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.family_members_add_btn),
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = Theme.colors.primary
            )
        }
    }
}

@Preview(showBackground = true, name = "Family Members Light Mode", widthDp = 390, heightDp = 900)
@Composable
fun FamilyMembersScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        FamilyMembersContent(
            state = FamilyMembersState(),
            onEvent = {}
        )
    }
}

@Preview(showBackground = true, name = "Family Members Dark Mode", widthDp = 390, heightDp = 900)
@Composable
fun FamilyMembersScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        FamilyMembersContent(
            state = FamilyMembersState(),
            onEvent = {}
        )
    }
}
