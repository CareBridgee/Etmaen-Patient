package com.carenest.designsystem.components.button

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable

@Composable
fun SocialButton(
    caption: String,
    iconPainter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(29.dp),
                color = Theme.colors.hint // Use outline color
            )
            .clip(RoundedCornerShape(29.dp))
            .background(Color.Transparent)
            .noRippleClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = iconPainter,
            contentDescription = caption,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicText(
            text = caption,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont
            )
        )
    }
}
