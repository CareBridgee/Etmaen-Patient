package com.carenest.domain.validation

import com.carenest.domain.model.profile.BasicHealthUpdate
import com.carenest.domain.model.profile.EmergencyContactInput
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.model.profile.MedicalHistoryUpdate
import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.model.profile.MedicationValidationErrors
import com.carenest.domain.model.profile.MobilityInput
import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.domain.model.profile.PersonalInfoUpdate
import com.carenest.domain.model.profile.ProfileField
import com.carenest.domain.model.profile.ProfileValidationError
import com.carenest.domain.model.profile.ProfileValidationException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object ProfileValidator {
    private val allowedBloodTypes = setOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private val allowedGenders = setOf("MALE", "FEMALE")
    private val nameRegex = Regex("^[\\p{L}\\p{M}][\\p{L}\\p{M} .’'\\-]*$")

    fun personalInfo(
        firstName: String,
        lastName: String,
        displayDateOfBirth: String,
        gender: String
    ): PersonalInfoUpdate {
        val upperGender = gender.trim().uppercase()
        val errors = buildMap {
            validateName(firstName)?.let { put(ProfileField.FirstName, it) }
            validateName(lastName)?.let { put(ProfileField.LastName, it) }
            validateDateOfBirth(displayDateOfBirth)?.let { put(ProfileField.DateOfBirth, it) }
            if (upperGender !in allowedGenders) put(ProfileField.Gender, ProfileValidationError.Required)
        }
        errors.throwIfNotEmpty()
        return PersonalInfoUpdate(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            dateOfBirth = displayDateOfBirth.toBackendDate(),
            gender = upperGender
        )
    }

    fun basicHealth(height: String, weight: String, bloodType: String): BasicHealthUpdate {
        val errors = buildMap {
            validateMeasurement(height, 30.0, 250.0, ProfileValidationError.HeightOutOfRange)
                ?.let { put(ProfileField.Height, it) }
            validateMeasurement(weight, MINIMUM_WEIGHT_KG, MAXIMUM_WEIGHT_KG, ProfileValidationError.WeightOutOfRange)
                ?.let { put(ProfileField.Weight, it) }
            if (bloodType.normalizedBloodType() !in allowedBloodTypes) {
                put(ProfileField.BloodType, ProfileValidationError.InvalidBloodType)
            }
        }
        errors.throwIfNotEmpty()
        return BasicHealthUpdate(
            height = height.trim().toDouble(),
            weight = weight.trim().toDouble(),
            bloodType = bloodType.normalizedBloodType()
        )
    }

    fun medicalConditions(otherConditions: String): String {
        val trimmed = otherConditions.trim()
        if (trimmed.length > 500) {
            throw ProfileValidationException(
                mapOf(ProfileField.OtherConditions to ProfileValidationError.TextTooLong500)
            )
        }
        return trimmed
    }

    fun allergies(
        hasNoKnownAllergies: Boolean,
        selectedAllergyIds: Set<String>,
        otherAllergies: String
    ): Set<String> {
        val trimmedOther = otherAllergies.trim()
        val errors = buildMap {
            if (!hasNoKnownAllergies && selectedAllergyIds.isEmpty() && trimmedOther.isEmpty()) {
                put(ProfileField.AllergiesSelection, ProfileValidationError.AllergySelectionRequired)
            }
            if (trimmedOther.length > 500) {
                put(ProfileField.OtherAllergies, ProfileValidationError.TextTooLong500)
            }
        }
        errors.throwIfNotEmpty()
        return if (hasNoKnownAllergies) emptySet() else selectedAllergyIds
    }

    fun medications(
        hasNoCurrentMedications: Boolean,
        entries: List<MedicationInput>
    ): List<MedicationInput> {
        if (hasNoCurrentMedications) return emptyList()

        val fieldErrors = mutableMapOf<ProfileField, ProfileValidationError>()
        val entryErrors = mutableMapOf<Long, MedicationValidationErrors>()
        val normalizedEntries = entries.map { entry -> entry.copy(name = entry.name.trim()) }
        val completedEntries = normalizedEntries.filter { it.name.isNotEmpty() }

        if (normalizedEntries.isEmpty()) {
            fieldErrors[ProfileField.MedicationsSelection] = ProfileValidationError.MedicationSelectionRequired
        } else if (completedEntries.isEmpty()) {
            val firstEntry = normalizedEntries.first()
            entryErrors[firstEntry.uiKey] = MedicationValidationErrors(
                name = ProfileValidationError.MedicationNameRequired
            )
        }
        completedEntries.forEach { entry ->
            if (entry.name.length > 100) {
                entryErrors[entry.uiKey] = MedicationValidationErrors(
                    name = ProfileValidationError.TextTooLong100
                )
            }
        }
        if (fieldErrors.isNotEmpty() || entryErrors.isNotEmpty()) {
            throw ProfileValidationException(fieldErrors, entryErrors)
        }
        return completedEntries
    }

    fun medicalHistory(
        previousSurgeries: String,
        previousHospitalizations: String
    ): MedicalHistoryUpdate {
        val errors = buildMap {
            if (previousSurgeries.trim().length > 1000) {
                put(ProfileField.PreviousSurgeries, ProfileValidationError.TextTooLong1000)
            }
            if (previousHospitalizations.trim().length > 1000) {
                put(ProfileField.PreviousHospitalizations, ProfileValidationError.TextTooLong1000)
            }
        }
        errors.throwIfNotEmpty()
        return MedicalHistoryUpdate(previousSurgeries.trim(), previousHospitalizations.trim())
    }

    fun mobility(status: MobilityStatus?, notes: String): MobilityInput {
        val errors = buildMap {
            if (status == null) put(ProfileField.MobilityStatus, ProfileValidationError.MobilityRequired)
            if (notes.trim().length > 500) {
                put(ProfileField.MobilityNotes, ProfileValidationError.TextTooLong500)
            }
        }
        errors.throwIfNotEmpty()
        return MobilityInput(requireNotNull(status), notes.trim())
    }

    fun emergencyContact(
        name: String,
        relationship: EmergencyRelationship?,
        phoneNumber: String,
        phoneCountry: SupportedPhoneCountry = SupportedPhoneCountry.EGYPT
    ): EmergencyContactInput {
        val trimmedName = name.trim()
        val trimmedPhone = phoneNumber.trim()
        val errors = buildMap {
            when {
                trimmedName.isBlank() -> put(ProfileField.EmergencyContactName, ProfileValidationError.Required)
                trimmedName.length !in 2..100 -> put(
                    ProfileField.EmergencyContactName,
                    ProfileValidationError.EmergencyNameLength
                )
                !nameRegex.matches(trimmedName) -> put(
                    ProfileField.EmergencyContactName,
                    ProfileValidationError.InvalidName
                )
            }
            if (relationship == null) {
                put(ProfileField.EmergencyRelationship, ProfileValidationError.Required)
            }
            when (PhoneValidator.validate(trimmedPhone, phoneCountry)) {
                PhoneNumberValidationError.Required -> put(
                    ProfileField.EmergencyPhoneNumber,
                    ProfileValidationError.PhoneRequired
                )
                PhoneNumberValidationError.InvalidLength -> put(
                    ProfileField.EmergencyPhoneNumber,
                    ProfileValidationError.PhoneLength
                )
                PhoneNumberValidationError.InvalidFormat -> put(
                    ProfileField.EmergencyPhoneNumber,
                    ProfileValidationError.InvalidPhone
                )
                null -> Unit
            }
        }
        errors.throwIfNotEmpty()
        return EmergencyContactInput(
            contactName = trimmedName,
            relationship = requireNotNull(relationship).backendValue,
            phoneNumber = PhoneValidator.toInternationalNumber(trimmedPhone, phoneCountry)
        )
    }

    private fun validateName(value: String): ProfileValidationError? {
        val trimmed = value.trim()
        return when {
            trimmed.isBlank() -> ProfileValidationError.Required
            trimmed.length !in 2..50 -> ProfileValidationError.NameLength
            !nameRegex.matches(trimmed) -> ProfileValidationError.InvalidName
            else -> null
        }
    }

    private fun parseAnyDate(value: String): Date? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return null
        val patterns = listOf(
            "MM/dd/yyyy",
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (pattern in patterns) {
            val parser = SimpleDateFormat(pattern, Locale.US).apply {
                isLenient = false
                timeZone = UTC
            }
            val parsed = runCatching { parser.parse(trimmed) }.getOrNull()
            if (parsed != null) return parsed
        }
        return null
    }

    private fun validateDateOfBirth(value: String): ProfileValidationError? {
        if (value.isBlank()) return ProfileValidationError.Required
        val date = parseAnyDate(value) ?: return ProfileValidationError.InvalidDate
        // A date of birth is a calendar date, not an instant. Compare its UTC-parsed
        // components with the device's local date so "today" remains valid around
        // local midnight even when the UTC date is still yesterday.
        val enteredDate = Calendar.getInstance(UTC).apply { time = date }
        val today = Calendar.getInstance()
        if (enteredDate.toDateKey() > today.toDateKey()) return ProfileValidationError.FutureDate
        val oldestAllowed = (today.clone() as Calendar).apply { add(Calendar.YEAR, -120) }
        if (enteredDate.toDateKey() < oldestAllowed.toDateKey()) return ProfileValidationError.DateTooOld
        return null
    }

    private fun Calendar.toDateKey(): Int =
        get(Calendar.YEAR) * 10_000 + (get(Calendar.MONTH) + 1) * 100 + get(Calendar.DAY_OF_MONTH)

    private fun validateMeasurement(
        value: String,
        minimum: Double,
        maximum: Double,
        rangeError: ProfileValidationError
    ): ProfileValidationError? {
        if (value.isBlank()) return ProfileValidationError.Required
        val parsed = value.toDoubleOrNull() ?: return ProfileValidationError.InvalidNumber
        if (!parsed.isFinite()) return ProfileValidationError.InvalidNumber
        return if (parsed !in minimum..maximum) rangeError else null
    }

    private fun String.toBackendDate(): String {
        val parsed = parseAnyDate(this) ?: return trim()
        return backendDateFormatter().format(parsed)
    }

    private fun String.normalizedBloodType(): String = replace('−', '-').trim()

    private fun Map<ProfileField, ProfileValidationError>.throwIfNotEmpty() {
        if (isNotEmpty()) throw ProfileValidationException(this)
    }

    private fun displayDateFormatter() = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply {
        isLenient = false
        timeZone = UTC
    }

    private fun backendDateFormatter() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
        timeZone = UTC
    }

    private val UTC: TimeZone = TimeZone.getTimeZone("UTC")

    private const val MINIMUM_WEIGHT_KG = 10.0
    private const val MAXIMUM_WEIGHT_KG = 300.0
}
