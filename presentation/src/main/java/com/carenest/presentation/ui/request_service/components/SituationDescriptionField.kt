package com.carenest.presentation.ui.request_service.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable

@Composable
fun SituationDescriptionField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isListening: Boolean = false,
    onMicClick: () -> Unit = {},
) {
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        BasicText(
            text = stringResource(id = com.carenest.designsystem.R.string.request_service_situation_label),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = Theme.shapes.large)
                .clip(Theme.shapes.large)
                .background(Theme.colors.backGround)
        ) {
            CustomTextField(
                text = text,
                onTextChange = onTextChange,
                hint = stringResource(id = com.carenest.designsystem.R.string.request_service_situation_hint),
                containerColor = Color.Transparent,
                borderColor = Color.Transparent,
                onFocusBorderColor = Color.Transparent,
                fieldHeight = 160.dp,
                singleLine = false,
                maxLines = 10,
                fieldVerticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Theme.spacing.small)
            )

            Icon(
                painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_mic),
                contentDescription = null,
                tint = if (isListening) Theme.colors.primary else Theme.colors.secondaryFont,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(Theme.spacing.medium)
                    .size(24.dp)
                    .then(
                        if (isListening) Modifier.scale(scale) else Modifier
                    )
                    .noRippleClickable(onClick = onMicClick)
            )
        }
    }
}

@Preview
@Composable
private fun SituationDescriptionFieldPreview() {
    SpTheme {
        Box(
            modifier = Modifier
                .background(Theme.colors.surfaceVariant)
                .padding(16.dp)
        ) {
            SituationDescriptionField(
                text = "",
                onTextChange = {}
            )
        }
    }
}
