package io.devexpert.splitbill.ui.screen.home

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.devexpert.splitbill.R
import io.devexpert.splitbill.domain.usecase.GetScansReminingUseCase
import io.devexpert.splitbill.domain.usecase.InitializeOrResetScanCounterUseCase
import io.devexpert.splitbill.domain.usecase.ProcessTicketUseCase
import io.devexpert.splitbill.utils.ImageConverter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val initializeOrResetScanCounterUseCase: InitializeOrResetScanCounterUseCase,
    private val getScansReminingUseCase: GetScansReminingUseCase,
    private val processTicketUseCase: ProcessTicketUseCase,
    private val context: Context
): ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val homeUiState = combine(_uiState, getScansReminingUseCase.invoke()) { uiState, scansRemaining ->
        uiState.copy(scansLeft = scansRemaining)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    init {
        viewModelScope.launch {
            initializeOrResetScanCounterUseCase.invoke()
        }
    }

    fun processTicket(
        bitmap: Bitmap,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            setIsProcessing(true)
            setErrorMessage()
            val imageByteArray = ImageConverter.toResizedByteArray(bitmap)
            try {
                processTicketUseCase.invoke(imageByteArray)
                setIsProcessing(false)
                onSuccess()
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                setErrorMessage(context.getString(R.string.error_processing_ticket, e))
                setIsProcessing(false)
            }

        }
    }

    private fun setIsProcessing(processing: Boolean) {
        _uiState.update { it.copy(isProcessing = processing) }
    }

    private fun setErrorMessage(message: String = "") {
        _uiState.update { it.copy(errorMessage = message) }
    }

}