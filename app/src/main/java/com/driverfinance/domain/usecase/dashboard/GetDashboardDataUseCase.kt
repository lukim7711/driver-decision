package com.driverfinance.domain.usecase.dashboard

import com.driverfinance.data.repository.CaptureRepository
import com.driverfinance.data.repository.ExtractionRepository
import com.driverfinance.data.repository.QuickEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val captureRepository: CaptureRepository,
    private val quickEntryRepository: QuickEntryRepository,
    private val extractionRepository: ExtractionRepository
) {

    operator fun invoke(): Flow<DashboardData> {
        val today = LocalDate.now().toString()

        return combine(
            captureRepository.getTodayHistoryTrips(today),
            quickEntryRepository.getTodayAllEntries(today)
        ) { historyTrips, quickEntries ->

            val shopeeEarning = historyTrips.sumOf { it.totalEarning }
            val tripCount = historyTrips.size

            val tripsByService = historyTrips
                .groupBy { it.serviceType }
                .mapValues { it.value.size }

            val activeExpenses = quickEntries.filter {
                it.type == TYPE_EXPENSE && it.isDeleted == 0
            }
            val totalExpense = activeExpenses.sumOf { it.amount }
            val expenseCount = activeExpenses.size

            val activeIncomes = quickEntries.filter {
                it.type == TYPE_INCOME && it.isDeleted == 0
            }
            val totalOtherIncome = activeIncomes.sumOf { it.amount }
            val incomeCount = activeIncomes.size

            val totalRevenue = shopeeEarning + totalOtherIncome
            val profit = totalRevenue - totalExpense

            DashboardData(
                shopeeEarning = shopeeEarning,
                otherIncome = totalOtherIncome,
                otherIncomeCount = incomeCount,
                totalExpense = totalExpense,
                expenseCount = expenseCount,
                profit = profit,
                tripCount = tripCount,
                orderCount = 0,
                totalPoints = 0,
                tripsByService = tripsByService,
                otherIncomeItems = activeIncomes.take(3).map {
                    IncomeItem(amount = it.amount, note = it.note)
                }
            )
        }
    }

    companion object {
        const val TYPE_EXPENSE = "EXPENSE"
        const val TYPE_INCOME = "INCOME"
    }
}

data class DashboardData(
    val shopeeEarning: Int,
    val otherIncome: Int,
    val otherIncomeCount: Int,
    val totalExpense: Int,
    val expenseCount: Int,
    val profit: Int,
    val tripCount: Int,
    val orderCount: Int,
    val totalPoints: Int,
    val tripsByService: Map<String, Int>,
    val otherIncomeItems: List<IncomeItem>
)

data class IncomeItem(
    val amount: Int,
    val note: String?
)
