package com.carenest.domain.model.profile

enum class Gender(val apiValue: String) {
    MALE("MALE"),
    FEMALE("FEMALE");

    companion object {
        fun fromApi(value: String?): Gender {
            val upper = value?.uppercase()
            return entries.firstOrNull { it.apiValue == upper || it.name == upper } ?: MALE
        }
    }
}
