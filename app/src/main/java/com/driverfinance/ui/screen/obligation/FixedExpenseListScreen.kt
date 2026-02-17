package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.screen.obligation.components.FixedExpenseCard
import com.driverfinance.ui.screen.obligation.components.FixedExpenseEmptyState
import com.driverfinance.ui.screen.obligation.components.FixedExpenseTotalHeader
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.util.Locale

/**
 * F009 — Fixed Expense List Screen.
 *
 * Layout: Total header → list of fixed expenses → FAB add.
 * F009 spec mockup A.
 * Accessed from "Lain" tab → "📋 Biaya Tetap Bulanan".
 */
@Composable
fun FixedExpenseListScreen(
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onTemplateSetup: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: FixedExpenseListViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (!state.isEmpty && !state.showTemplateSetup) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Biaya Tetap",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Text(
                text = "Biaya Tetap Bulanan",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(
                    horizontal = Spacing.screenPadding,
                    vertical = Spacing.md
                )
            )

            when {
                state.showTemplateSetup -> {
                    // First time — show template selection
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FixedExpenseEmptyState(
                            onAddClick = onAddClick,
                            onTemplateClick = onTemplateSetup
                        )
                    }
                }
                state.isEmpty -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        FixedExpenseEmptyState(
                            onAddClick = onAddClick,
                            onTemplateClick = null
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Spacing.screenPadding)
                    ) {
                        item(key = "total") {
                            FixedExpenseTotalHeader(totalAmount = state.totalAmount)
                            Spacer(modifier = Modifier.height(Spacing.lg))
                        }

                        items(state.expenses, key = { it.id }) { expense ->
                            FixedExpenseCard(
                                expense = expense,
                                onEditClick = { onEditClick(expense.id) },
                                onDeleteClick = { viewModel.deleteExpense(expense.id) }
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}
