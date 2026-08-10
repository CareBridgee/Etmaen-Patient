package com.carenest.presentation.ui.profile_completion.validation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.domain.validation.SupportedPhoneCountry
import com.carenest.presentation.R

@Composable
fun ProfileValidationError?.localizedMessage(
    field: ProfileField? = null,
    phoneCountry: SupportedPhoneCountry? = null
): String? {
    val error = this ?: return null
    if (field == ProfileField.EmergencyPhoneNumber && phoneCountry != null) {
        return when (error) {
            ProfileValidationError.PhoneLength -> stringResource(
                R.string.login_validation_phone_length,
                phoneCountry.nationalDigitLength,
                phoneCountry.dialCode
            )
            ProfileValidationError.InvalidPhone -> stringResource(
                when (phoneCountry) {
                    SupportedPhoneCountry.EGYPT -> R.string.login_validation_egypt_phone_prefix
                    SupportedPhoneCountry.SAUDI_ARABIA -> R.string.login_validation_saudi_phone_prefix
                    SupportedPhoneCountry.UAE -> R.string.login_validation_uae_phone_prefix
                }
            )
            else -> stringResource(error.stringRes(field))
        }
    }
    return stringResource(error.stringRes(field))
}

@StringRes
private fun ProfileValidationError.stringRes(field: ProfileField?): Int = when (this) {
    ProfileValidationError.Required -> when (field) {
        ProfileField.FirstName -> R.string.validation_first_name_required
        ProfileField.LastName -> R.string.validation_last_name_required
        ProfileField.DateOfBirth -> R.string.validation_dob_required
        ProfileField.Gender -> R.string.validation_gender_required
        ProfileField.Height -> R.string.validation_height_required
        ProfileField.Weight -> R.string.validation_weight_required
        ProfileField.BloodType -> R.string.validation_blood_type_required
        ProfileField.EmergencyContactName -> R.string.validation_emergency_name_required
        ProfileField.EmergencyRelationship -> R.string.validation_emergency_relationship_required
        ProfileField.EmergencyPhoneNumber -> R.string.validation_phone_required
        else -> R.string.validation_required
    }
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
