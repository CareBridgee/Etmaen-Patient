package com.carenest.data.mapper.profile

import com.carenest.domain.model.profile.Gender

fun String?.toGender(): Gender {
    val upper = this?.uppercase()
    return Gender.entries.firstOrNull { it.name == upper } ?: Gender.MALE
}

fun Gender.toApiValue(): String = name
