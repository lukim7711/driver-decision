package com.driverfinance.ui.screen.capture

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.CapturedOrderEntity
import com.driverfinance.domain.usecase.capture.GetTripOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TripDetailUiState {
    data object Loading : TripDetailUiState
    data class Success(
        val tripId: String,
        val orders: List<CapturedOrderEntity>
    ) : TripDetailUiState
    data class Error(val message: String) : TripDetailUiState
}

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTripOrdersUseCase: GetTripOrdersUseCase
) : ViewModel() {

    private val tripId: String = requireNotNull(savedStateHandle.get<String>("tripId")) {
        "tripId is required for TripDetailScreen"
    }

    private val _uiState = MutableStateFlow<TripDetailUiState>(TripDetailUiState.Loading)
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    init {
        loadOrders()
    }

    private fun loadOrders() {
        viewModelScope.launch {
            try {
                getTripOrdersUseCase(tripId).collectLatest { orders ->
                    _uiState.value = TripDetailUiState.Success(
                        tripId = tripId,
                        orders = orders
                    )
                }
            } catch (e: Exception) {
                _uiState.value = TripDetailUiState.Error(
                    message = e.message ?: "Gagal memuat detail trip"
                )
            }
        }
    }
}
