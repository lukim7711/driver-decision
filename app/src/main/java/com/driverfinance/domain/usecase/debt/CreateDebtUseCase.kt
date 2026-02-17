package com.driverfinance.domain.usecase.debt

import com.driverfinance.data.local.entity.DebtEntity
import com.driverfinance.data.repository.DebtRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class CreateDebtUseCase @Inject constructor(
    private val repository: DebtRepository
) {

    suspend operator fun invoke(input: DebtInput): CreateResult {
        if (input.name.isBlank()) return CreateResult.InvalidName
        if (input.originalAmount <= 0) return CreateResult.InvalidAmount
        if (input.remainingAmount < 0) return CreateResult.InvalidAmount
        if (input.remainingAmount > input.originalAmount) return CreateResult.RemainingExceedsOriginal

        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val entity = DebtEntity(
            id = UUID.randomUUID().toString(),
            name = input.name,
            debtType = input.debtType,
            originalAmount = input.originalAmount,
            remainingAmount = input.remainingAmount,
            monthlyInstallment = input.monthlyInstallment,
            dueDateDay = input.dueDateDay,
            penaltyType = input.penaltyType,
            penaltyAmount = input.penaltyAmount,
            note = input.note?.takeIf { it.isNotBlank() },
            status = if (input.remainingAmount == 0) "PAID_OFF" else "ACTIVE",
            paidOffAt = if (input.remainingAmount == 0) now else null,
            createdAt = now,
            updatedAt = now
        )
        repository.insertDebt(entity)
        return CreateResult.Success(entity.id)
    }

    sealed interface CreateResult {
        data class Success(val id: String) : CreateResult
        data object InvalidName : CreateResult
        data object InvalidAmount : CreateResult
        data object RemainingExceedsOriginal : CreateResult
    }
}

data class DebtInput(
    val name: String,
    val debtType: String,
    val originalAmount: Int,
    val remainingAmount: Int,
    val monthlyInstallment: Int?,
    val dueDateDay: Int?,
    val penaltyType: String,
    val penaltyAmount: Int,
    val note: String?
)
