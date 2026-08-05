package com.carenest.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.domain.usecase.home.GetUserRequestHistoryUseCase
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetUserRequestHistoryUseCase
) : ViewModel(),
    StateHolder<HistoryState> by DefaultStateHolder(HistoryState()),
    EffectPublisher<HistoryEffect> by DefaultEffectPublisher() {

    init {
        loadHistory()
    }

    fun onEvent(event: HistoryIntent) {
        when (event) {
            HistoryIntent.LoadHistory -> loadHistory()
            is HistoryIntent.HistoryItemClicked -> sendEffect(HistoryEffect.NavigateToHistoryDetails(event.historyId))
            HistoryIntent.RetryClicked -> loadHistory()
            HistoryIntent.BackClicked -> sendEffect(HistoryEffect.NavigateBack)
            HistoryIntent.ExploreServicesClicked -> sendEffect(HistoryEffect.NavigateToServices)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            getHistoryUseCase()
                .onSuccess { items ->
                    updateState { copy(historyItems = items, isLoading = false) }
                }
                .onFailure { exception ->
                    updateState { copy(error = exception.message ?: "Failed to load history", isLoading = false) }
                }
        }
    }
}
