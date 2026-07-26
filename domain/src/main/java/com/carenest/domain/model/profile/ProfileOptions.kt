package com.carenest.domain.model.profile

enum class MobilityStatus {
    Independent,
    NeedsAssistance,
    UsesWalkingAid,
    WheelchairUser,
    Bedridden
}

enum class EmergencyRelationship(val backendValue: String) {
    Spouse("Spouse"),
    Parent("Parent"),
    Sibling("Sibling"),
    AdultChild("Adult Child"),
    FriendOrNeighbor("Friend / Neighbor"),
    Other("Other");

    companion object {
        fun fromBackend(value: String?): EmergencyRelationship? = entries.firstOrNull {
            it.backendValue.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
        }
    }
}

data class MobilityInput(
    val status: MobilityStatus,
    val notes: String
)
