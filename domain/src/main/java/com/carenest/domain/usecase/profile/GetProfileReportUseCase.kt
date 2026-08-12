package com.carenest.domain.usecase.profile

import com.carenest.domain.repository.ProfileRepository
import javax.inject.Inject

class GetProfileReportUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profileId: String): Result<String> = repository.getProfileReport(profileId)
}
