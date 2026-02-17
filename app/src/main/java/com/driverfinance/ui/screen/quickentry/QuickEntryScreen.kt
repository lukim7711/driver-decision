package com.driverfinance.ui.screen.quickentry

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.driverfinance.domain.model.EntryType
import com.driverfinance.domain.model.InputPhase
import com.driverfinance.ui.screen.quickentry.components.AmountPicker
import com.driverfinance.ui.screen.quickentry.components.CategoryGrid
import com.driverfinance.ui.screen.quickentry.components.CustomNumpad
import com.driverfinance.ui.screen.quickentry.components.DailyCounter
import com.driverfinance.ui.theme.Spacing

/**
 * F004 — Input Cepat.
 * Flow: tap category → tap preset amount (auto-save) OR custom numpad → save.
 * Pencatatan beruntun: after save, back to grid for next entry.
 * Target: < 3 detik per pencatatan.
 */
@Composable
fun QuickEntryScreen(
    navController: NavController,
    viewModel: QuickEntryViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    BackHandler(enabled = state.phase != InputPhase.CATEGORY_GRID) {
        when (state.phase) {
            InputPhase.CUSTOM_NUMPAD -> viewModel.goBackToPresets()
            InputPhase.AMOUNT_PICKER -> viewModel.goBackToGrid()
            else -> { /* no-op */ }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        Spacer(modifier = Modifier.height(Spacing.screenTopPadding))

        Text(
            text = "Input Cepat",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Tab row: Pengeluaran | Pemasukan
        val tabs = listOf(EntryType.EXPENSE to "Pengeluaran", EntryType.INCOME to "Pemasukan")
        val selectedIndex = tabs.indexOfFirst { it.first == state.activeTab }

        TabRow(
            selectedTabIndex = selectedIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, (type, label) ->
                Tab(
                    selected = selectedIndex == index,
                    onClick = { viewModel.switchTab(type) },
                    text = {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Phase-based content
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "phaseTransition",
            modifier = Modifier.weight(1f)
        ) { phase ->
            when (phase) {
                InputPhase.CATEGORY_GRID -> {
                    CategoryGrid(
                        categories = state.filteredCategories,
                        onCategoryClick = viewModel::selectCategory
                    )
                }

                InputPhase.AMOUNT_PICKER -> {
                    state.selectedCategory?.let { category ->
                        AmountPicker(
                            category = category,
                            note = state.note,
                            isSaving = state.isSaving,
                            onAmountClick = viewModel::selectPresetAmount,
                            onCustomClick = viewModel::openCustomNumpad,
                            onNoteChange = viewModel::updateNote,
                            onBack = viewModel::goBackToGrid
                        )
                    }
                }

                InputPhase.CUSTOM_NUMPAD -> {
                    state.selectedCategory?.let { category ->
                        CustomNumpad(
                            category = category,
                            digits = state.customAmountDigits,
                            note = state.note,
                            isSaving = state.isSaving,
                            canSave = state.customAmountValue > 0,
                            onDigit = viewModel::appendDigit,
                            onDelete = viewModel::deleteLastDigit,
                            onNoteChange = viewModel::updateNote,
                            onSave = viewModel::saveCustomAmount,
                            onBack = viewModel::goBackToPresets
                        )
                    }
                }
            }
        }

        // Daily counter
        DailyCounter(
            count = state.todayCount,
            total = state.todayTotal,
            label = state.todayLabel
        )

        // Saved confirmation snackbar
        state.lastSavedEntry?.let { entry ->
            Snackbar(
                modifier = Modifier.padding(vertical = Spacing.sm)
            ) {
                Text(
                    text = "\u2705 ${entry.categoryEmoji} ${entry.categoryName} ${com.driverfinance.util.CurrencyFormatter.format(entry.amount)}" +
                            (entry.note?.let { " \u2014 $it" }.orEmpty())
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))
    }
}
