package com.driverfinance.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * Domain models for F006 Manajemen Hutang.
 * Covers 3 debt types, penalty calculation, payment tracking.
 */

/** Tipe hutang: Cicilan Tetap, Pinjol/Paylater, Personal. */
enum class DebtType(val label: String) {
    FIXED_INSTALLMENT("Cicilan Tetap"),
    PINJOL_PAYLATER("Pinjol/Paylater"),
    PERSONAL("Personal")
}

/** Tipe denda: per hari, per bulan, atau tidak ada. */
enum class PenaltyType(val label: String) {
    DAILY("Per Hari"),
    MONTHLY("Per Bulan"),
    NONE("Tidak Ada")
}

/** Status hutang. */
enum class DebtStatus {
    ACTIVE,
    PAID_OFF,
    DELETED
}

/** Tipe pembayaran. */
enum class PaymentType(val label: String) {
    INSTALLMENT("Cicilan"),
    PENALTY("Denda"),
    PARTIAL("Pembayaran")
}

/**
 * Visual status badge for debt list/detail.
 * F006 spec Section 6.3 #2.
 */
sealed class DebtVisualStatus {
    data object OnTrack : DebtVisualStatus()
    data class Approaching(val daysLeft: Int) : DebtVisualStatus()
    data class Overdue(val daysOverdue: Int, val penaltyAmount: Long) : DebtVisualStatus()
    data class OverdueMonths(val monthsOverdue: Int, val penaltyAmount: Long) : DebtVisualStatus()
    data object NoDeadline : DebtVisualStatus()
    data class PaidOff(val paidOffAt: LocalDateTime) : DebtVisualStatus()
}

/**
 * Domain model for a debt.
 * Maps from Room entity DebtEntity.
 */
data class Debt(
    val id: String,
    val name: String,
    val debtType: DebtType,
    val originalAmount: Long,
    val remainingAmount: Long,
    val monthlyInstallment: Long?,
    val dueDateDay: Int?,
    val penaltyType: PenaltyType,
    val penaltyAmount: Long,
    val note: String?,
    val status: DebtStatus,
    val paidOffAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    /** Percentage paid off (0-100). */
    val paidPercentage: Int
        get() = if (originalAmount > 0) {
            ((originalAmount - remainingAmount) * 100 / originalAmount).toInt().coerceIn(0, 100)
        } else 0

    /** Amount already paid. */
    val amountPaid: Long
        get() = originalAmount - remainingAmount

    /** Estimated months remaining based on monthly installment. */
    val estimatedMonthsRemaining: Int?
        get() = monthlyInstallment?.let {
            if (it > 0) ((remainingAmount + it - 1) / it).toInt() else null
        }
}

/**
 * Domain model for a debt payment.
 */
data class DebtPayment(
    val id: String,
    val debtId: String,
    val amount: Long,
    val paymentType: PaymentType,
    val includeAsExpense: Boolean,
    val linkedExpenseId: String?,
    val paymentDate: LocalDate,
    val note: String?,
    val createdAt: LocalDateTime
)

/**
 * Screen state for debt list.
 */
data class DebtListScreenState(
    val activeDebts: List<Debt> = emptyList(),
    val paidOffDebts: List<Debt> = emptyList(),
    val isLoading: Boolean = true,
    val totalRemainingAmount: Long = 0,
    val totalMonthlyInstallment: Long = 0,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean
        get() = activeDebts.isEmpty() && paidOffDebts.isEmpty() && !isLoading
}

/**
 * Screen state for debt detail.
 */
data class DebtDetailScreenState(
    val debt: Debt? = null,
    val payments: List<DebtPayment> = emptyList(),
    val visualStatus: DebtVisualStatus = DebtVisualStatus.OnTrack,
    val currentPenalty: Long = 0,
    val realRemainingAmount: Long = 0,
    val isLoading: Boolean = true,
    val showPayDialog: Boolean = false,
    val showPayPenaltyDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Screen state for debt form (add/edit).
 */
data class DebtFormScreenState(
    val isEditMode: Boolean = false,
    val debtId: String? = null,
    val name: String = "",
    val debtType: DebtType = DebtType.FIXED_INSTALLMENT,
    val originalAmount: String = "",
    val remainingAmount: String = "",
    val monthlyInstallment: String = "",
    val dueDateDay: String = "",
    val penaltyType: PenaltyType = PenaltyType.NONE,
    val penaltyAmount: String = "",
    val note: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val savedSuccessfully: Boolean = false
) {
    val canSave: Boolean
        get() = name.isNotBlank() &&
                originalAmount.isNotBlank() &&
                remainingAmount.isNotBlank() &&
                !isSaving

    /** Show monthly installment field for non-personal types. */
    val showInstallmentField: Boolean
        get() = debtType != DebtType.PERSONAL

    /** Show penalty fields for pinjol/paylater type. */
    val showPenaltyFields: Boolean
        get() = debtType == DebtType.PINJOL_PAYLATER
}

/**
 * Payment dialog state.
 */
data class PaymentDialogState(
    val debtName: String = "",
    val debtType: DebtType = DebtType.FIXED_INSTALLMENT,
    val maxAmount: Long = 0,
    val presetAmount: Long = 0,
    val inputAmount: String = "",
    val currentRemaining: Long = 0,
    val includeAsExpense: Boolean? = null,
    val isPenaltyPayment: Boolean = false
) {
    val amount: Long
        get() = inputAmount.toLongOrNull() ?: 0

    val isValid: Boolean
        get() = amount > 0 && amount <= maxAmount

    val newRemaining: Long
        get() = (currentRemaining - amount).coerceAtLeast(0)

    val willPayOff: Boolean
        get() = amount >= currentRemaining

    /** Personal debts need user choice for expense inclusion. */
    val needsExpenseChoice: Boolean
        get() = debtType == DebtType.PERSONAL && !isPenaltyPayment
}
