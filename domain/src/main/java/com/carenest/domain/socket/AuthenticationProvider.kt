package com.carenest.domain.socket

interface AuthenticationProvider {
    /**
     * Returns the current JWT access token, if any.
     * Could potentially refresh the token if expired.
     */
    suspend fun getAccessToken(): String?
}
