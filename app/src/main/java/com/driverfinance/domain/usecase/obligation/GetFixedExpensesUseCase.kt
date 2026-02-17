package com.driverfinance.domain.usecase.obligation

import com.driverfinance.data.local.entity.FixedExpenseEntity
import com.driverfinance.data.repository.FixedExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFixedExpensesUseCase @Inject constructor(
    private val repository: FixedExpenseRepository
) {

    operator fun invoke(): Flow<FixedExpenseListData> {
        return repository.getActiveExpenses().map { expenses ->
            FixedExpenseListData(
                expenses = expenses,
                total = expenses.sumOf { it.amount }
            )
        }
    }
}

data class FixedExpenseListData(
    val expenses: List<FixedExpenseEntity>,
    val total: Int
)
