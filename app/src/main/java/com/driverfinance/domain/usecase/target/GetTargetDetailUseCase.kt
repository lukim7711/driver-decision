package com.driverfinance.domain.usecase.target

import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.data.repository.DailyTargetRepository
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.data.repository.FixedExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

/**
 * Provides full breakdown data for Target Detail screen.
 * Combines cache + live debt/expense data.
 */
class GetTargetDetailUseCase @Inject constructor(
    private val dailyTargetRepository: DailyTargetRepository,
    private val debtRepository: DebtRepository,
    private val fixedExpenseRepository: FixedExpenseRepository
) {

    operator fun invoke(): Flow<TargetDetailData?> {
        return combine(
            dailyTargetRepository.getTodayCache(),
            debtRepository.getActiveDebts(),
            fixedExpenseRepository.getActiveExpenses()
        ) { cache, activeDebts, fixedExpenses ->
            if (cache == null) return@combine null

            val today = LocalDate.now()

            // Debt obligations with deadline info
            val debtObligations = activeDebts
                .filter { it.monthlyInstallment != null && it.monthlyInstallment > 0 }
                .map { debt ->
                    ObligationItem(
                        emoji = when (debt.debtType) {
                            "FIXED_INSTALLMENT" -> "\uD83D\uDEB2"
                            "PINJOL_PAYLATER" -> "\uD83D\uDCF1"
                            else -> "\uD83D\uDCB0"
                        },
                        name = debt.name,
                        amount = debt.monthlyInstallment ?: 0,
                        dueDateDay = debt.dueDateDay,
                        isUrgent = debt.dueDateDay != null &&
                            debt.dueDateDay >= today.dayOfMonth &&
                            (debt.dueDateDay - today.dayOfMonth) <= 3,
                        isOverdue = debt.dueDateDay != null && debt.dueDateDay < today.dayOfMonth
                    )
                }

            // Fixed expense obligations
            val fixedExpenseObligations = fixedExpenses.map { expense ->
                ObligationItem(
                    emoji = expense.emoji,
                    name = expense.name,
                    amount = expense.amount,
                    dueDateDay = null,
                    isUrgent = false,
                    isOverdue = false
                )
            }

            // Urgent deadlines (≤3 days)
            val urgentDeadlines = debtObligations.filter { it.isUrgent }
            val overdueDeadlines = debtObligations.filter { it.isOverdue }

            // Today's profit from cache (profitAccumulated is month-to-date)
            val todayProfit = cache.profitAccumulated.coerceAtLeast(0)

            TargetDetailData(
                cache = cache,
                todayProfit = todayProfit,
                todayRemaining = (cache.targetAmount - todayProfit).coerceAtLeast(0),
                todayProgress = if (cache.targetAmount > 0) {
                    (todayProfit * 100) / cache.targetAmount
                } else 100,
                debtObligations = debtObligations,
                fixedExpenseObligations = fixedExpenseObligations,
                subtotalCicilan = debtObligations.sumOf { it.amount },
                subtotalBiayaTetap = fixedExpenseObligations.sumOf { it.amount },
                urgentDeadlines = urgentDeadlines,
                overdueDeadlines = overdueDeadlines
            )
        }
    }
}

data class TargetDetailData(
    val cache: DailyTargetCacheEntity,
    val todayProfit: Int,
    val todayRemaining: Int,
    val todayProgress: Int,
    val debtObligations: List<ObligationItem>,
    val fixedExpenseObligations: List<ObligationItem>,
    val subtotalCicilan: Int,
    val subtotalBiayaTetap: Int,
    val urgentDeadlines: List<ObligationItem>,
    val overdueDeadlines: List<ObligationItem>
)

data class ObligationItem(
    val emoji: String,
    val name: String,
    val amount: Int,
    val dueDateDay: Int?,
    val isUrgent: Boolean,
    val isOverdue: Boolean
)
