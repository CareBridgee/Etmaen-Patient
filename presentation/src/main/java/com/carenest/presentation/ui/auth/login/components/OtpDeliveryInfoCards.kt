package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LockClock
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R as DR
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun OtpDeliveryInfoCards(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StaticInfoCard(
            title = stringResource(R.string.phone_input_feature_instant_sms),
            backgroundColor = Theme.colors.primary.copy(alpha = 0.05f),
            contentColor = Theme.colors.primary,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(DR.drawable.ic_message),
                contentDescription = null,
                modifier = Modifier.size(34.dp)
            )
        }

        StaticInfoCard(
            title = stringResource(R.string.phone_input_feature_verified_private),
            backgroundColor = Theme.colors.cardBackground,
            contentColor = Theme.colors.secondaryFont,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Outlined.LockClock,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
private fun StaticInfoCard(
    title: String,
    backgroundColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = Theme.typography.body.medium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
