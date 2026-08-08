package com.carenest.presentation.ui.visit_summary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.SpTheme
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R

@Composable
fun VisitRatingDialogContent(
    selectedRating: Int,
    reviewText: String,
    isAnonymous: Boolean,
    isSubmitting: Boolean,
    onStarSelected: (Int) -> Unit,
    onReviewTextChanged: (String) -> Unit,
    onAnonymousChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Theme.colors.surface, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Theme.colors.primary.copy(alpha = 0.1f), CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                )
            }
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Theme.colors.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.carenest.designsystem.R.drawable.ic_check_white),
                    tint = Theme.colors.onPrimary,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.visit_rating_title),
            style = Theme.typography.title.copy(fontWeight = FontWeight.Bold),
            color = Theme.colors.primaryFont,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.visit_rating_subtitle),
            style = Theme.typography.body.medium,
            color = Theme.colors.secondaryFont,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
            for (star in 1..5) {
                IconButton(onClick = { onStarSelected(star) }, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = if (star <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = stringResource(
                            R.string.visit_rating_star_content_description,
                            star
                        ),
                        tint = if (star <= selectedRating) Theme.colors.amber else Theme.colors.hint,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(
            text = reviewText,
            onTextChange = onReviewTextChanged,
            hint = "Share your experience...",
            fieldHeight = 100.dp,
            maxLines = 4,
            fieldVerticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
        ) {
            Checkbox(
                checked = isAnonymous,
                onCheckedChange = onAnonymousChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = Theme.colors.primary,
                    uncheckedColor = Theme.colors.hint
                )
            )
            Text(
                text = "Submit anonymously",
                style = Theme.typography.body.small,
                color = Theme.colors.secondaryFont
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.visit_rating_skip_button),
                    style = Theme.typography.body.medium.copy(fontWeight = FontWeight.SemiBold),
                    color = Theme.colors.secondaryFont,
                )
            }
            
            androidx.compose.material3.Button(
                onClick = onSubmit,
                enabled = selectedRating > 0 && !isSubmitting,
                modifier = Modifier.weight(1.5f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Theme.colors.primary,
                    disabledContainerColor = Theme.colors.disable
                )
            ) {
                if (isSubmitting) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Theme.colors.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.visit_rating_submit_button),
                        style = Theme.typography.body.medium.copy(fontWeight = FontWeight.Bold),
                        color = Theme.colors.onPrimary,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    SpTheme {
        VisitRatingDialogContent(
            selectedRating = 3,
            reviewText = "Great service!",
            isAnonymous = false,
            isSubmitting = false,
            onStarSelected = {},
            onReviewTextChanged = {},
            onAnonymousChanged = {},
            onSubmit = {},
            onDismiss = {},
        )
    }
}