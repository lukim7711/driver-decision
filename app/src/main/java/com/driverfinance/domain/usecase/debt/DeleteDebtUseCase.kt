package com.driverfinance.domain.usecase.debt

import com.driverfinance.data.repository.DebtRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Soft deletes a debt (status = DELETED).
 * Post-delete: checks if active debts remain (for F009 ambitious mode trigger).
 */
class DeleteDebtUseCase @Inject constructor(
    private val repository: DebtRepository
) {

    suspend operator fun invoke(debtId: String): DeleteResult {
        val debt = repository.getDebtById(debtId) ?: return DeleteResult.NotFound
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        repository.updateDebt(
            debt.copy(status = "DELETED", updatedAt = now)
        )

        val activeCount = repository.getActiveDebtCount()

        return DeleteResult.Success(activeDebtsRemaining = activeCount)
    }

    sealed interface DeleteResult {
        data class Success(val activeDebtsRemaining: Int) : DeleteResult
        data object NotFound : DeleteResult
    }
}
