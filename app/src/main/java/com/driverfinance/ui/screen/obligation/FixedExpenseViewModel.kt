package com.driverfinance.ui.screen.obligation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.obligation.FixedExpenseInput
import com.driverfinance.domain.usecase.obligation.FixedExpenseListData
import com.driverfinance.domain.usecase.obligation.FixedExpenseTemplate
import com.driverfinance.domain.usecase.obligation.GetFixedExpensesUseCase
import com.driverfinance.domain.usecase.obligation.ManageFixedExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FixedExpenseUiState {
    data object Loading : FixedExpenseUiState
    data class ShowTemplate(
        val templates: List<FixedExpenseTemplate>,
        val selected: Set<Int>
    ) : FixedExpenseUiState
    data class ExpenseList(val data: FixedExpenseListData) : FixedExpenseUiState
}

@HiltViewModel
class FixedExpenseViewModel @Inject constructor(
    private val getFixedExpensesUseCase: GetFixedExpensesUseCase,
    private val manageFixedExpenseUseCase: ManageFixedExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FixedExpenseUiState>(FixedExpenseUiState.Loading)
    val uiState: StateFlow<FixedExpenseUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val shouldShowTemplate = manageFixedExpenseUseCase.shouldShowTemplate()
            if (shouldShowTemplate) {
                _uiState.value = FixedExpenseUiState.ShowTemplate(
                    templates = ManageFixedExpenseUseCase.DEFAULT_TEMPLATES,
                    selected = emptySet()
                )
            } else {
                getFixedExpensesUseCase().collectLatest { data ->
                    _uiState.value = FixedExpenseUiState.ExpenseList(data)
                }
            }
        }
    }

    fun toggleTemplate(index: Int) {
        val current = _uiState.value as? FixedExpenseUiState.ShowTemplate ?: return
        val newSelected = current.selected.toMutableSet()
        if (index in newSelected) newSelected.remove(index) else newSelected.add(index)
        _uiState.value = current.copy(selected = newSelected)
    }

    fun confirmTemplates() {
        val current = _uiState.value as? FixedExpenseUiState.ShowTemplate ?: return
        viewModelScope.launch {
            val selectedTemplates = current.selected.map { current.templates[it] }
            if (selectedTemplates.isNotEmpty()) {
                manageFixedExpenseUseCase.seedFromTemplate(selectedTemplates)
            } else {
                manageFixedExpenseUseCase.create(
                    FixedExpenseInput(emoji = "\u2795", name = "Placeholder", amount = 0, note = null)
                )
            }
            getFixedExpensesUseCase().collectLatest { data ->
                _uiState.value = FixedExpenseUiState.ExpenseList(data)
            }
        }
    }

    fun addExpense(input: FixedExpenseInput) {
        viewModelScope.launch {
            manageFixedExpenseUseCase.create(input)
        }
    }

    fun updateExpense(id: String, input: FixedExpenseInput) {
        viewModelScope.launch {
            manageFixedExpenseUseCase.update(id, input)
        }
    }

    fun deleteExpense(id: String) {
        viewModelScope.launch {
            manageFixedExpenseUseCase.delete(id)
        }
    }
}
