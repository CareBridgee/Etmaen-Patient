package com.carenest.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.designsystem.util.noRippleClickable

@Composable
fun SegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Theme.colors.surface, RoundedCornerShape(14.dp))
            .border(1.dp, Theme.colors.hint, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(
                        if (isSelected) Theme.colors.primary else Color.Transparent,
                        if (isSelected) RoundedCornerShape(14.dp) else RoundedCornerShape(0.dp)
                    )
                    .noRippleClickable { onItemSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = item,
                    style = Theme.typography.body.large.copy(
                        color = if (isSelected) Theme.colors.onPrimary else Theme.colors.primaryFont
                    )
                )
            }
        }
    }
}
