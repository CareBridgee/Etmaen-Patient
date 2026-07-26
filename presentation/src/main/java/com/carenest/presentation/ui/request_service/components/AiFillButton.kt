package com.carenest.presentation.ui.request_service.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.R
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.bounceClick

@Composable
fun AiFillButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier = modifier
            .bounceClick(
                shape = shape,
                onClick = onClick
            )
            .background(Theme.colors.backGround)
            .border(BorderStroke(1.dp, Theme.colors.primaryVariant), shape)
            .padding(horizontal = Theme.spacing.large, vertical = Theme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_ai_sparkles),
            contentDescription = null,
            tint = Theme.colors.primaryVariant,
            modifier = Modifier.size(Theme.size.iconMedium)
        )
        BasicText(
            text = stringResource(id = R.string.request_service_fill_ai),
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryVariant,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview
@Composable
private fun AiFillButtonPreview() {
    SpTheme {
        AiFillButton(onClick = {})
    }
}
