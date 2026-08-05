package com.carenest.designsystem.components.dialog

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SPDatePickerDialog(
    state: DatePickerState,
    onDismissRequest: () -> Unit,
    onConfirm: (Long?) -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String = "OK",
    dismissLabel: String = "Cancel"
) {
    val datePickerColors = DatePickerDefaults.colors(
        containerColor = Theme.colors.surface,
        titleContentColor = Theme.colors.primaryFont,
        headlineContentColor = Theme.colors.primaryFont,
        weekdayContentColor = Theme.colors.secondaryFont,
        subheadContentColor = Theme.colors.primaryFont,
        navigationContentColor = Theme.colors.primary,
        yearContentColor = Theme.colors.primaryFont,
        disabledYearContentColor = Theme.colors.onDisable,
        currentYearContentColor = Theme.colors.primary,
        selectedYearContentColor = Theme.colors.onPrimary,
        disabledSelectedYearContentColor = Theme.colors.onDisable,
        selectedYearContainerColor = Theme.colors.primary,
        disabledSelectedYearContainerColor = Theme.colors.disable,
        dayContentColor = Theme.colors.primaryFont,
        disabledDayContentColor = Theme.colors.onDisable,
        selectedDayContentColor = Theme.colors.onPrimary,
        disabledSelectedDayContentColor = Theme.colors.onDisable,
        selectedDayContainerColor = Theme.colors.primary,
        disabledSelectedDayContainerColor = Theme.colors.disable,
        todayContentColor = Theme.colors.primary,
        todayDateBorderColor = Theme.colors.primary,
        dayInSelectionRangeContainerColor = Theme.colors.primaryContainer,
        dayInSelectionRangeContentColor = Theme.colors.onPrimaryContainer,
        dividerColor = Theme.colors.divider
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        colors = datePickerColors,
        tonalElevation = 0.dp,
        modifier = modifier,
        confirmButton = {
            TextButton(
                onClick = { onConfirm(state.selectedDateMillis) },
                enabled = state.selectedDateMillis != null,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
            ) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                colors = ButtonDefaults.textButtonColors(contentColor = Theme.colors.primary)
            ) {
                Text(dismissLabel)
            }
        }
    ) {
        DatePicker(
            state = state,
            colors = datePickerColors
        )
    }
}
