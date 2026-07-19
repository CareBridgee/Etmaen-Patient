package com.carenest.presentation.ui.profile.allergies

import androidx.lifecycle.ViewModel
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder

class AllergiesViewModel : ViewModel(),
    StateHolder<AllergiesState> by DefaultStateHolder(AllergiesState()),
    EffectPublisher<AllergiesEffect> by DefaultEffectPublisher() {

    fun onEvent(event: AllergiesIntent) {
        when (event) {
            AllergiesIntent.NoKnownAllergiesToggled -> updateState {
                copy(
                    hasNoKnownAllergies = !hasNoKnownAllergies,
                    selectedDrugAllergies = emptySet(),
                    selectedFoodAllergies = emptySet(),
                    otherAllergies = ""
                )
            }
            is AllergiesIntent.DrugAllergyToggled -> updateState {
                copy(
                    hasNoKnownAllergies = false,
                    selectedDrugAllergies = selectedDrugAllergies.toggle(event.allergy)
                )
            }
            is AllergiesIntent.FoodAllergyToggled -> updateState {
                copy(
                    hasNoKnownAllergies = false,
                    selectedFoodAllergies = selectedFoodAllergies.toggle(event.allergy)
                )
            }
            is AllergiesIntent.OtherAllergiesChanged -> updateState {
                copy(hasNoKnownAllergies = false, otherAllergies = event.allergies)
            }
            AllergiesIntent.BackClicked -> sendEffect(AllergiesEffect.NavigateBack)
            AllergiesIntent.ContinueClicked ->
                sendEffect(AllergiesEffect.ContinueToRemainingProfile)
        }
    }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
