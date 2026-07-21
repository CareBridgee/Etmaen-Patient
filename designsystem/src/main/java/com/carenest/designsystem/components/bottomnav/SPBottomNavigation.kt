package com.carenest.designsystem.components.bottomnav

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.R
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme

data class BottomNavItem(
    val label: String,
    val iconRes: Int,
)

@Composable
fun SPBottomNavigation(
    items: List<BottomNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return

    val isDarkTheme = Theme.colors.backGround.luminance() < 0.5f
    val containerColor = if (isDarkTheme) {
        Theme.colors.surface
    } else {
        Theme.colors.primary
    }
    val indicatorColor = if (isDarkTheme) {
        Theme.colors.onPrimaryVariant
    } else {
        Theme.colors.primaryVariant
    }
    val shadowElevation = if (isDarkTheme) 3.dp else 4.dp
    val navShape = RoundedCornerShape(34.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = Theme.spacing.medium,
                end = Theme.spacing.medium,
                bottom = Theme.spacing.space20,
            ),
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = shadowElevation, shape = navShape, clip = false)
                .clip(navShape)
                .background(containerColor),
        ) {
            val itemWidth = maxWidth / items.size
            val layoutDirection = LocalLayoutDirection.current
            val boundedSelectedIndex = selectedIndex.coerceIn(items.indices)
            val visualSelectedIndex = if (layoutDirection == LayoutDirection.Rtl) {
                items.lastIndex - boundedSelectedIndex
            } else {
                boundedSelectedIndex
            }
            val indicatorOffset by animateDpAsState(
                targetValue = itemWidth * visualSelectedIndex,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                label = "bottomNavIndicatorOffset",
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .fillMaxHeight()
                    .padding(
                        horizontal = Theme.spacing.extraSmall,
                        vertical = Theme.spacing.space10,
                    )
                    .clip(RoundedCornerShape(percent = 50))
                    .background(indicatorColor),
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEachIndexed { index, item ->
                    SPBottomNavigationItem(
                        item = item,
                        isSelected = index == boundedSelectedIndex,
                        isDarkTheme = isDarkTheme,
                        onClick = { onItemSelected(index) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun SPBottomNavigationItem(
    item: BottomNavItem,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedColor = if (isDarkTheme) {
        Theme.colors.primaryFont
    } else {
        Theme.colors.onPrimary
    }
    val inactiveColor = if (isDarkTheme) {
        Theme.colors.secondaryFont.copy(alpha = 0.94f)
    } else {
        Theme.colors.onPrimary.copy(alpha = 0.68f)
    }
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else inactiveColor,
        animationSpec = tween(durationMillis = 160),
        label = "bottomNavContentColor",
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
        label = "bottomNavIconScale",
    )

    Row(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(vertical = Theme.spacing.extraSmall),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(item.iconRes),
            contentDescription = item.label,
            tint = contentColor,
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                },
        )
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(animationSpec = tween(durationMillis = 140)) +
                expandHorizontally(
                    animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
                    expandFrom = Alignment.Start,
                ),
            exit = fadeOut(animationSpec = tween(durationMillis = 100)) +
                shrinkHorizontally(
                    animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing),
                    shrinkTowards = Alignment.Start,
                ),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(Theme.spacing.extraSmall))
                BasicText(
                    text = item.label,
                    maxLines = 1,
                    style = Theme.typography.body.small.copy(
                        color = contentColor,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationPreview(isDarkTheme: Boolean) {
    SpTheme(isDarkTheme = isDarkTheme, languageCode = "en") {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround)
                .padding(top = Theme.spacing.extraLarge),
            contentAlignment = Alignment.BottomCenter,
        ) {
            SPBottomNavigation(
                items = remember {
                    listOf(
                        BottomNavItem("Home", R.drawable.ic_bottom_nav_home),
                        BottomNavItem("Services", R.drawable.ic_bottom_nav_services),
                        BottomNavItem("Bookings", R.drawable.ic_bottom_nav_bookings),
                        BottomNavItem("Profile", R.drawable.ic_bottom_nav_profile),
                    )
                },
                selectedIndex = 1,
                onItemSelected = {},
            )
        }
    }
}

@Preview(
    name = "Patient Bottom Navigation - Light",
    widthDp = 390,
    heightDp = 132,
    showBackground = true,
)
@Composable
private fun BottomNavigationLightPreview() {
    BottomNavigationPreview(isDarkTheme = false)
}

@Preview(
    name = "Patient Bottom Navigation - Dark",
    widthDp = 390,
    heightDp = 132,
    showBackground = true,
)
@Composable
private fun BottomNavigationDarkPreview() {
    BottomNavigationPreview(isDarkTheme = true)
}
