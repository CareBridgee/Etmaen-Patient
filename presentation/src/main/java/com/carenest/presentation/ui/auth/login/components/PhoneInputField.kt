package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.ui.auth.login.Country
import com.carenest.presentation.ui.auth.login.countries

@Composable
fun PhoneInputField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    selectedCountry: Country,
    isDropdownExpanded: Boolean,
    onCountryClick: () -> Unit,
    onCountrySelect: (Country) -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholder = selectedCountry.phoneConfig.format(
        "0".repeat(selectedCountry.phoneConfig.nationalDigitLength)
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Country Code Block
        Box(
            modifier = Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.colors.surface)
                .clickable { onCountryClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicText(
                text = "${selectedCountry.flag} ${selectedCountry.code}",
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont
                )
            )

            DropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { onCountryClick() },
                modifier = Modifier.background(Theme.colors.surface)
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "${country.flag} ${country.name} (${country.code})",
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.primaryFont
                                )
                            )
                        },
                        onClick = { onCountrySelect(country) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Phone Number Input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.colors.surface)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (phone.isEmpty()) {
                BasicText(
                    text = placeholder,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.hint
                    )
                )
            }
            
            BasicTextField(
                value = phone,
                onValueChange = onPhoneChange,
                textStyle = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                visualTransformation = PhoneNumberVisualTransformation(
                    selectedCountry.phoneConfig.groupSizes
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private class PhoneNumberVisualTransformation(
    groupSizes: List<Int>
) : VisualTransformation {
    private val groupEndOffsets = groupSizes
        .runningFold(0, Int::plus)
        .drop(1)
        .dropLast(1)

    override fun filter(text: AnnotatedString): TransformedText {
        val activeSeparators = groupEndOffsets.filter { it < text.length }
        val formatted = buildString {
            text.forEachIndexed { index, char ->
                if (index in activeSeparators) append(' ')
                append(char)
            }
        }
        val transformedSeparatorOffsets = activeSeparators.mapIndexed { index, rawOffset ->
            rawOffset + index
        }

        return TransformedText(
            text = AnnotatedString(formatted),
            offsetMapping = object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    val safeOffset = offset.coerceIn(0, text.length)
                    return safeOffset + activeSeparators.count { it <= safeOffset }
                }

                override fun transformedToOriginal(offset: Int): Int {
                    val safeOffset = offset.coerceIn(0, formatted.length)
                    return (safeOffset - transformedSeparatorOffsets.count { it < safeOffset })
                        .coerceIn(0, text.length)
                }
            }
        )
    }
}
