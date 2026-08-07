package com.carenest.presentation.ui.profile_completion.validation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.presentation.R

@Composable
fun ProfileValidationError?.localizedMessage(): String? = this?.let { stringResource(it.stringRes()) }

@StringRes
private fun ProfileValidationError.stringRes(): Int = when (this) {
    ProfileValidationError.Required -> R.string.validation_required
    ProfileValidationError.InvalidName -> R.string.validation_invalid_name
    ProfileValidationError.NameLength -> R.string.validation_name_length
    ProfileValidationError.InvalidDate -> R.string.validation_invalid_date
    ProfileValidationError.FutureDate -> R.string.validation_future_date
    ProfileValidationError.DateTooOld -> R.string.validation_date_too_old
    ProfileValidationError.InvalidNumber -> R.string.validation_invalid_number
    ProfileValidationError.HeightOutOfRange -> R.string.validation_height_range
    ProfileValidationError.WeightOutOfRange -> R.string.validation_weight_range
    ProfileValidationError.InvalidBloodType -> R.string.validation_blood_type
    ProfileValidationError.TextTooLong100 -> R.string.validation_text_too_long_100
    ProfileValidationError.TextTooLong500 -> R.string.validation_text_too_long_500
    ProfileValidationError.TextTooLong1000 -> R.string.validation_text_too_long_1000
    ProfileValidationError.AllergySelectionRequired -> R.string.validation_allergy_selection
    ProfileValidationError.MedicationSelectionRequired -> R.string.validation_medication_selection
    ProfileValidationError.MedicationNameRequired -> R.string.validation_medication_name
    ProfileValidationError.MobilityRequired -> R.string.validation_mobility_required
    ProfileValidationError.EmergencyNameLength -> R.string.validation_emergency_name_length
    ProfileValidationError.PhoneRequired -> R.string.validation_phone_required
    ProfileValidationError.PhoneLength -> R.string.validation_phone_length
    ProfileValidationError.InvalidPhone -> R.string.validation_invalid_phone
}
