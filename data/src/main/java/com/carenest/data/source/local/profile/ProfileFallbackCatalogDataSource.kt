package com.carenest.data.source.local.profile

import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.Medication

interface ProfileFallbackCatalogDataSource {
    fun medicalConditions(): List<MedicalCondition>
    fun allergies(): List<Allergy>
    fun medications(): List<Medication>
}
