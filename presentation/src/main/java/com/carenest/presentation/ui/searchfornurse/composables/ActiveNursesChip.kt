package com.carenest.presentation.ui.searchfornurse.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun ActiveNursesChip(
    count: Int,
    modifier: Modifier = Modifier
) {
    val colors = Theme.colors

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colors.tint.copy(0.10f)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = Theme.spacing.medium,
                vertical = Theme.spacing.space6
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                       Theme.colors.tint,
                        CircleShape
                    )
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.nurse_active_nearby, count),
                style = Theme.typography.body.small.copy(
                    fontWeight = FontWeight.Medium,
                    color = colors.secondary
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    SpTheme {
        ActiveNursesChip(count = 3)
    }
}