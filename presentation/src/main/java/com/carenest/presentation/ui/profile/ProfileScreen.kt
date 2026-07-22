package com.carenest.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

import androidx.compose.foundation.layout.statusBarsPadding
import com.carenest.presentation.navigation.HideTopBar

@Composable
fun ProfileScreen() {
    HideTopBar()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.colors.backGround)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.profile_screen_title),
            style = Theme.typography.body.large,
            color = Theme.colors.primaryFont
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    SpTheme {
        ProfileScreen()
    }
}
