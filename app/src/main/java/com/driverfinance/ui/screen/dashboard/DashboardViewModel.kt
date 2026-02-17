package com.driverfinance.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.dashboard.DashboardData
import com.driverfinance.domain.usecase.dashboard.GetDashboardDataUseCase
import com.driverfinance.domain.usecase.dashboard.GetPendingReviewCountUseCase
import com.driverfinance.domain.usecase.dashboard.GetTripSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(
        val data: DashboardData,
        val orderCount: Int,
        val totalPoints: Int,
        val pendingReviewCount: Int,
        val dailyTarget: Int?,
        val targetProgress: Float,
        val targetRemaining: Int,
        val isTargetAchieved: Boolean
    ) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getTripSummaryUseCase: GetTripSummaryUseCase,
    private val getPendingReviewCountUseCase: GetPendingReviewCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            try {
                getDashboardDataUseCase().collectLatest { data ->
                    val tripSummary = getTripSummaryUseCase()
                    val pendingCount = getPendingReviewCountUseCase()

                    val dailyTarget: Int? = null
                    val profit = data.profit
                    val targetProgress = if (dailyTarget != null && dailyTarget > 0) {
                        (profit.toFloat() / dailyTarget).coerceIn(0f, 1f)
                    } else 0f
                    val targetRemaining = if (dailyTarget != null) {
                        (dailyTarget - profit).coerceAtLeast(0)
                    } else 0
                    val isTargetAchieved = dailyTarget != null && profit >= dailyTarget

                    _uiState.value = DashboardUiState.Success(
                        data = data,
                        orderCount = tripSummary.orderCount,
                        totalPoints = tripSummary.totalPoints,
                        pendingReviewCount = pendingCount,
                        dailyTarget = dailyTarget,
                        targetProgress = targetProgress,
                        targetRemaining = targetRemaining,
                        isTargetAchieved = isTargetAchieved
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(
                    message = e.message ?: "Gagal memuat dashboard"
                )
            }
        }
    }

    fun refresh() {
        _uiState.value = DashboardUiState.Loading
        loadDashboard()
    }
}
