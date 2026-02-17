package com.driverfinance.ui.screen.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.domain.usecase.debt.DebtListData
import com.driverfinance.domain.usecase.debt.GetDebtsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DebtListUiState {
    data object Loading : DebtListUiState
    data class Success(val data: DebtListData) : DebtListUiState
    data class Error(val message: String) : DebtListUiState
}

@HiltViewModel
class DebtListViewModel @Inject constructor(
    private val getDebtsUseCase: GetDebtsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DebtListUiState>(DebtListUiState.Loading)
    val uiState: StateFlow<DebtListUiState> = _uiState.asStateFlow()

    init {
        loadDebts()
    }

    private fun loadDebts() {
        viewModelScope.launch {
            try {
                getDebtsUseCase().collectLatest { data ->
                    _uiState.value = DebtListUiState.Success(data)
                }
            } catch (e: Exception) {
                _uiState.value = DebtListUiState.Error(e.message ?: "Gagal memuat data hutang")
            }
        }
    }
}
