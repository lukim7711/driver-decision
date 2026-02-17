package com.driverfinance.ui.screen.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.HistoryTripEntity
import com.driverfinance.data.local.entity.TripEntity
import com.driverfinance.domain.model.CaptureMode
import com.driverfinance.domain.usecase.capture.GetCaptureModeUseCase
import com.driverfinance.domain.usecase.capture.GetHistoryCaptureModeUseCase
import com.driverfinance.domain.usecase.capture.GetTodayHistoryTripsUseCase
import com.driverfinance.domain.usecase.capture.GetTodayTripsUseCase
import com.driverfinance.domain.usecase.capture.GetTripOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CaptureManagerUiState {
    data object Loading : CaptureManagerUiState
    data class Success(
        val orderCaptureMode: CaptureMode,
        val historyCaptureMode: CaptureMode,
        val trips: List<TripWithOrderCount>,
        val todayOrderCount: Int,
        val historyTrips: List<HistoryTripWithDetailCount>,
        val historyTripsWithDetails: Int
    ) : CaptureManagerUiState
    data class Error(val message: String) : CaptureManagerUiState
}

data class TripWithOrderCount(
    val trip: TripEntity,
    val orderCount: Int
)

data class HistoryTripWithDetailCount(
    val historyTrip: HistoryTripEntity,
    val detailCount: Int
)

@HiltViewModel
class CaptureManagerViewModel @Inject constructor(
    private val getCaptureModeUseCase: GetCaptureModeUseCase,
    private val getHistoryCaptureModeUseCase: GetHistoryCaptureModeUseCase,
    private val getTodayTripsUseCase: GetTodayTripsUseCase,
    private val getTodayHistoryTripsUseCase: GetTodayHistoryTripsUseCase,
    private val getTripOrdersUseCase: GetTripOrdersUseCase,
    private val captureRepository: com.driverfinance.data.repository.CaptureRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CaptureManagerUiState>(CaptureManagerUiState.Loading)
    val uiState: StateFlow<CaptureManagerUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val orderMode = getCaptureModeUseCase()
                val historyMode = getHistoryCaptureModeUseCase()

                combine(
                    getTodayTripsUseCase(),
                    getTodayHistoryTripsUseCase()
                ) { trips, historyTrips ->
                    Pair(trips, historyTrips)
                }.collectLatest { (trips, historyTrips) ->
                    var totalOrders = 0
                    val tripsWithCount = trips.map { trip ->
                        val orders = getTripOrdersUseCase(trip.id).firstOrNull().orEmpty()
                        totalOrders += orders.size
                        TripWithOrderCount(trip = trip, orderCount = orders.size)
                    }

                    var tripsWithDetails = 0
                    val historyTripsWithCount = historyTrips.map { ht ->
                        val count = captureRepository.getDetailsCountByHistoryTripId(ht.id)
                        if (count > 0) tripsWithDetails++
                        HistoryTripWithDetailCount(historyTrip = ht, detailCount = count)
                    }

                    _uiState.value = CaptureManagerUiState.Success(
                        orderCaptureMode = orderMode,
                        historyCaptureMode = historyMode,
                        trips = tripsWithCount,
                        todayOrderCount = totalOrders,
                        historyTrips = historyTripsWithCount,
                        historyTripsWithDetails = tripsWithDetails
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
