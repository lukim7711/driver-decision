package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.WeekSchedule
import com.driverfinance.domain.model.WorkScheduleDay
import com.driverfinance.domain.model.WorkScheduleScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for F009 Work Schedule screen.
 *
 * F009 spec 6.3 #4-5:
 * - Toggle per day, default is_working = 1
 * - Display 2 weeks (this + next)
 * - Reset monthly: unset dates default to working
 * - Remaining work days = count from tomorrow to end of month WHERE is_working = 1
 *
 * TODO: Inject WorkScheduleRepository.
 */
@HiltViewModel
class WorkScheduleViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(WorkScheduleScreenState())
    val screenState: StateFlow<WorkScheduleScreenState> = _screenState.asStateFlow()

    private val dateFmt = DateTimeFormatter.ofPattern("d MMM", Locale("id", "ID"))
    private val dateFmtFull = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))

    // In-memory schedule overrides (TODO: replace with Room queries)
    private val scheduleOverrides = mutableMapOf<LocalDate, Boolean>()

    init {
        loadSchedule()
    }

    private fun loadSchedule() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(200)

                val today = LocalDate.now()
                val thisWeek = buildWeekSchedule(today, "Minggu Ini")
                val nextWeekStart = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                val nextWeek = buildWeekSchedule(nextWeekStart, "Minggu Depan")

                _screenState.update {
                    it.copy(
                        thisWeek = thisWeek,
                        nextWeek = nextWeek,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load work schedule")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat jadwal")
                }
            }
        }
    }

    /**
     * Toggle a day between working and off.
     * F009 spec 6.3 #4: INSERT OR REPLACE work_schedules.
     */
    fun toggleDay(date: LocalDate) {
        val currentlyWorking = scheduleOverrides[date] ?: true // default = working
        scheduleOverrides[date] = !currentlyWorking

        // TODO: workScheduleRepository.upsert(date, !currentlyWorking)
        Timber.d("Toggled $date: working=${!currentlyWorking}")

        loadSchedule() // Refresh UI
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    // ---- Helpers ----

    private fun buildWeekSchedule(startOfWeek: LocalDate, label: String): WeekSchedule {
        // Find Sunday of the week containing startOfWeek
        val sunday = if (startOfWeek.dayOfWeek == DayOfWeek.SUNDAY) {
            startOfWeek
        } else {
            startOfWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        }

        val days = (0..6).map { offset ->
            val date = sunday.plusDays(offset.toLong())
            val isWorking = scheduleOverrides[date] ?: true // Default: working
            WorkScheduleDay(date = date, isWorking = isWorking)
        }

        val saturday = sunday.plusDays(6)
        val dateRange = "${sunday.format(dateFmt)} - ${saturday.format(dateFmtFull)}"

        return WeekSchedule(
            label = label,
            dateRange = dateRange,
            days = days,
            workingDays = days.count { it.isWorking },
            offDays = days.count { !it.isWorking }
        )
    }
}
