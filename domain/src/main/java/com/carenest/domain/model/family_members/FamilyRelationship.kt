package com.carenest.domain.model.family_members

enum class FamilyRelationship(val backendValue: String) {
    Father("Father"),
    Mother("Mother"),
    Brother("Brother"),
    Sister("Sister"),
    Son("Son"),
    Daughter("Daughter"),
    Husband("Husband"),
    Wife("Wife"),
    Spouse("Spouse"),
    Friend("Friend"),
    Relative("Relative"),
    Guardian("Guardian"),
    Parent("Parent"),
    Sibling("Sibling"),
    AdultChild("Adult Child"),
    FriendOrNeighbor("Friend / Neighbor"),
    Other("Other");

    companion object {
        fun fromBackend(value: String?): FamilyRelationship? = entries.firstOrNull {
            it.backendValue.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
        }
    }
}
