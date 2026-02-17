package com.driverfinance.ui.screen.debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.model.Debt
import com.driverfinance.domain.model.DebtDetailScreenState
import com.driverfinance.domain.model.DebtPayment
import com.driverfinance.domain.model.DebtStatus
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.DebtVisualStatus
import com.driverfinance.domain.model.PaymentDialogState
import com.driverfinance.domain.model.PaymentType
import com.driverfinance.domain.model.PenaltyType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * ViewModel for F006 Debt Detail screen.
 *
 * Handles:
 * - Debt detail display with visual status
 * - Real-time penalty calculation (F006 spec 6.3 #1)
 * - Payment flow (cicilan, denda, personal)
 * - Delete (soft delete)
 *
 * TODO: Inject DebtRepository for real data + payment operations.
 */
@HiltViewModel
class DebtDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val debtId: String = savedStateHandle.get<String>("debtId") ?: ""

    private val _screenState = MutableStateFlow(DebtDetailScreenState())
    val screenState: StateFlow<DebtDetailScreenState> = _screenState.asStateFlow()

    private val _paymentDialogState = MutableStateFlow(PaymentDialogState())
    val paymentDialogState: StateFlow<PaymentDialogState> = _paymentDialogState.asStateFlow()

    init {
        loadDebtDetail()
    }

    private fun loadDebtDetail() {
        viewModelScope.launch {
            try {
                _screenState.update { it.copy(isLoading = true) }
                delay(300)

                // TODO: Replace with debtRepository.getDebtById(debtId)
                val debt = getPlaceholderDebt(debtId)
                val payments = getPlaceholderPayments(debtId)

                if (debt != null) {
                    val penalty = calculatePenalty(debt, payments)
                    val visualStatus = calculateVisualStatus(debt, penalty)

                    _screenState.update {
                        it.copy(
                            debt = debt,
                            payments = payments,
                            visualStatus = visualStatus,
                            currentPenalty = penalty,
                            realRemainingAmount = debt.remainingAmount + penalty,
                            isLoading = false
                        )
                    }
                } else {
                    _screenState.update {
                        it.copy(isLoading = false, errorMessage = "Hutang tidak ditemukan")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load debt detail")
                _screenState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat detail hutang")
                }
            }
        }
    }

    // ---- Payment Dialog ----

    fun showPayDialog() {
        val debt = _screenState.value.debt ?: return
        _paymentDialogState.update {
            PaymentDialogState(
                debtName = debt.name,
                debtType = debt.debtType,
                maxAmount = debt.remainingAmount,
                presetAmount = debt.monthlyInstallment ?: 0,
                inputAmount = (debt.monthlyInstallment ?: 0).toString(),
                currentRemaining = debt.remainingAmount,
                includeAsExpense = when (debt.debtType) {
                    DebtType.FIXED_INSTALLMENT, DebtType.PINJOL_PAYLATER -> false
                    DebtType.PERSONAL -> null // User chooses
                },
                isPenaltyPayment = false
            )
        }
        _screenState.update { it.copy(showPayDialog = true) }
    }

    fun showPayPenaltyDialog() {
        val debt = _screenState.value.debt ?: return
        val penalty = _screenState.value.currentPenalty
        _paymentDialogState.update {
            PaymentDialogState(
                debtName = debt.name,
                debtType = debt.debtType,
                maxAmount = penalty,
                presetAmount = penalty,
                inputAmount = penalty.toString(),
                currentRemaining = debt.remainingAmount,
                includeAsExpense = true, // Denda selalu masuk pengeluaran
                isPenaltyPayment = true
            )
        }
        _screenState.update { it.copy(showPayPenaltyDialog = true) }
    }

    fun updatePaymentAmount(amount: String) {
        _paymentDialogState.update { it.copy(inputAmount = amount) }
    }

    fun updateIncludeAsExpense(include: Boolean) {
        _paymentDialogState.update { it.copy(includeAsExpense = include) }
    }

    fun confirmPayment() {
        viewModelScope.launch {
            val dialogState = _paymentDialogState.value
            if (!dialogState.isValid) return@launch

            // TODO: Replace with actual repository calls:
            //   1. debtRepository.makePayment(debtId, amount, paymentType, includeAsExpense)
            //   2. If includeAsExpense → INSERT quick_entries
            //   3. If willPayOff → set PAID_OFF, trigger F009 ambitious mode check
            Timber.d("Payment confirmed: ${dialogState.amount} for ${dialogState.debtName}")

            dismissPayDialog()
            loadDebtDetail() // Refresh
        }
    }

    fun dismissPayDialog() {
        _screenState.update { it.copy(showPayDialog = false, showPayPenaltyDialog = false) }
    }

    // ---- Delete ----

    fun showDeleteDialog() {
        _screenState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteDialog() {
        _screenState.update { it.copy(showDeleteDialog = false) }
    }

    fun confirmDelete() {
        viewModelScope.launch {
            // TODO: debtRepository.softDelete(debtId)
            //   + trigger F009 ambitious mode check
            Timber.d("Debt deleted: $debtId")
            _screenState.update { it.copy(showDeleteDialog = false) }
        }
    }

    fun dismissError() {
        _screenState.update { it.copy(errorMessage = null) }
    }

    // ---- Penalty Calculation (F006 spec 6.3 #1) ----

    /**
     * Calculate current penalty for a debt.
     *
     * Penalty applies only to PINJOL_PAYLATER with DAILY or MONTHLY penalty.
     * Start date = MAX(due_date this month, last penalty payment date).
     * Penalty stops if this month's installment is paid.
     */
    private fun calculatePenalty(debt: Debt, payments: List<DebtPayment>): Long {
        if (debt.debtType != DebtType.PINJOL_PAYLATER) return 0
        if (debt.penaltyType == PenaltyType.NONE || debt.penaltyAmount <= 0) return 0
        if (debt.dueDateDay == null) return 0

        val today = LocalDate.now()
        val dueDate = today.withDayOfMonth(debt.dueDateDay.coerceAtMost(today.lengthOfMonth()))

        // Check if this month's installment is already paid → no penalty
        val thisMonthInstallmentPaid = payments.any {
            it.paymentType == PaymentType.INSTALLMENT &&
                    it.paymentDate.month == today.month &&
                    it.paymentDate.year == today.year
        }
        if (thisMonthInstallmentPaid) return 0

        // Not yet past due date this month
        if (today.isBefore(dueDate) || today.isEqual(dueDate)) return 0

        // Find last penalty payment date
        val lastPenaltyDate = payments
            .filter { it.paymentType == PaymentType.PENALTY }
            .maxByOrNull { it.paymentDate }
            ?.paymentDate

        // Start date = whichever is more recent
        val startDate = if (lastPenaltyDate != null && lastPenaltyDate.isAfter(dueDate)) {
            lastPenaltyDate
        } else {
            dueDate
        }

        return when (debt.penaltyType) {
            PenaltyType.DAILY -> {
                val daysOverdue = ChronoUnit.DAYS.between(startDate, today)
                if (daysOverdue > 0) debt.penaltyAmount * daysOverdue else 0
            }
            PenaltyType.MONTHLY -> {
                val monthsOverdue = ChronoUnit.MONTHS.between(startDate, today)
                if (monthsOverdue > 0) debt.penaltyAmount * monthsOverdue else 0
            }
            PenaltyType.NONE -> 0
        }
    }

    /**
     * Determine visual status badge.
     * F006 spec 6.3 #2.
     */
    private fun calculateVisualStatus(debt: Debt, penalty: Long): DebtVisualStatus {
        if (debt.status == DebtStatus.PAID_OFF && debt.paidOffAt != null) {
            return DebtVisualStatus.PaidOff(debt.paidOffAt)
        }

        if (debt.dueDateDay == null) {
            return DebtVisualStatus.NoDeadline
        }

        val today = LocalDate.now()
        val dueDate = today.withDayOfMonth(debt.dueDateDay.coerceAtMost(today.lengthOfMonth()))

        return when {
            today.isAfter(dueDate) -> {
                val daysOverdue = ChronoUnit.DAYS.between(dueDate, today).toInt()
                if (debt.penaltyType == PenaltyType.MONTHLY) {
                    val monthsOverdue = ChronoUnit.MONTHS.between(dueDate, today).toInt().coerceAtLeast(1)
                    DebtVisualStatus.OverdueMonths(monthsOverdue, penalty)
                } else {
                    DebtVisualStatus.Overdue(daysOverdue, penalty)
                }
            }
            ChronoUnit.DAYS.between(today, dueDate) <= 7 -> {
                DebtVisualStatus.Approaching(ChronoUnit.DAYS.between(today, dueDate).toInt())
            }
            else -> DebtVisualStatus.OnTrack
        }
    }

    // ---- Placeholder Data ----

    private fun getPlaceholderDebt(id: String): Debt? {
        return when (id) {
            "debt-1" -> Debt(
                id = "debt-1", name = "Cicilan Motor Honda BeAT",
                debtType = DebtType.FIXED_INSTALLMENT, originalAmount = 17_000_000,
                remainingAmount = 12_350_000, monthlyInstallment = 650_000,
                dueDateDay = 15, penaltyType = PenaltyType.NONE, penaltyAmount = 0,
                note = null, status = DebtStatus.ACTIVE, paidOffAt = null,
                createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
            )
            "debt-2" -> Debt(
                id = "debt-2", name = "Pinjol Akulaku",
                debtType = DebtType.PINJOL_PAYLATER, originalAmount = 2_000_000,
                remainingAmount = 1_500_000, monthlyInstallment = 750_000,
                dueDateDay = 10, penaltyType = PenaltyType.DAILY, penaltyAmount = 15_000,
                note = null, status = DebtStatus.ACTIVE, paidOffAt = null,
                createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
            )
            "debt-3" -> Debt(
                id = "debt-3", name = "Hutang ke Budi",
                debtType = DebtType.PERSONAL, originalAmount = 1_000_000,
                remainingAmount = 700_000, monthlyInstallment = null,
                dueDateDay = 28, penaltyType = PenaltyType.NONE, penaltyAmount = 0,
                note = "Bayar kalau sudah ada", status = DebtStatus.ACTIVE, paidOffAt = null,
                createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
            )
            else -> null
        }
    }

    private fun getPlaceholderPayments(debtId: String): List<DebtPayment> {
        return when (debtId) {
            "debt-1" -> listOf(
                DebtPayment("pay-1", debtId, 650_000, PaymentType.INSTALLMENT, false, null, LocalDate.of(2026, 2, 15), null, LocalDateTime.now()),
                DebtPayment("pay-2", debtId, 650_000, PaymentType.INSTALLMENT, false, null, LocalDate.of(2026, 1, 15), null, LocalDateTime.now()),
                DebtPayment("pay-3", debtId, 650_000, PaymentType.INSTALLMENT, false, null, LocalDate.of(2025, 12, 15), null, LocalDateTime.now())
            )
            "debt-2" -> listOf(
                DebtPayment("pay-4", debtId, 750_000, PaymentType.INSTALLMENT, false, null, LocalDate.of(2026, 1, 10), null, LocalDateTime.now())
            )
            "debt-3" -> listOf(
                DebtPayment("pay-5", debtId, 100_000, PaymentType.PARTIAL, true, null, LocalDate.of(2026, 2, 5), null, LocalDateTime.now()),
                DebtPayment("pay-6", debtId, 100_000, PaymentType.PARTIAL, true, null, LocalDate.of(2026, 1, 20), null, LocalDateTime.now()),
                DebtPayment("pay-7", debtId, 100_000, PaymentType.PARTIAL, false, null, LocalDate.of(2026, 1, 3), null, LocalDateTime.now())
            )
            else -> emptyList()
        }
    }
}
