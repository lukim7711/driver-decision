package com.driverfinance.ui.screen.target

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.AmbitiousModeScreenState
import com.driverfinance.domain.model.DeadlineObligation
import com.driverfinance.domain.model.DeadlineWarning
import com.driverfinance.domain.model.HeroTargetState
import com.driverfinance.domain.model.ObligationItem
import com.driverfinance.domain.model.ObligationSource
import com.driverfinance.domain.model.TargetDetailScreenState
import com.driverfinance.domain.model.TargetStatus
import com.driverfinance.domain.model.WeekSchedule
import com.driverfinance.domain.model.WorkScheduleDay
import com.driverfinance.domain.usecase.CalculateDailyTargetUseCase
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
 * ViewModel for F007 Target Detail screen.
 *
 * Orchestrates:
 * - Obligation breakdown (F006 debts + F009 fixed expenses)
 * - Profit calculation (F002 earnings + F004 entries)
 * - Deadline-aware target algorithm (CalculateDailyTargetUseCase)
 * - F009 embedded data (work schedule + ambitious mode)
 * - Cache to daily_target_cache
 *
 * TODO: Inject repositories for real data:
 *   DebtRepository, FixedExpenseRepository, WorkScheduleRepository,
 *   AmbitiousModeRepository, QuickEntryRepository, HistoryTripRepository,
 *   DailyTargetCacheRepository
 */
@HiltViewModel
class TargetDetailViewModel @Inject constructor() : ViewModel() {

    private val calculateTarget = CalculateDailyTargetUseCase()

    private val _screenState = MutableStateFlow(TargetDetailScreenState())
    val screenState: StateFlow<TargetDetailScreenState> = _screenState.asStateFlow()

    private val dateFmt = DateTimeFormatter.ofPattern("d MMM", Locale("id", "ID"))
    private val dateFmtFull = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))

    init {
        loadTargetDetail()
    }

    private fun loadTargetDetail() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(300)

                val today = LocalDate.now()

                // --- 1. Load obligations ---
                // TODO: debtRepository.getActiveDebts() + fixedExpenseRepository.getActive()
                val debtItems = getPlaceholderDebtItems()
                val fixedItems = getPlaceholderFixedItems()
                val subtotalDebt = debtItems.sumOf { it.amount }
                val subtotalFixed = fixedItems.sumOf { it.amount }
                val totalObligation = subtotalDebt + subtotalFixed

                // --- 2. Load profit ---
                // TODO: Sum from history_trips + quick_entries for current month
                val profitAccumulated = 1_180_000L
                val debtPaymentsMade = 750_000L
                val profitAvailable = profitAccumulated - debtPaymentsMade

                // --- 3. Calculate remaining obligation ---
                val remainingObligation = totalObligation - debtPaymentsMade
                val amountToCollect = (remainingObligation - profitAvailable).coerceAtLeast(0)

                // --- 4. Work days ---
                // TODO: workScheduleRepository.getRemainingWorkDays()
                val remainingWorkDays = 8

                // --- 5. Run deadline-aware algorithm ---
                val deadlineObligations = debtItems
                    .filter { it.dueDateDay != null }
                    .map {
                        DeadlineObligation(
                            name = it.name,
                            emoji = it.emoji,
                            amount = it.amount,
                            dueDateDay = it.dueDateDay!!,
                            isPaid = it.isPaid
                        )
                    }

                // Build work days list from tomorrow to end of month
                val endOfMonth = today.withDayOfMonth(today.lengthOfMonth())
                val workDaysFromTomorrow = generateWorkDays(today.plusDays(1), endOfMonth)

                val calcResult = calculateTarget.execute(
                    CalculateDailyTargetUseCase.Input(
                        obligations = deadlineObligations,
                        profitAvailable = profitAvailable,
                        workDaysFromTomorrow = workDaysFromTomorrow,
                        today = today
                    )
                )

                val targetPerDay = calcResult.targetPerDay

                // --- 6. Build deadline warnings ---
                val warnings = buildDeadlineWarnings(deadlineObligations, profitAvailable, today)

                // --- 7. Build hero state ---
                val profitToday = 102_000L // TODO: calculate from today's data
                val hero = buildHeroState(
                    targetPerDay = targetPerDay,
                    profitToday = profitToday,
                    totalObligation = totalObligation,
                    profitAvailable = profitAvailable,
                    remainingObligation = remainingObligation
                )

                // --- 8. Embedded F009 data ---
                val thisWeek = buildThisWeekSchedule(today)

                // --- 9. Check no work days edge case ---
                val noWorkDaysWarning = remainingWorkDays == 0 && amountToCollect > 0

                _screenState.update {
                    it.copy(
                        isLoading = false,
                        hero = hero,
                        debtInstallments = debtItems,
                        fixedExpenses = fixedItems,
                        subtotalDebt = subtotalDebt,
                        subtotalFixedExpense = subtotalFixed,
                        totalObligation = totalObligation,
                        profitAccumulated = profitAccumulated,
                        debtPaymentsMade = debtPaymentsMade,
                        profitAvailable = profitAvailable,
                        remainingObligation = remainingObligation,
                        amountToCollect = amountToCollect,
                        remainingWorkDays = remainingWorkDays,
                        targetPerDay = targetPerDay,
                        deadlineWarnings = warnings,
                        noWorkDaysWarning = noWorkDaysWarning,
                        workScheduleThisWeek = thisWeek,
                        ambitiousModeState = AmbitiousModeScreenState(
                            isActive = false,
                            hasActiveDebts = true,
                            isLoading = false
                        )
                    )
                }

                // TODO: Cache result to daily_target_cache

            } catch (e: Exception) {
                Timber.e(e, "Failed to load target detail")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal menghitung target")
                }
            }
        }
    }

    // ---- Ambitious Mode delegation (embedded F009) ----

    fun toggleAmbitiousMode() {
        // TODO: Delegate to AmbitiousModeRepository
        _screenState.update { state ->
            val current = state.ambitiousModeState
            if (!current.isActive && !current.canActivate) {
                state.copy(
                    ambitiousModeState = current.copy(
                        errorMessage = "Tidak ada hutang aktif untuk dipercepat"
                    )
                )
            } else {
                state.copy(
                    ambitiousModeState = current.copy(isActive = !current.isActive)
                )
            }
        }
        // Recalculate target after toggle
        loadTargetDetail()
    }

    fun selectAmbitiousPresetMonths(months: Int) {
        _screenState.update { state ->
            state.copy(
                ambitiousModeState = state.ambitiousModeState.copy(
                    targetMonths = months, customMonths = ""
                )
            )
        }
    }

    fun updateAmbitiousCustomMonths(input: String) {
        val filtered = input.filter { it.isDigit() }
        _screenState.update { state ->
            val months = filtered.toIntOrNull()
            state.copy(
                ambitiousModeState = state.ambitiousModeState.copy(
                    customMonths = filtered,
                    targetMonths = if (months != null && months in 1..120) months
                    else state.ambitiousModeState.targetMonths
                )
            )
        }
    }

    // ---- Work Schedule delegation (embedded F009) ----

    fun toggleWorkDay(date: LocalDate) {
        // TODO: Delegate to WorkScheduleRepository
        Timber.d("Toggle work day: $date")
        loadTargetDetail() // Recalculate after schedule change
    }

    fun refresh() {
        loadTargetDetail()
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    // ---- Private helpers ----

    private fun buildHeroState(
        targetPerDay: Long,
        profitToday: Long,
        totalObligation: Long,
        profitAvailable: Long,
        remainingObligation: Long
    ): HeroTargetState {
        val remaining = (targetPerDay - profitToday).coerceAtLeast(0)
        val surplus = (profitToday - targetPerDay).coerceAtLeast(0)
        val progress = if (targetPerDay > 0) {
            (profitToday.toFloat() / targetPerDay * 100).coerceIn(0f, 200f)
        } else 0f

        val status = when {
            totalObligation == 0L -> TargetStatus.NO_OBLIGATION
            profitAvailable >= remainingObligation -> TargetStatus.ALL_COVERED
            profitToday >= targetPerDay -> TargetStatus.ACHIEVED
            else -> TargetStatus.ON_TRACK
        }

        return HeroTargetState(
            status = status,
            targetAmount = targetPerDay,
            profitToday = profitToday,
            remainingAmount = remaining,
            surplusAmount = surplus,
            progressPercent = progress
        )
    }

    private fun buildDeadlineWarnings(
        obligations: List<DeadlineObligation>,
        profitAvailable: Long,
        today: LocalDate
    ): List<DeadlineWarning> {
        return obligations
            .filter { !it.isPaid }
            .mapNotNull { obligation ->
                val dueDate = today.withDayOfMonth(
                    obligation.dueDateDay.coerceAtMost(today.lengthOfMonth())
                )
                val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, dueDate).toInt()
                val isOverdue = daysUntil < 0

                // Only show warning for <=3 days away or overdue
                if (daysUntil <= 3) {
                    val amountShort = (obligation.amount - profitAvailable).coerceAtLeast(0)
                    DeadlineWarning(
                        name = obligation.name,
                        emoji = obligation.emoji,
                        dueDate = dueDate,
                        daysUntilDue = daysUntil,
                        amountNeeded = obligation.amount,
                        amountAvailable = profitAvailable,
                        amountShort = amountShort,
                        isOverdue = isOverdue
                    )
                } else null
            }
            .sortedBy { it.daysUntilDue }
    }

    private fun generateWorkDays(start: LocalDate, end: LocalDate): List<LocalDate> {
        // TODO: Use workScheduleRepository to check actual schedule
        // Default: all days are work days
        val days = mutableListOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) {
            days.add(current) // Default all working
            current = current.plusDays(1)
        }
        return days
    }

    private fun buildThisWeekSchedule(today: LocalDate): WeekSchedule {
        val sunday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val days = (0..6).map { offset ->
            val date = sunday.plusDays(offset.toLong())
            WorkScheduleDay(date = date, isWorking = true) // TODO: from repository
        }
        val saturday = sunday.plusDays(6)
        return WeekSchedule(
            label = "Minggu Ini",
            dateRange = "${sunday.format(dateFmt)} - ${saturday.format(dateFmtFull)}",
            days = days,
            workingDays = days.count { it.isWorking },
            offDays = days.count { !it.isWorking }
        )
    }

    // ---- Placeholder data ----

    private fun getPlaceholderDebtItems() = listOf(
        ObligationItem(
            emoji = "\uD83C\uDFCD\uFE0F", name = "Motor (tgl 15)",
            amount = 650_000, dueDateDay = 15, isPaid = false,
            source = ObligationSource.DEBT_INSTALLMENT
        ),
        ObligationItem(
            emoji = "\uD83D\uDCF1", name = "Pinjol (tgl 10)",
            amount = 750_000, dueDateDay = 10, isPaid = true,
            source = ObligationSource.DEBT_INSTALLMENT
        )
    )

    private fun getPlaceholderFixedItems() = listOf(
        ObligationItem(
            emoji = "\uD83D\uDCF1", name = "Pulsa",
            amount = 50_000, dueDateDay = null, isPaid = false,
            source = ObligationSource.FIXED_EXPENSE
        ),
        ObligationItem(
            emoji = "\u26A1", name = "Listrik",
            amount = 200_000, dueDateDay = null, isPaid = false,
            source = ObligationSource.FIXED_EXPENSE
        )
    )
}
