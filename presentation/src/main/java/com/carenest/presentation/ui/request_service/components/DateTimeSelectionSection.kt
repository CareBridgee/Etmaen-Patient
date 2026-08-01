package com.carenest.presentation.ui.request_service.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.carenest.designsystem.components.textfield.CustomTextField
import com.carenest.designsystem.theme.Theme
import java.util.Calendar

@Composable
fun DateTimeSelectionSection(
    preferredDate: String,
    preferredHour: Int,
    preferredMinute: Int,
    onDateChanged: (String) -> Unit,
    onTimeChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "When do you need the service?",
            style = Theme.typography.title,
            color = Theme.colors.primaryFont,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(Theme.spacing.small))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                CustomTextField(
                    text = preferredDate,
                    onTextChange = {},
                    readOnly = true,
                    enabled = false,
                    hint = "Date",
                    modifier = Modifier.fillMaxWidth()
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        val calendar = Calendar.getInstance()
                        val parts = preferredDate.split("-")
                        if (parts.size == 3) {
                            calendar.set(parts[0].toIntOrNull() ?: calendar.get(Calendar.YEAR), 
                                         (parts[1].toIntOrNull() ?: (calendar.get(Calendar.MONTH) + 1)) - 1, 
                                         parts[2].toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH))
                        }
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                val formattedMonth = (month + 1).toString().padStart(2, '0')
                                val formattedDay = dayOfMonth.toString().padStart(2, '0')
                                onDateChanged("$year-$formattedMonth-$formattedDay")
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                val timeString = String.format(java.util.Locale.US, "%02d:%02d", preferredHour, preferredMinute)
                CustomTextField(
                    text = timeString,
                    onTextChange = {},
                    readOnly = true,
                    enabled = false,
                    hint = "Time",
                    modifier = Modifier.fillMaxWidth()
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                onTimeChanged(hourOfDay, minute)
                            },
                            preferredHour,
                            preferredMinute,
                            true
                        ).show()
                    }
                )
            }
        }
    }
}
