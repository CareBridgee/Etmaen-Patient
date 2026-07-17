package com.carenest.data.source.local.preferences

import kotlinx.coroutines.flow.Flow


interface CarenestDatastore {

    val isOnBoardingDone: Flow<Boolean>
    val isLoggedIn: Flow<Boolean>


    suspend fun setOnboardingDone(done: Boolean)
    suspend fun setLoggedIn(done: Boolean)

    suspend fun clearAll()

}