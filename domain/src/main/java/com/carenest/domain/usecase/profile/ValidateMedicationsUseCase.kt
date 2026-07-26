package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MedicationInput
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class ValidateMedicationsUseCase @Inject constructor() {
    operator fun invoke(
        hasNoCurrentMedications: Boolean,
        entries: List<MedicationInput>
    ): Result<List<MedicationInput>> = runCatching {
        ProfileValidator.medications(hasNoCurrentMedications, entries)
    }
}
