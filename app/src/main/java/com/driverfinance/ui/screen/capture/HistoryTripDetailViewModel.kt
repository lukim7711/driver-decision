package com.driverfinance.ui.screen.capture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.domain.usecase.capture.GetHistoryTripDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HistoryTripDetailUiState {
    data object Loading : HistoryTripDetailUiState
    data class Success(
        val historyTripId: String,
        val details: List<HistoryDetailEntity>
    ) : HistoryTripDetailUiState
    data class Error(val message: String) : HistoryTripDetailUiState
}

@HiltViewModel
class HistoryTripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHistoryTripDetailsUseCase: GetHistoryTripDetailsUseCase
) : ViewModel() {

    private val historyTripId: String = requireNotNull(
        savedStateHandle.get<String>("historyTripId")
    ) { "historyTripId is required" }

    private val _uiState = MutableStateFlow<HistoryTripDetailUiState>(HistoryTripDetailUiState.Loading)
    val uiState: StateFlow<HistoryTripDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            try {
                getHistoryTripDetailsUseCase(historyTripId).collectLatest { details ->
                    _uiState.value = HistoryTripDetailUiState.Success(
                        historyTripId = historyTripId,
                        details = details
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HistoryTripDetailUiState.Error(
                    message = e.message ?: "Gagal memuat rincian trip"
                )
            }
        }
    }
}
