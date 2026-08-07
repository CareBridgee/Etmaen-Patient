package com.carenest.presentation.core.validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.domain.validation.PhoneNumberValidationError
import com.carenest.presentation.R

@Composable
fun PhoneNumberValidationError?.localizedMessage(): String? = when (this) {
    PhoneNumberValidationError.Required -> stringResource(R.string.validation_phone_required)
    PhoneNumberValidationError.InvalidLength -> stringResource(R.string.validation_phone_length)
    PhoneNumberValidationError.InvalidFormat -> stringResource(R.string.validation_invalid_phone)
    null -> null
}
