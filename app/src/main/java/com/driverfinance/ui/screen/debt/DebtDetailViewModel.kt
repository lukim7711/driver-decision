package com.driverfinance.ui.screen.debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.DebtEntity
import com.driverfinance.data.local.entity.DebtPaymentEntity
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.domain.usecase.debt.CalculatePenaltyUseCase
import com.driverfinance.domain.usecase.debt.DeleteDebtUseCase
import com.driverfinance.domain.usecase.debt.PayDebtUseCase
import com.driverfinance.domain.usecase.debt.PaymentInput
import com.driverfinance.domain.usecase.debt.PenaltyResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DebtDetailUiState {
    data object Loading : DebtDetailUiState
    data class Success(
        val debt: DebtEntity,
        val payments: List<DebtPaymentEntity>,
        val penalty: PenaltyResult,
        val realRemaining: Int,
        val progressPercent: Float,
        val paidAmount: Int,
        val remainingMonths: Int?,
        val isPaying: Boolean
    ) : DebtDetailUiState
    data object NotFound : DebtDetailUiState
}

sealed interface DebtDetailEvent {
    data class PaymentSuccess(val amount: Int, val newRemaining: Int, val isPaidOff: Boolean) : DebtDetailEvent
    data object Deleted : DebtDetailEvent
}

@HiltViewModel
class DebtDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val debtRepository: DebtRepository,
    private val calculatePenaltyUseCase: CalculatePenaltyUseCase,
    private val payDebtUseCase: PayDebtUseCase,
    private val deleteDebtUseCase: DeleteDebtUseCase
) : ViewModel() {

    private val debtId: String = savedStateHandle.get<String>("debtId") ?: ""

    private val _uiState = MutableStateFlow<DebtDetailUiState>(DebtDetailUiState.Loading)
    val uiState: StateFlow<DebtDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<DebtDetailEvent>()
    val events: SharedFlow<DebtDetailEvent> = _events.asSharedFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val debt = debtRepository.getDebtById(debtId)
            if (debt == null) {
                _uiState.value = DebtDetailUiState.NotFound
                return@launch
            }

            debtRepository.getPaymentsByDebtId(debtId).collectLatest { payments ->
                val currentDebt = debtRepository.getDebtById(debtId) ?: return@collectLatest
                val penalty = calculatePenaltyUseCase(currentDebt)
                val realRemaining = currentDebt.remainingAmount + penalty.totalPenalty
                val paidAmount = currentDebt.originalAmount - currentDebt.remainingAmount
                val progress = if (currentDebt.originalAmount > 0) {
                    paidAmount.toFloat() / currentDebt.originalAmount
                } else 0f
                val remainingMonths = currentDebt.monthlyInstallment?.let {
                    if (it > 0) (currentDebt.remainingAmount + it - 1) / it else null
                }

                _uiState.value = DebtDetailUiState.Success(
                    debt = currentDebt,
                    payments = payments,
                    penalty = penalty,
                    realRemaining = realRemaining,
                    progressPercent = progress.coerceIn(0f, 1f),
                    paidAmount = paidAmount,
                    remainingMonths = remainingMonths,
                    isPaying = false
                )
            }
        }
    }

    fun pay(amount: Int, paymentType: String, includeAsExpense: Boolean, systemCategoryId: String?) {
        viewModelScope.launch {
            updateSuccess { it.copy(isPaying = true) }
            val result = payDebtUseCase(
                PaymentInput(
                    debtId = debtId,
                    amount = amount,
                    paymentType = paymentType,
                    includeAsExpense = includeAsExpense,
                    systemCategoryId = systemCategoryId,
                    note = null
                )
            )
            if (result is PayDebtUseCase.PayResult.Success) {
                _events.emit(
                    DebtDetailEvent.PaymentSuccess(
                        amount = result.amountPaid,
                        newRemaining = result.newRemaining,
                        isPaidOff = result.isPaidOff
                    )
                )
            }
            updateSuccess { it.copy(isPaying = false) }
            loadDetail()
        }
    }

    fun delete() {
        viewModelScope.launch {
            val result = deleteDebtUseCase(debtId)
            if (result is DeleteDebtUseCase.DeleteResult.Success) {
                _events.emit(DebtDetailEvent.Deleted)
            }
        }
    }

    private fun updateSuccess(transform: (DebtDetailUiState.Success) -> DebtDetailUiState.Success) {
        val current = _uiState.value as? DebtDetailUiState.Success ?: return
        _uiState.value = transform(current)
    }
}
