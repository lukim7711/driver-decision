package com.driverfinance.ui.screen.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.TripEntity
import com.driverfinance.domain.model.CaptureMode
import com.driverfinance.domain.usecase.capture.GetCaptureModeUseCase
import com.driverfinance.domain.usecase.capture.GetTodayTripsUseCase
import com.driverfinance.domain.usecase.capture.GetTripOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CaptureManagerUiState {
    data object Loading : CaptureManagerUiState
    data class Success(
        val mode: CaptureMode,
        val trips: List<TripWithOrderCount>,
        val todayOrderCount: Int
    ) : CaptureManagerUiState
    data class Error(val message: String) : CaptureManagerUiState
}

data class TripWithOrderCount(
    val trip: TripEntity,
    val orderCount: Int
)

@HiltViewModel
class CaptureManagerViewModel @Inject constructor(
    private val getCaptureModeUseCase: GetCaptureModeUseCase,
    private val getTodayTripsUseCase: GetTodayTripsUseCase,
    private val getTripOrdersUseCase: GetTripOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureManagerUiState>(CaptureManagerUiState.Loading)
    val uiState: StateFlow<CaptureManagerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val mode = getCaptureModeUseCase()
                getTodayTripsUseCase().collectLatest { trips ->
                    var totalOrders = 0
                    val tripsWithCount = trips.map { trip ->
                        val orders = getTripOrdersUseCase(trip.id).firstOrNull().orEmpty()
                        totalOrders += orders.size
                        TripWithOrderCount(trip = trip, orderCount = orders.size)
                    }
                    _uiState.value = CaptureManagerUiState.Success(
                        mode = mode,
                        trips = tripsWithCount,
                        todayOrderCount = totalOrders
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CaptureManagerUiState.Error(
                    message = e.message ?: "Gagal memuat data capture"
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = CaptureManagerUiState.Loading
        loadData()
    }
}
