package com.driverfinance.domain.usecase.debt

import com.driverfinance.data.local.entity.DebtEntity
import com.driverfinance.data.repository.DebtRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetDebtsUseCase @Inject constructor(
    private val repository: DebtRepository,
    private val calculatePenaltyUseCase: CalculatePenaltyUseCase
) {

    operator fun invoke(): Flow<DebtListData> {
        return combine(
            repository.getActiveDebts(),
            repository.getPaidOffDebts()
        ) { activeDebts, paidOffDebts ->

            val activeItems = activeDebts.map { debt ->
                val penalty = calculatePenaltyUseCase(debt)
                DebtDisplayItem(
                    debt = debt,
                    penalty = penalty,
                    status = computeStatus(debt, penalty),
                    realRemaining = debt.remainingAmount + penalty.totalPenalty
                )
            }

            val paidOffItems = paidOffDebts.map { debt ->
                DebtDisplayItem(
                    debt = debt,
                    penalty = PenaltyResult(0, 0, false),
                    status = DebtStatus.PaidOff(debt.paidOffAt ?: ""),
                    realRemaining = 0
                )
            }

            val totalRemaining = activeItems.sumOf { it.realRemaining }
            val totalMonthly = activeDebts.sumOf { it.monthlyInstallment ?: 0 }

            DebtListData(
                activeDebts = activeItems,
                paidOffDebts = paidOffItems,
                totalRemaining = totalRemaining,
                totalMonthlyInstallment = totalMonthly
            )
        }
    }

    private fun computeStatus(debt: DebtEntity, penalty: PenaltyResult): DebtStatus {
        if (penalty.isLate) {
            val unit = if (debt.penaltyType == "MONTHLY") "bulan" else "hari"
            return DebtStatus.Late(penalty.unitsLate, unit, penalty.totalPenalty)
        }

        val dueDateDay = debt.dueDateDay
        if (dueDateDay == null) return DebtStatus.NoDueDate

        val today = LocalDate.now()
        val dueDate = try {
            today.withDayOfMonth(dueDateDay.coerceAtMost(today.lengthOfMonth()))
        } catch (e: Exception) {
            today.withDayOfMonth(today.lengthOfMonth())
        }

        val adjustedDue = if (dueDate.isBefore(today)) dueDate.plusMonths(1) else dueDate
        val daysUntil = java.time.temporal.ChronoUnit.DAYS.between(today, adjustedDue).toInt()

        return when {
            daysUntil <= 7 -> DebtStatus.Approaching(daysUntil)
            else -> DebtStatus.OnTrack
        }
    }
}

data class DebtListData(
    val activeDebts: List<DebtDisplayItem>,
    val paidOffDebts: List<DebtDisplayItem>,
    val totalRemaining: Int,
    val totalMonthlyInstallment: Int
)

data class DebtDisplayItem(
    val debt: DebtEntity,
    val penalty: PenaltyResult,
    val status: DebtStatus,
    val realRemaining: Int
)

sealed interface DebtStatus {
    data object OnTrack : DebtStatus
    data class Approaching(val daysLeft: Int) : DebtStatus
    data class Late(val units: Int, val unitLabel: String, val penaltyAmount: Int) : DebtStatus
    data class PaidOff(val date: String) : DebtStatus
    data object NoDueDate : DebtStatus
}
