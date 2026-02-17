package com.driverfinance.ui.screen.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.debt.CreateDebtUseCase
import com.driverfinance.domain.usecase.debt.DebtInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AddDebtUiState {
    data class Form(
        val debtType: String,
        val name: String,
        val originalAmount: String,
        val remainingAmount: String,
        val monthlyInstallment: String,
        val dueDateDay: String,
        val penaltyType: String,
        val penaltyAmount: String,
        val note: String,
        val isSaving: Boolean
    ) : AddDebtUiState
}

sealed interface AddDebtEvent {
    data class Success(val id: String) : AddDebtEvent
    data class Error(val message: String) : AddDebtEvent
}

@HiltViewModel
class AddDebtViewModel @Inject constructor(
    private val createDebtUseCase: CreateDebtUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddDebtUiState.Form(
            debtType = "FIXED_INSTALLMENT",
            name = "",
            originalAmount = "",
            remainingAmount = "",
            monthlyInstallment = "",
            dueDateDay = "",
            penaltyType = "NONE",
            penaltyAmount = "",
            note = "",
            isSaving = false
        )
    )
    val uiState: StateFlow<AddDebtUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddDebtEvent>()
    val events: SharedFlow<AddDebtEvent> = _events.asSharedFlow()

    fun updateType(type: String) {
        updateForm {
            it.copy(
                debtType = type,
                penaltyType = if (type == "PINJOL_PAYLATER") "DAILY" else "NONE",
                penaltyAmount = if (type != "PINJOL_PAYLATER") "" else it.penaltyAmount
            )
        }
    }

    fun updateName(name: String) = updateForm { it.copy(name = name) }
    fun updateOriginalAmount(v: String) = updateForm { it.copy(originalAmount = v.filter { c -> c.isDigit() }) }
    fun updateRemainingAmount(v: String) = updateForm { it.copy(remainingAmount = v.filter { c -> c.isDigit() }) }
    fun updateMonthlyInstallment(v: String) = updateForm { it.copy(monthlyInstallment = v.filter { c -> c.isDigit() }) }
    fun updateDueDateDay(v: String) = updateForm { it.copy(dueDateDay = v.filter { c -> c.isDigit() }) }
    fun updatePenaltyType(v: String) = updateForm { it.copy(penaltyType = v) }
    fun updatePenaltyAmount(v: String) = updateForm { it.copy(penaltyAmount = v.filter { c -> c.isDigit() }) }
    fun updateNote(v: String) = updateForm { it.copy(note = v) }

    fun save() {
        val form = _uiState.value as AddDebtUiState.Form
        viewModelScope.launch {
            updateForm { it.copy(isSaving = true) }

            val original = form.originalAmount.toIntOrNull() ?: 0
            val remaining = form.remainingAmount.toIntOrNull() ?: original

            val result = createDebtUseCase(
                DebtInput(
                    name = form.name.trim(),
                    debtType = form.debtType,
                    originalAmount = original,
                    remainingAmount = remaining,
                    monthlyInstallment = form.monthlyInstallment.toIntOrNull(),
                    dueDateDay = form.dueDateDay.toIntOrNull()?.coerceIn(1, 31),
                    penaltyType = form.penaltyType,
                    penaltyAmount = form.penaltyAmount.toIntOrNull() ?: 0,
                    note = form.note
                )
            )

            when (result) {
                is CreateDebtUseCase.CreateResult.Success -> _events.emit(AddDebtEvent.Success(result.id))
                is CreateDebtUseCase.CreateResult.InvalidName -> _events.emit(AddDebtEvent.Error("Nama hutang tidak boleh kosong"))
                is CreateDebtUseCase.CreateResult.InvalidAmount -> _events.emit(AddDebtEvent.Error("Nominal tidak valid"))
                is CreateDebtUseCase.CreateResult.RemainingExceedsOriginal -> _events.emit(AddDebtEvent.Error("Sisa hutang tidak boleh lebih dari total"))
            }
            updateForm { it.copy(isSaving = false) }
        }
    }

    private fun updateForm(transform: (AddDebtUiState.Form) -> AddDebtUiState.Form) {
        val current = _uiState.value as? AddDebtUiState.Form ?: return
        _uiState.value = transform(current)
    }
}
