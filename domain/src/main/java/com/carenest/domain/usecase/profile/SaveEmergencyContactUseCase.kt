package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.EmergencyContact
import com.carenest.domain.model.profile.EmergencyRelationship
import com.carenest.domain.model.profile.ProfileException
import com.carenest.domain.repository.ProfileRepository
import com.carenest.domain.validation.ProfileValidator
import com.carenest.domain.validation.SupportedPhoneCountry
import javax.inject.Inject

class SaveEmergencyContactUseCase @Inject constructor(private val repository: ProfileRepository) {
    suspend operator fun invoke(
        profileId: String,
        emergencyContactId: String?,
        contactName: String,
        relationship: EmergencyRelationship?,
        phoneNumber: String,
        phoneCountry: SupportedPhoneCountry = SupportedPhoneCountry.EGYPT
    ): Result<EmergencyContact> {
        val input = runCatching {
            ProfileValidator.emergencyContact(
                name = contactName,
                relationship = relationship,
                phoneNumber = phoneNumber,
                phoneCountry = phoneCountry
            )
        }.getOrElse { return Result.failure(it) }

        val contacts = repository.getEmergencyContacts(profileId)
            .getOrElse { return Result.failure(it) }

        val existingContact = resolveExistingContact(contacts, emergencyContactId)
            .getOrElse { return Result.failure(it) }

        if (existingContact != null) {
            return repository.updateEmergencyContact(existingContact.id, input)
        }

        val createResult = repository.createEmergencyContact(profileId, input)
        val createFailure = createResult.exceptionOrNull() ?: return createResult
        if (!createFailure.isConflict()) return Result.failure(createFailure)

        val contactsAfterConflict = repository.getEmergencyContacts(profileId)
            .getOrElse { return Result.failure(it) }
        val contactAfterConflict = resolveExistingContact(contactsAfterConflict, emergencyContactId)
            .getOrElse { return Result.failure(it) }
            ?: return Result.failure(createFailure)

        return repository.updateEmergencyContact(contactAfterConflict.id, input)
    }

    private fun resolveExistingContact(
        contacts: List<EmergencyContact>,
        requestedContactId: String?
    ): Result<EmergencyContact?> {
        if (contacts.isEmpty()) return Result.success(null)

        requestedContactId?.let { requestedId ->
            contacts.firstOrNull { it.id == requestedId }?.let {
                return Result.success(it)
            }
        }

        return when (contacts.size) {
            1 -> Result.success(contacts.single())
            else -> Result.failure(
                ProfileException(
                    "Multiple emergency contacts were found. Select one before updating."
                )
            )
        }
    }

    private fun Throwable.isConflict(): Boolean =
        (this as? ProfileException)?.statusCode == HTTP_CONFLICT

    private companion object {
        const val HTTP_CONFLICT = 409
    }
}
