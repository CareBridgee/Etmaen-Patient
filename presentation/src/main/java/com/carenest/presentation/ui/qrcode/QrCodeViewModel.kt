package com.carenest.presentation.ui.qrcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carenest.presentation.core.mvi.DefaultEffectPublisher
import com.carenest.presentation.core.mvi.DefaultStateHolder
import com.carenest.presentation.core.mvi.EffectPublisher
import com.carenest.presentation.core.mvi.StateHolder
import com.carenest.domain.usecase.tracking.GetVisitVerificationCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrCodeViewModel @Inject constructor(
    private val getVisitVerificationCodeUseCase: GetVisitVerificationCodeUseCase
) : ViewModel(),
    StateHolder<QrCodeState> by DefaultStateHolder(QrCodeState()),
    EffectPublisher<QrCodeEffect> by DefaultEffectPublisher() {

    fun onEvent(event: QrCodeIntent) {
        when (event) {
            is QrCodeIntent.LoadQrCode -> loadQrCode(event.requestId)
            QrCodeIntent.BackClicked -> sendEffect(QrCodeEffect.NavigateBack)
        }
    }

    private fun loadQrCode(requestId: String) {
        viewModelScope.launch {
            updateState { copy(isLoading = true, error = null) }
            
            getVisitVerificationCodeUseCase(requestId)
                .onSuccess { qrContent ->
                    updateState {
                        copy(
                            qrData = qrContent,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    updateState {
                        copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load QR code"
                        )
                    }
                }
        }
    }
}
