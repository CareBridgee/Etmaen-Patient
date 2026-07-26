package com.carenest.data.source.local.preferences

import kotlinx.coroutines.flow.Flow


interface CarenestDatastore {

    val isOnBoardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>
    val authTokens: Flow<AuthTokens?>


    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(done: Boolean)
    suspend fun saveAuthTokens(accessToken: String, refreshToken: String)
    suspend fun clearAuthTokens()

    suspend fun clearAll()

}

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)
