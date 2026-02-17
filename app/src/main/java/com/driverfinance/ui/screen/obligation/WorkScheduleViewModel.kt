package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.obligation.GetWorkScheduleUseCase
import com.driverfinance.domain.usecase.obligation.ToggleWorkScheduleUseCase
import com.driverfinance.domain.usecase.obligation.WorkScheduleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WorkScheduleUiState {
    data object Loading : WorkScheduleUiState
    data class Success(val data: WorkScheduleData) : WorkScheduleUiState
}

@HiltViewModel
class WorkScheduleViewModel @Inject constructor(
    private val getWorkScheduleUseCase: GetWorkScheduleUseCase,
    private val toggleWorkScheduleUseCase: ToggleWorkScheduleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WorkScheduleUiState>(WorkScheduleUiState.Loading)
    val uiState: StateFlow<WorkScheduleUiState> = _uiState.asStateFlow()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            getWorkScheduleUseCase().collectLatest { data ->
                _uiState.value = WorkScheduleUiState.Success(data)
            }
        }
    }

    fun toggleDay(date: String) {
        viewModelScope.launch {
            toggleWorkScheduleUseCase(date)
        }
    }
}
