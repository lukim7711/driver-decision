package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.repository.QuickEntryRepository
import java.time.LocalDate
import javax.inject.Inject

class GetTodayQuickEntrySummaryUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    suspend operator fun invoke(): Summary {
        val today = LocalDate.now().toString()
        return Summary(
            expenseCount = repository.getTodayCount(today, TYPE_EXPENSE),
            expenseTotal = repository.getTodaySummary(today, TYPE_EXPENSE),
            incomeCount = repository.getTodayCount(today, TYPE_INCOME),
            incomeTotal = repository.getTodaySummary(today, TYPE_INCOME)
        )
    }

    data class Summary(
        val expenseCount: Int,
        val expenseTotal: Int,
        val incomeCount: Int,
        val incomeTotal: Int
    )

    companion object {
        const val TYPE_EXPENSE = "EXPENSE"
        const val TYPE_INCOME = "INCOME"
    }
}
