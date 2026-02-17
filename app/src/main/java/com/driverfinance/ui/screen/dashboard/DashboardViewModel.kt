package com.driverfinance.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.DashboardData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * CONSTITUTION: sealed interface for UI state (Loading, Success, Error).
 * CONSTITUTION: MutableStateFlow, not var.
 */
sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

/**
 * ViewModel for F005 Dashboard.
 *
 * TODO: Inject real repositories when F002, F004, F007 are built:
 *   - CaptureRepository (F002) → trip/order/poin data
 *   - QuickEntryRepository (F004) → expenses/income data
 *   - TargetRepository (F007) → daily target calculation
 *   - ExtractionRepository (F003) → pending review count
 */
@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // Fresh app state — all zeros, no target
                // Real data loading will be connected per feature build
                val data = DashboardData()
                _uiState.value = DashboardUiState.Success(data)
            } catch (e: Exception) {
                Timber.e(e, "Failed to load dashboard data")
                _uiState.value = DashboardUiState.Error("Gagal memuat data. Coba lagi.")
            }
        }
    }
}
