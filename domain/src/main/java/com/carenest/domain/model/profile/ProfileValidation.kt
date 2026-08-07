package com.carenest.domain.model.profile

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
    PhoneRequired,
    PhoneLength,
    InvalidPhone
}

data class MedicationValidationErrors(
    val name: ProfileValidationError? = null
) {
    val isEmpty: Boolean get() = name == null
}

class ProfileValidationException(
    val fieldErrors: Map<ProfileField, ProfileValidationError> = emptyMap(),
    val medicationErrors: Map<Long, MedicationValidationErrors> = emptyMap()
) : IllegalArgumentException("Please correct the highlighted fields")
