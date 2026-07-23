package com.carenest.presentation.ui.profile.validation

import com.carenest.domain.model.profile.LocalMedicationEntry
import com.carenest.presentation.ui.profile.EmergencyRelationship
import com.carenest.presentation.ui.profile.MobilityStatus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

enum class ProfileField {
    FirstName,
    LastName,
    DateOfBirth,
    Gender,
    Height,
    Weight,
    BloodType,
    OtherConditions,
    AllergiesSelection,
    OtherAllergies,
    MedicationsSelection,
    PreviousSurgeries,
    PreviousHospitalizations,
    MobilityStatus,
    MobilityNotes,
    EmergencyContactName,
    EmergencyRelationship,
    EmergencyPhoneNumber
}

enum class ProfileValidationError {
    Required,
    InvalidName,
    NameLength,
    InvalidDate,
    FutureDate,
    DateTooOld,
    InvalidNumber,
    HeightOutOfRange,
    WeightOutOfRange,
    InvalidBloodType,
    TextTooLong100,
    TextTooLong500,
    TextTooLong1000,
    AllergySelectionRequired,
    MedicationSelectionRequired,
    MedicationNameRequired,
    MobilityRequired,
    EmergencyNameLength,
    InvalidPhone
}

data class MedicationValidationErrors(
    val name: ProfileValidationError? = null,
    val dosage: ProfileValidationError? = null,
    val frequency: ProfileValidationError? = null
) {
    val isEmpty: Boolean get() = name == null && dosage == null && frequency == null
}

data class MedicationsValidationResult(
    val fieldErrors: Map<ProfileField, ProfileValidationError> = emptyMap(),
    val entryErrors: Map<String, MedicationValidationErrors> = emptyMap()
) {
    val isValid: Boolean get() = fieldErrors.isEmpty() && entryErrors.isEmpty()
}

object ProfileInputValidator {
    private val allowedBloodTypes = setOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private val nameRegex = Regex("^[\\p{L}\\p{M}][\\p{L}\\p{M} .’'\\-]*$")
    private val phoneRegex = Regex("^\\+?[0-9\\s\\-]{7,20}$")

    fun personalInfo(
        firstName: String,
        lastName: String,
        dateOfBirth: String,
        gender: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        validateName(firstName)?.let { put(ProfileField.FirstName, it) }
        validateName(lastName)?.let { put(ProfileField.LastName, it) }
        validateDateOfBirth(dateOfBirth)?.let { put(ProfileField.DateOfBirth, it) }
        if (gender !in setOf("MALE", "FEMALE")) put(ProfileField.Gender, ProfileValidationError.Required)
    }

    fun basicHealth(
        height: String,
        weight: String,
        bloodType: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        validateMeasurement(height, 30.0, 250.0, ProfileValidationError.HeightOutOfRange)
            ?.let { put(ProfileField.Height, it) }
        validateMeasurement(weight, 1.0, 500.0, ProfileValidationError.WeightOutOfRange)
            ?.let { put(ProfileField.Weight, it) }
        val normalizedBloodType = bloodType.replace('−', '-').trim()
        if (normalizedBloodType !in allowedBloodTypes) {
            put(ProfileField.BloodType, ProfileValidationError.InvalidBloodType)
        }
    }

    fun medicalConditions(otherConditions: String): Map<ProfileField, ProfileValidationError> = buildMap {
        if (otherConditions.trim().length > 500) {
            put(ProfileField.OtherConditions, ProfileValidationError.TextTooLong500)
        }
    }

    fun allergies(
        hasNoKnownAllergies: Boolean,
        selectedAllergyKeys: Set<String>,
        otherAllergies: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        val trimmedOther = otherAllergies.trim()
        if (!hasNoKnownAllergies && selectedAllergyKeys.isEmpty() && trimmedOther.isEmpty()) {
            put(ProfileField.AllergiesSelection, ProfileValidationError.AllergySelectionRequired)
        }
        if (trimmedOther.length > 500) {
            put(ProfileField.OtherAllergies, ProfileValidationError.TextTooLong500)
        }
    }

    fun medications(
        hasNoCurrentMedications: Boolean,
        entries: List<LocalMedicationEntry>
    ): MedicationsValidationResult {
        if (hasNoCurrentMedications) return MedicationsValidationResult()

        val nonBlankEntries = entries.filter { entry ->
            entry.name.isNotBlank() || entry.dosage.isNotBlank() || entry.frequency.isNotBlank()
        }
        val fieldErrors = if (nonBlankEntries.isEmpty()) {
            mapOf(ProfileField.MedicationsSelection to ProfileValidationError.MedicationSelectionRequired)
        } else {
            emptyMap()
        }
        val entryErrors = nonBlankEntries.mapNotNull { entry ->
            val errors = MedicationValidationErrors(
                name = when {
                    entry.name.isBlank() -> ProfileValidationError.MedicationNameRequired
                    entry.name.trim().length > 100 -> ProfileValidationError.TextTooLong100
                    else -> null
                },
                dosage = if (entry.dosage.trim().length > 100) ProfileValidationError.TextTooLong100 else null,
                frequency = if (entry.frequency.trim().length > 100) ProfileValidationError.TextTooLong100 else null
            )
            if (errors.isEmpty) null else entry.localId to errors
        }.toMap()
        return MedicationsValidationResult(fieldErrors, entryErrors)
    }

    fun medicalHistory(
        previousSurgeries: String,
        previousHospitalizations: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        if (previousSurgeries.trim().length > 1000) {
            put(ProfileField.PreviousSurgeries, ProfileValidationError.TextTooLong1000)
        }
        if (previousHospitalizations.trim().length > 1000) {
            put(ProfileField.PreviousHospitalizations, ProfileValidationError.TextTooLong1000)
        }
    }

    fun mobility(
        status: MobilityStatus?,
        notes: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        if (status == null) put(ProfileField.MobilityStatus, ProfileValidationError.MobilityRequired)
        if (notes.trim().length > 500) put(ProfileField.MobilityNotes, ProfileValidationError.TextTooLong500)
    }

    fun emergencyContact(
        name: String,
        relationship: EmergencyRelationship?,
        phoneNumber: String
    ): Map<ProfileField, ProfileValidationError> = buildMap {
        val trimmedName = name.trim()
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
        if (relationship == null) put(ProfileField.EmergencyRelationship, ProfileValidationError.Required)

        val trimmedPhone = phoneNumber.trim()
        val digitsCount = trimmedPhone.count(Char::isDigit)
        when {
            trimmedPhone.isBlank() -> put(ProfileField.EmergencyPhoneNumber, ProfileValidationError.Required)
            !phoneRegex.matches(trimmedPhone) || digitsCount !in 7..15 -> put(
                ProfileField.EmergencyPhoneNumber,
                ProfileValidationError.InvalidPhone
            )
        }
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

    private fun validateDateOfBirth(value: String): ProfileValidationError? {
        if (value.isBlank()) return ProfileValidationError.Required
        val date = runCatching {
            dateFormatter().parse(value.trim())
        }.getOrNull() ?: return ProfileValidationError.InvalidDate

        val today = Calendar.getInstance(UTC).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (date.after(today.time)) return ProfileValidationError.FutureDate

        val oldestAllowed = (today.clone() as Calendar).apply { add(Calendar.YEAR, -120) }
        if (date.before(oldestAllowed.time)) return ProfileValidationError.DateTooOld
        return null
    }

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

    private fun dateFormatter() = SimpleDateFormat("MM/dd/yyyy", Locale.US).apply {
        isLenient = false
        timeZone = UTC
    }

    private val UTC: TimeZone = TimeZone.getTimeZone("UTC")
}
