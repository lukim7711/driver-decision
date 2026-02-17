package com.driverfinance.domain.usecase.debt

import com.driverfinance.data.local.entity.DebtPaymentEntity
import com.driverfinance.data.local.entity.QuickEntryEntity
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.data.repository.QuickEntryRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class PayDebtUseCase @Inject constructor(
    private val debtRepository: DebtRepository,
    private val quickEntryRepository: QuickEntryRepository
) {

    suspend operator fun invoke(input: PaymentInput): PayResult {
        val debt = debtRepository.getDebtById(input.debtId) ?: return PayResult.DebtNotFound
        if (input.amount <= 0) return PayResult.InvalidAmount

        val effectiveAmount = input.amount.coerceAtMost(debt.remainingAmount)
        val now = OffsetDateTime.now()
        val nowStr = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val today = LocalDate.now().toString()

        var linkedExpenseId: String? = null

        if (input.includeAsExpense) {
            val systemCategoryName = when (input.paymentType) {
                "PENALTY" -> "Denda Hutang"
                "PARTIAL" -> "Pembayaran Hutang"
                else -> null
            }

            if (systemCategoryName != null) {
                val expenseId = UUID.randomUUID().toString()
                val note = when (input.paymentType) {
                    "PENALTY" -> "Denda ${debt.name}"
                    else -> "Bayar ${debt.name}"
                }
                val entry = QuickEntryEntity(
                    id = expenseId,
                    type = "EXPENSE",
                    categoryId = input.systemCategoryId ?: "",
                    amount = effectiveAmount,
                    note = note,
                    entryDate = today,
                    entryTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    isDeleted = 0,
                    createdAt = nowStr,
                    updatedAt = nowStr
                )
                quickEntryRepository.saveEntry(entry)
                linkedExpenseId = expenseId
            }
        }

        val payment = DebtPaymentEntity(
            id = UUID.randomUUID().toString(),
            debtId = input.debtId,
            amount = effectiveAmount,
            paymentType = input.paymentType,
            includeAsExpense = if (input.includeAsExpense) 1 else 0,
            linkedExpenseId = linkedExpenseId,
            paymentDate = today,
            note = input.note,
            createdAt = nowStr
        )
        debtRepository.insertPayment(payment)

        val newRemaining = if (input.paymentType == "PENALTY") {
            debt.remainingAmount
        } else {
            (debt.remainingAmount - effectiveAmount).coerceAtLeast(0)
        }

        val newStatus = if (newRemaining <= 0) "PAID_OFF" else debt.status
        val paidOffAt = if (newRemaining <= 0) nowStr else debt.paidOffAt

        debtRepository.updateDebt(
            debt.copy(
                remainingAmount = newRemaining,
                status = newStatus,
                paidOffAt = paidOffAt,
                updatedAt = nowStr
            )
        )

        return PayResult.Success(
            amountPaid = effectiveAmount,
            newRemaining = newRemaining,
            isPaidOff = newRemaining <= 0
        )
    }

    sealed interface PayResult {
        data class Success(
            val amountPaid: Int,
            val newRemaining: Int,
            val isPaidOff: Boolean
        ) : PayResult
        data object DebtNotFound : PayResult
        data object InvalidAmount : PayResult
    }
}

data class PaymentInput(
    val debtId: String,
    val amount: Int,
    val paymentType: String,
    val includeAsExpense: Boolean,
    val systemCategoryId: String?,
    val note: String?
)
