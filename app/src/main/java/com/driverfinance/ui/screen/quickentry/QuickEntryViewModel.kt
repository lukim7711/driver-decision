package com.driverfinance.ui.screen.quickentry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.driverfinance.data.local.entity.QuickEntryCategoryEntity
import com.driverfinance.data.local.entity.QuickEntryPresetEntity
import com.driverfinance.domain.usecase.quickentry.GetCategoriesUseCase
import com.driverfinance.domain.usecase.quickentry.GetPresetsUseCase
import com.driverfinance.domain.usecase.quickentry.GetTodayQuickEntrySummaryUseCase
import com.driverfinance.domain.usecase.quickentry.SaveQuickEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface QuickEntryPhase {
    data object CategoryGrid : QuickEntryPhase
    data class NominalSelect(
        val category: QuickEntryCategoryEntity,
        val presets: List<QuickEntryPresetEntity>
    ) : QuickEntryPhase
    data class NumpadCustom(
        val category: QuickEntryCategoryEntity,
        val currentInput: String
    ) : QuickEntryPhase
}

sealed interface QuickEntryUiState {
    data object Loading : QuickEntryUiState
    data class Success(
        val activeTab: EntryTab,
        val phase: QuickEntryPhase,
        val categories: List<QuickEntryCategoryEntity>,
        val todayCount: Int,
        val todayTotal: Int,
        val isSaving: Boolean,
        val note: String
    ) : QuickEntryUiState
}

enum class EntryTab(val type: String, val label: String) {
    EXPENSE("EXPENSE", "Pengeluaran"),
    INCOME("INCOME", "Pemasukan")
}

data class SavedConfirmation(
    val categoryName: String,
    val categoryIcon: String,
    val amount: Int,
    val note: String?
)

@HiltViewModel
class QuickEntryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getPresetsUseCase: GetPresetsUseCase,
    private val saveQuickEntryUseCase: SaveQuickEntryUseCase,
    private val getSummaryUseCase: GetTodayQuickEntrySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuickEntryUiState>(QuickEntryUiState.Loading)
    val uiState: StateFlow<QuickEntryUiState> = _uiState.asStateFlow()

    private val _savedEvent = MutableSharedFlow<SavedConfirmation>()
    val savedEvent: SharedFlow<SavedConfirmation> = _savedEvent.asSharedFlow()

    private var currentTab = EntryTab.EXPENSE

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase(currentTab.type).collectLatest { categories ->
                val summary = getSummaryUseCase()
                val (count, total) = when (currentTab) {
                    EntryTab.EXPENSE -> summary.expenseCount to summary.expenseTotal
                    EntryTab.INCOME -> summary.incomeCount to summary.incomeTotal
                }
                _uiState.value = QuickEntryUiState.Success(
                    activeTab = currentTab,
                    phase = QuickEntryPhase.CategoryGrid,
                    categories = categories,
                    todayCount = count,
                    todayTotal = total,
                    isSaving = false,
                    note = ""
                )
            }
        }
    }

    fun switchTab(tab: EntryTab) {
        currentTab = tab
        loadCategories()
    }

    fun selectCategory(category: QuickEntryCategoryEntity) {
        viewModelScope.launch {
            getPresetsUseCase(category.id).collectLatest { presets ->
                updateSuccess { it.copy(
                    phase = QuickEntryPhase.NominalSelect(category, presets),
                    note = ""
                )}
            }
        }
    }

    fun selectPreset(amount: Int) {
        val state = _uiState.value as? QuickEntryUiState.Success ?: return
        val phase = state.phase as? QuickEntryPhase.NominalSelect ?: return
        saveEntry(phase.category, amount, state.note)
    }

    fun openNumpad() {
        val state = _uiState.value as? QuickEntryUiState.Success ?: return
        val phase = state.phase as? QuickEntryPhase.NominalSelect ?: return
        updateSuccess { it.copy(
            phase = QuickEntryPhase.NumpadCustom(phase.category, "")
        )}
    }

    fun numpadInput(digit: String) {
        val state = _uiState.value as? QuickEntryUiState.Success ?: return
        val phase = state.phase as? QuickEntryPhase.NumpadCustom ?: return
        val newInput = when (digit) {
            "DEL" -> phase.currentInput.dropLast(1)
            else -> {
                val candidate = phase.currentInput + digit
                if (candidate.length > 9) phase.currentInput else candidate
            }
        }
        updateSuccess { it.copy(
            phase = QuickEntryPhase.NumpadCustom(phase.category, newInput)
        )}
    }

    fun submitNumpad() {
        val state = _uiState.value as? QuickEntryUiState.Success ?: return
        val phase = state.phase as? QuickEntryPhase.NumpadCustom ?: return
        val amount = phase.currentInput.toIntOrNull() ?: return
        if (amount <= 0) return
        saveEntry(phase.category, amount, state.note)
    }

    fun updateNote(note: String) {
        updateSuccess { it.copy(note = note) }
    }

    fun backToGrid() {
        updateSuccess { it.copy(
            phase = QuickEntryPhase.CategoryGrid,
            note = ""
        )}
    }

    private fun saveEntry(
        category: QuickEntryCategoryEntity,
        amount: Int,
        note: String
    ) {
        viewModelScope.launch {
            updateSuccess { it.copy(isSaving = true) }
            val result = saveQuickEntryUseCase(
                type = currentTab.type,
                categoryId = category.id,
                amount = amount,
                note = note.takeIf { it.isNotBlank() }
            )
            if (result is SaveQuickEntryUseCase.SaveResult.Success) {
                _savedEvent.emit(SavedConfirmation(
                    categoryName = category.name,
                    categoryIcon = category.emoji,
                    amount = amount,
                    note = note.takeIf { it.isNotBlank() }
                ))
                delay(DOUBLE_TAP_GUARD_MS)
                val summary = getSummaryUseCase()
                val (count, total) = when (currentTab) {
                    EntryTab.EXPENSE -> summary.expenseCount to summary.expenseTotal
                    EntryTab.INCOME -> summary.incomeCount to summary.incomeTotal
                }
                updateSuccess { it.copy(
                    phase = QuickEntryPhase.CategoryGrid,
                    todayCount = count,
                    todayTotal = total,
                    isSaving = false,
                    note = ""
                )}
            } else {
                updateSuccess { it.copy(isSaving = false) }
            }
        }
    }

    private fun updateSuccess(transform: (QuickEntryUiState.Success) -> QuickEntryUiState.Success) {
        val current = _uiState.value as? QuickEntryUiState.Success ?: return
        _uiState.value = transform(current)
    }

    companion object {
        const val DOUBLE_TAP_GUARD_MS = 500L
    }
}
