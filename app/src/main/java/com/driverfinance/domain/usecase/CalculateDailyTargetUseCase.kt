package com.driverfinance.domain.usecase

import com.driverfinance.domain.model.DeadlineObligation
import com.driverfinance.domain.model.DeadlineTargetResult
import timber.log.Timber
import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.max

/**
 * F007 Deadline-Aware Target Calculation Algorithm.
 *
 * F007 spec 6.3 #5:
 * a. Sort all obligations by due_date_day ASC
 * b. For each deadline d_i:
 *    - cumulative_obligation_i = SUM all obligations due <= d_i AND unpaid
 *    - work_days_until_i = COUNT work days from tomorrow to d_i (inclusive)
 *    - needed_i = cumulative_obligation_i - profit_available
 *    - If needed_i <= 0 → target_i = 0
 *    - If work_days_until_i = 0 AND needed_i > 0 → URGENT
 *    - Else → target_i = CEIL(needed_i / work_days_until_i)
 * c. Target Harian = MAX(target_i for all i)
 * d. If Target Harian <= 0 → status = TERCUKUPI
 */
class CalculateDailyTargetUseCase {

    data class Input(
        val obligations: List<DeadlineObligation>,
        val profitAvailable: Long,
        val workDaysFromTomorrow: List<LocalDate>,
        val today: LocalDate = LocalDate.now()
    )

    data class Output(
        val targetPerDay: Long,
        val results: List<DeadlineTargetResult>,
        val isCovered: Boolean,
        val hasUrgent: Boolean
    )

    fun execute(input: Input): Output {
        val unpaid = input.obligations
            .filter { !it.isPaid }
            .sortedBy { it.dueDateDay }

        if (unpaid.isEmpty()) {
            Timber.d("No unpaid obligations, target = 0")
            return Output(
                targetPerDay = 0,
                results = emptyList(),
                isCovered = true,
                hasUrgent = false
            )
        }

        val results = mutableListOf<DeadlineTargetResult>()
        var maxTarget = 0L
        var hasUrgent = false

        // Group by deadline day and accumulate
        val deadlineDays = unpaid.map { it.dueDateDay }.distinct().sorted()

        for (deadlineDay in deadlineDays) {
            // Cumulative obligation for all debts due <= this deadline
            val cumulativeObligation = unpaid
                .filter { it.dueDateDay <= deadlineDay }
                .sumOf { it.amount }

            // Work days from tomorrow until this deadline (day of current month)
            val deadlineDate = input.today.withDayOfMonth(
                deadlineDay.coerceAtMost(input.today.lengthOfMonth())
            )
            val workDaysUntil = input.workDaysFromTomorrow.count { it <= deadlineDate }

            // Needed amount
            val needed = cumulativeObligation - input.profitAvailable

            val targetForDeadline = when {
                needed <= 0 -> 0L
                workDaysUntil == 0 -> {
                    hasUrgent = true
                    needed // Can't divide by 0, show full amount as urgent
                }
                else -> ceil(needed.toDouble() / workDaysUntil).toLong()
            }

            // Find the representative obligation for this deadline
            val representative = unpaid.first { it.dueDateDay == deadlineDay }

            results.add(
                DeadlineTargetResult(
                    deadline = representative,
                    cumulativeObligation = cumulativeObligation,
                    workDaysUntil = workDaysUntil,
                    needed = max(0, needed),
                    targetPerDay = targetForDeadline,
                    isUrgent = workDaysUntil == 0 && needed > 0
                )
            )

            maxTarget = max(maxTarget, targetForDeadline)
        }

        val isCovered = maxTarget <= 0

        Timber.d(
            "Target calculation: maxTarget=%d, isCovered=%b, hasUrgent=%b, deadlines=%d",
            maxTarget, isCovered, hasUrgent, results.size
        )

        return Output(
            targetPerDay = maxTarget,
            results = results,
            isCovered = isCovered,
            hasUrgent = hasUrgent
        )
    }
}
