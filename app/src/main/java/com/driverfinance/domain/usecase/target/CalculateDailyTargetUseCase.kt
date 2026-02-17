package com.driverfinance.domain.usecase.target

import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.data.repository.AmbitiousModeRepository
import com.driverfinance.data.repository.DailyTargetRepository
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.data.repository.FixedExpenseRepository
import com.driverfinance.data.repository.QuickEntryRepository
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
    private val ambitiousModeRepository: AmbitiousModeRepository,
    private val quickEntryRepository: QuickEntryRepository
) {

    suspend operator fun invoke(): DailyTargetCacheEntity {
        val today = LocalDate.now()
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val yearMonth = String.format("%04d-%02d", today.year, today.monthValue)

        // --- §6.3.1: Kewajiban Bulanan ---
        val totalCicilanNormal = debtRepository.getTotalMonthlyInstallment()
        val totalBiayaTetap = fixedExpenseRepository.getTotalActive()

        // Mode Ambisius check (§6.3.1 + F009 §6.3.7)
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
        // Monthly profit = total income - total expenses for this month
        val monthlyIncome = quickEntryRepository.getTodaySummary(yearMonth + "%", "INCOME")
        val monthlyExpense = quickEntryRepository.getTodaySummary(yearMonth + "%", "EXPENSE")
        val profitAccumulated = monthlyIncome - monthlyExpense

        // Non-expense debt payments (already paid but not counted as expense)
        val nonExpensePayments = debtRepository.getNonExpensePaymentsForMonth(yearMonth)
        val profitAvailable = profitAccumulated - nonExpensePayments

        // --- §6.3.3: Sisa Kewajiban ---
        // Approximate: total obligation minus profit available
        val remainingObligation = (totalObligation - profitAvailable.coerceAtLeast(0)).coerceAtLeast(0)

        // --- §6.3.4: Sisa Hari Kerja ---
        val remainingWorkDays = workScheduleRepository.getRemainingWorkDays()

        // --- §6.3.5: Target Calculation ---
        // Simple division: remaining obligation / remaining work days
        val targetAmount = if (remainingObligation <= 0 || remainingWorkDays <= 0) {
            0
        } else {
            ceil(remainingObligation.toDouble() / remainingWorkDays).toInt()
        }

        // --- Status ---
        val seventyPercent = (totalObligation * 7) / 10
        val status = when {
            totalObligation == 0 -> "NO_OBLIGATION"
            targetAmount <= 0 -> "ACHIEVED"
            profitAvailable >= seventyPercent -> "ON_TRACK"
            else -> "BEHIND"
        }

        val entity = DailyTargetCacheEntity(
            id = UUID.randomUUID().toString(),
            targetDate = today.toString(),
            targetAmount = targetAmount,
            totalObligation = totalObligation,
            obligationPaid = profitAvailable.coerceAtLeast(0),
            remainingObligation = remainingObligation,
            profitAccumulated = profitAccumulated,
            profitAvailable = profitAvailable,
            remainingWorkDays = remainingWorkDays,
            status = status,
            urgentDeadlineName = null,
            urgentDeadlineDate = null,
            urgentDeadlineGap = null,
            calculatedAt = now,
            createdAt = now
        )

        dailyTargetRepository.saveCache(entity)
        return entity
    }
}
