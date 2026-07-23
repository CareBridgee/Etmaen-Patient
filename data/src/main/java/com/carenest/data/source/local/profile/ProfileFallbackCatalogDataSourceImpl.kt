package com.carenest.data.source.local.profile

import com.carenest.domain.model.profile.Allergy
import com.carenest.domain.model.profile.AllergyType
import com.carenest.domain.model.profile.CatalogSource
import com.carenest.domain.model.profile.MedicalCondition
import com.carenest.domain.model.profile.Medication
import com.carenest.domain.model.profile.SyncState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileFallbackCatalogDataSourceImpl @Inject constructor() : ProfileFallbackCatalogDataSource {
    override fun medicalConditions(): List<MedicalCondition> = listOf(
        "Diabetes", "Hypertension", "Heart Disease", "Asthma", "COPD", "Epilepsy", "Liver Disease"
    ).map { name ->
        MedicalCondition(name.normalizedCatalogKey(), null, name, null, CatalogSource.FALLBACK, SyncState.LOCAL_ONLY)
    }

    override fun allergies(): List<Allergy> {
        val drug = listOf("Penicillin", "Sulfa Drugs", "Aspirin", "Ibuprofen", "Lidocaine", "Codeine", "Latex")
            .map { it to AllergyType.DRUG }
        val food = listOf("Peanuts", "Shellfish", "Dairy", "Eggs", "Soy", "Tree Nuts", "Wheat/Gluten")
            .map { it to AllergyType.FOOD }
        return (drug + food).map { (name, type) ->
            Allergy(name.normalizedCatalogKey(), null, name, type, CatalogSource.FALLBACK, SyncState.LOCAL_ONLY)
        }
    }

    override fun medications(): List<Medication> = emptyList()
}

internal fun String.normalizedCatalogKey(): String =
    trim().lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')
