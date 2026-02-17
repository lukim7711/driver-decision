package com.driverfinance.ui.screen.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.obligation.GetWorkScheduleUseCase
import com.driverfinance.domain.usecase.obligation.ManageAmbitiousModeUseCase
import com.driverfinance.domain.usecase.obligation.ToggleWorkScheduleUseCase
import com.driverfinance.domain.usecase.obligation.WorkScheduleData
import com.driverfinance.domain.usecase.obligation.AmbitiousModeDisplay
import com.driverfinance.domain.usecase.target.GetDailyTargetUseCase
import com.driverfinance.domain.usecase.target.GetTargetDetailUseCase
import com.driverfinance.domain.usecase.target.TargetDetailData
import com.driverfinance.data.repository.AmbitiousModeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TargetDetailUiState {
    data object Loading : TargetDetailUiState
    data class Success(
        val detail: TargetDetailData,
        val schedule: WorkScheduleData?,
        val ambitiousMode: AmbitiousModeDisplay?
    ) : TargetDetailUiState
    data class NoObligation(val message: String) : TargetDetailUiState
}

sealed interface TargetDetailEvent {
    data class ShowSnackbar(val message: String) : TargetDetailEvent
}

@HiltViewModel
class TargetDetailViewModel @Inject constructor(
    private val getTargetDetailUseCase: GetTargetDetailUseCase,
    private val getWorkScheduleUseCase: GetWorkScheduleUseCase,
    private val toggleWorkScheduleUseCase: ToggleWorkScheduleUseCase,
    private val manageAmbitiousModeUseCase: ManageAmbitiousModeUseCase,
    private val getDailyTargetUseCase: GetDailyTargetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TargetDetailUiState>(TargetDetailUiState.Loading)
    val uiState: StateFlow<TargetDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<TargetDetailEvent?>(null)
    val events: StateFlow<TargetDetailEvent?> = _events.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            combine(
                getTargetDetailUseCase(),
                getWorkScheduleUseCase(),
                manageAmbitiousModeUseCase.getAmbitiousModeDisplay()
            ) { detail, schedule, ambitious ->
                Triple(detail, schedule, ambitious)
            }.collectLatest { (detail, schedule, ambitious) ->
                if (detail == null) {
                    _uiState.value = TargetDetailUiState.NoObligation(
                        "Belum ada kewajiban bulan ini. Tambahkan hutang atau biaya tetap."
                    )
                } else {
                    _uiState.value = TargetDetailUiState.Success(
                        detail = detail,
                        schedule = schedule,
                        ambitiousMode = ambitious
                    )
                }
            }
        }
    }

    fun toggleDay(date: String) {
        viewModelScope.launch {
            toggleWorkScheduleUseCase(date)
            getDailyTargetUseCase.recalculate()
        }
    }

    fun activateAmbitiousMode(targetMonths: Int) {
        viewModelScope.launch {
            val result = manageAmbitiousModeUseCase.activate(targetMonths)
            when (result) {
                is AmbitiousModeRepository.ActivateResult.Success -> {
                    getDailyTargetUseCase.recalculate()
                    _events.value = TargetDetailEvent.ShowSnackbar("\uD83D\uDE80 Mode Ambisius aktif!")
                }
                is AmbitiousModeRepository.ActivateResult.NoActiveDebts -> {
                    _events.value = TargetDetailEvent.ShowSnackbar("Tidak ada hutang aktif untuk dipercepat")
                }
            }
        }
    }

    fun deactivateAmbitiousMode() {
        viewModelScope.launch {
            manageAmbitiousModeUseCase.deactivate()
            getDailyTargetUseCase.recalculate()
            _events.value = TargetDetailEvent.ShowSnackbar("Mode Ambisius dinonaktifkan")
        }
    }

    fun consumeEvent() {
        _events.value = null
    }
}
