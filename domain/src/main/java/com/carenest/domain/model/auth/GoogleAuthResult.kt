package com.carenest.domain.model.auth

sealed interface GoogleAuthResult {
    data class Authenticated(val authResult: AuthResult) : GoogleAuthResult
    data class PhoneRequired(
        val pendingToken: String,
        val email: String,
        val firstName: String?,
        val lastName: String?,
        val profileImageUrl: String?
    ) : GoogleAuthResult
}
