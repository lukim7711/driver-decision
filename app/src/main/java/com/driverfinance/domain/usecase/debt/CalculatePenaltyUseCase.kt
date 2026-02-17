package com.driverfinance.domain.usecase.debt

import com.driverfinance.data.local.entity.DebtEntity
import com.driverfinance.data.repository.DebtRepository
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Real-time penalty calculation for PINJOL_PAYLATER debts.
 * - DAILY: penalty_amount × days since (due_date OR last penalty payment, whichever is newer)
 * - MONTHLY: penalty_amount × months since reference
 * - Penalty = 0 if installment paid this month (not late)
 */
class CalculatePenaltyUseCase @Inject constructor(
    private val repository: DebtRepository
) {

    suspend operator fun invoke(debt: DebtEntity): PenaltyResult {
        if (debt.debtType != "PINJOL_PAYLATER") return PenaltyResult(0, 0, false)
        if (debt.penaltyType == "NONE" || debt.penaltyAmount == 0) return PenaltyResult(0, 0, false)
        if (debt.status != "ACTIVE") return PenaltyResult(0, 0, false)

        val today = LocalDate.now()
        val dueDateDay = debt.dueDateDay ?: return PenaltyResult(0, 0, false)

        val dueDate = try {
            today.withDayOfMonth(dueDateDay.coerceAtMost(today.lengthOfMonth()))
        } catch (e: Exception) {
            today.withDayOfMonth(today.lengthOfMonth())
        }

        val effectiveDueDate = if (today.dayOfMonth <= dueDateDay) {
            dueDate.minusMonths(1)
        } else {
            dueDate
        }

        if (today.isBefore(effectiveDueDate) || today.isEqual(effectiveDueDate)) {
            return PenaltyResult(0, 0, false)
        }

        val lastPenaltyPaymentDate = repository.getLastPenaltyPaymentDate(debt.id)
        val referenceDate = if (lastPenaltyPaymentDate != null) {
            val lastPayment = LocalDate.parse(lastPenaltyPaymentDate)
            if (lastPayment.isAfter(effectiveDueDate)) lastPayment else effectiveDueDate
        } else {
            effectiveDueDate
        }

        val isLate = today.isAfter(referenceDate)
        if (!isLate) return PenaltyResult(0, 0, false)

        return when (debt.penaltyType) {
            "DAILY" -> {
                val daysLate = ChronoUnit.DAYS.between(referenceDate, today).toInt()
                PenaltyResult(
                    totalPenalty = debt.penaltyAmount * daysLate,
                    unitsLate = daysLate,
                    isLate = true
                )
            }
            "MONTHLY" -> {
                val monthsLate = ChronoUnit.MONTHS.between(referenceDate, today).toInt().coerceAtLeast(1)
                PenaltyResult(
                    totalPenalty = debt.penaltyAmount * monthsLate,
                    unitsLate = monthsLate,
                    isLate = true
                )
            }
            else -> PenaltyResult(0, 0, false)
        }
    }
}

data class PenaltyResult(
    val totalPenalty: Int,
    val unitsLate: Int,
    val isLate: Boolean
)
