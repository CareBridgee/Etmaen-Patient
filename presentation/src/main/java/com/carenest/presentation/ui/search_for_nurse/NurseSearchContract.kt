package com.carenest.presentation.ui.search_for_nurse


data class NearbyNurse(
    val id: String,
    val name: String,
    val title: String, // "RN", "NP"
    val price: Double,
    val rating: Double,
    val reviewCount: Int,
    val area: String,
    val distanceKm: Double,
    val avatarUrl: String? = null
)

data class NurseSearchState(
    val nearbyNurses: List<NearbyNurse> = emptyList(),
    val activeNursesCount: Int = 0,
    val isSearching: Boolean = true,
    val matchedNurseId: String? = null
)

sealed interface NurseSearchIntent {
    data object StartSearching : NurseSearchIntent
    data class AcceptOffer(val nurseId: String) : NurseSearchIntent
    data class DeclineOffer(val nurseId: String) : NurseSearchIntent
    data object CancelSearch : NurseSearchIntent
}

sealed interface NurseSearchEffect {
    data class NavigateToEnRoute(val nurseId: String) : NurseSearchEffect
    data object NavigateBack : NurseSearchEffect
}