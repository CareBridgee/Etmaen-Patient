package com.carenest.data.mapper.profile

import com.carenest.data.source.remote.dto.profile.AllergyDto
import com.carenest.data.source.remote.dto.profile.ProfileAllergyResponseDto
import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.AllergyType
import com.carenest.domain.model.profile.ProfileAllergy

internal fun AllergyDto.toDomain(): Allergy = Allergy(
    id = id.requiredUuid("allergy id"),
    name = name.requiredText("allergy name"),
    type = type.toAllergyType()
)

internal fun ProfileAllergyResponseDto.toDomain(): ProfileAllergy = ProfileAllergy(
    allergyId = allergyId.requiredUuid("allergy id"),
    allergyName = allergyName.requiredText("allergy name"),
    type = allergyType.toAllergyType()
)

private fun String?.toAllergyType(): AllergyType = when (
    this?.trim()?.uppercase()?.replace('-', '_')?.replace(' ', '_')
) {
    "DRUG", "DRUG_ALLERGY" -> AllergyType.DRUG
    "FOOD", "FOOD_ALLERGY" -> AllergyType.FOOD
    else -> AllergyType.OTHER
}
