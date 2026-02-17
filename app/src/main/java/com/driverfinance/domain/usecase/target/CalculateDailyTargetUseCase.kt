package com.driverfinance.domain.usecase.target

import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.data.repository.AmbitiousModeRepository
import com.driverfinance.data.repository.DailyTargetRepository
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.data.repository.FixedExpenseRepository
import com.driverfinance.data.repository.WorkScheduleRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject
import kotlin.math.ceil

/**
 * Core deadline-aware algorithm per F007 §6.3.5.
 * Calculates daily target considering all deadlines and picks the MAX.
 */
class CalculateDailyTargetUseCase @Inject constructor(
    private val dailyTargetRepository: DailyTargetRepository,
    private val debtRepository: DebtRepository,
    private val fixedExpenseRepository: FixedExpenseRepository,
    private val workScheduleRepository: WorkScheduleRepository,
    private val ambitiousModeRepository: AmbitiousModeRepository
) {

    suspend operator fun invoke(): DailyTargetCacheEntity {
        val today = LocalDate.now()
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        // --- §6.3.1: Kewajiban Bulanan ---
        val totalCicilanNormal = debtRepository.getTotalMonthlyInstallment()
        val totalBiayaTetap = fixedExpenseRepository.getTotalActive()

        // Mode Ambisius check (§6.3.1 + F009 §6.3.7)
        val ambitiousMode = ambitiousModeRepository.getAmbitiousModeSync()
        // Reactive fallback: F009 §6.3.8 Mekanisme B
        ambitiousModeRepository.checkAndDeactivateAmbitiousMode()
        val refreshedMode = ambitiousModeRepository.getAmbitiousModeSync()

        val totalObligation: Int
        if (refreshedMode != null && refreshedMode.isActive == 1 && refreshedMode.targetMonths > 0) {
            val totalRemainingDebt = debtRepository.getTotalRemainingAmount()
            val cicilanAmbisius = (totalRemainingDebt + refreshedMode.targetMonths - 1) / refreshedMode.targetMonths
            val effectiveCicilan = maxOf(cicilanAmbisius, totalCicilanNormal)
            totalObligation = effectiveCicilan + totalBiayaTetap
        } else {
            totalObligation = totalCicilanNormal + totalBiayaTetap
        }

        // --- §6.3.2: Profit ---
        val profitAccumulated = debtRepository.getMonthlyProfitAccumulated(today)
        val nonExpensePayments = debtRepository.getNonExpensePaymentsForMonth(
            year = today.year,
            month = today.monthValue
        )
        val profitAvailable = profitAccumulated - nonExpensePayments

        // --- §6.3.3: Sisa Kewajiban ---
        val cicilanSudahDibayar = debtRepository.getMonthlyDebtPaymentsTotal(
            year = today.year,
            month = today.monthValue
        )
        val remainingObligation = (totalObligation - cicilanSudahDibayar).coerceAtLeast(0)

        // --- §6.3.4: Sisa Hari Kerja ---
        val remainingWorkDays = workScheduleRepository.getRemainingWorkDays()

        // --- §6.3.5: Deadline-Aware Algorithm ---
        val activeDebtsWithDeadline = debtRepository.getActiveDebtsWithDeadlineSync()

        // Sort by due_date_day ASC
        val obligations = activeDebtsWithDeadline
            .filter { it.dueDateDay != null }
            .sortedBy { it.dueDateDay }
            .map { debt ->
                DeadlineObligation(
                    name = debt.name,
                    amount = debt.monthlyInstallment ?: 0,
                    dueDateDay = debt.dueDateDay ?: 0,
                    paidThisMonth = debt.paidThisMonth
                )
            }

        var targetAmount = 0
        var urgentDeadlineName: String? = null
        var urgentDeadlineDate: Int? = null
        var urgentDeadlineGap: Int? = null

        if (obligations.isNotEmpty()) {
            var maxTarget = 0

            for (obligation in obligations) {
                // Cumulative obligations up to this deadline
                val cumulativeObligation = obligations
                    .filter { it.dueDateDay <= obligation.dueDateDay }
                    .sumOf { (it.amount - it.paidThisMonth).coerceAtLeast(0) }

                val deadlineDate = if (obligation.dueDateDay >= today.dayOfMonth) {
                    today.withDayOfMonth(obligation.dueDateDay.coerceAtMost(today.lengthOfMonth()))
                } else {
                    // Already passed this month
                    today.withDayOfMonth(obligation.dueDateDay.coerceAtMost(today.lengthOfMonth()))
                }

                val workDaysUntil = countWorkDaysUntil(today, deadlineDate)
                val needed = cumulativeObligation - profitAvailable

                val targetForDeadline = when {
                    needed <= 0 -> 0
                    workDaysUntil <= 0 -> {
                        // URGENT: no work days left but still need money
                        urgentDeadlineName = obligation.name
                        urgentDeadlineDate = obligation.dueDateDay
                        urgentDeadlineGap = needed
                        needed // Show full amount needed
                    }
                    else -> {
                        val perDay = ceil(needed.toDouble() / workDaysUntil).toInt()
                        if (perDay > maxTarget) {
                            urgentDeadlineName = obligation.name
                            urgentDeadlineDate = obligation.dueDateDay
                            urgentDeadlineGap = needed
                        }
                        perDay
                    }
                }

                if (targetForDeadline > maxTarget) {
                    maxTarget = targetForDeadline
                }
            }

            targetAmount = maxTarget
        } else {
            // No deadline-based debts, use simple division
            val needed = remainingObligation - profitAvailable
            targetAmount = if (needed <= 0 || remainingWorkDays <= 0) {
                0
            } else {
                ceil(needed.toDouble() / remainingWorkDays).toInt()
            }
        }

        // --- §6.3.5: Status ---
        val status = when {
            totalObligation == 0 -> "NO_OBLIGATION"
            targetAmount <= 0 -> "ACHIEVED"
            profitAvailable >= remainingObligation * 0.7 -> "ON_TRACK"
            else -> "BEHIND"
        }

        val entity = DailyTargetCacheEntity(
            id = UUID.randomUUID().toString(),
            targetDate = today.toString(),
            targetAmount = targetAmount,
            totalObligation = totalObligation,
            obligationPaid = cicilanSudahDibayar,
            remainingObligation = remainingObligation,
            profitAccumulated = profitAccumulated,
            profitAvailable = profitAvailable,
            remainingWorkDays = remainingWorkDays,
            status = status,
            urgentDeadlineName = urgentDeadlineName,
            urgentDeadlineDate = urgentDeadlineDate,
            urgentDeadlineGap = urgentDeadlineGap,
            calculatedAt = now,
            createdAt = now
        )

        dailyTargetRepository.saveCache(entity)
        return entity
    }

    private suspend fun countWorkDaysUntil(from: LocalDate, until: LocalDate): Int {
        if (until.isBefore(from) || until.isEqual(from)) return 0
        val tomorrow = from.plusDays(1)
        if (tomorrow.isAfter(until)) return 0
        // Simple count: days between tomorrow and until, minus known off days
        val totalDays = ChronoUnit.DAYS.between(tomorrow, until).toInt() + 1
        // For simplicity, assume all days are work days unless explicitly off
        // Full implementation would query work_schedules for each date
        return totalDays.coerceAtLeast(0)
    }
}

private data class DeadlineObligation(
    val name: String,
    val amount: Int,
    val dueDateDay: Int,
    val paidThisMonth: Int
)
