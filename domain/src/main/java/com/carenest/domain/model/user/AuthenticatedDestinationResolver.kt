package com.carenest.domain.model.user

import com.carenest.domain.model.home.User
import com.carenest.domain.model.profile.Profile

object AuthenticatedDestinationResolver {
    fun resolve(
        user: User,
        profile: Profile? = null,
        hasEmergencyContact: Boolean = false
    ): AuthenticatedDestination = when {
        !user.hasCompletePersonalInformation -> AuthenticatedDestination.Registration
        profile == null -> AuthenticatedDestination.CompleteProfile
        !profile.hasRequiredHealthInformation -> AuthenticatedDestination.CompleteProfile
        !hasEmergencyContact -> AuthenticatedDestination.CompleteProfile
        else -> AuthenticatedDestination.Home
    }
}

private val Profile.hasRequiredHealthInformation: Boolean
    get() = height != null &&
        weight != null &&
        !bloodType.isNullOrBlank() &&
        !mobilityStatus.isNullOrBlank()
