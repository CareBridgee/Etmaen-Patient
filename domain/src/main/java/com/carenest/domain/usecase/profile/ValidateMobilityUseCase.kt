package com.carenest.domain.usecase.profile

import com.carenest.domain.model.profile.MobilityInput
import com.carenest.domain.model.profile.MobilityStatus
import com.carenest.domain.validation.ProfileValidator
import javax.inject.Inject

class ValidateMobilityUseCase @Inject constructor() {
    operator fun invoke(status: MobilityStatus?, notes: String): Result<MobilityInput> =
        runCatching { ProfileValidator.mobility(status, notes) }
}
