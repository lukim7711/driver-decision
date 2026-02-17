package com.driverfinance.ui.screen.debt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.model.Debt
import com.driverfinance.domain.model.DebtStatus
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.PenaltyType
import com.driverfinance.ui.screen.debt.components.DebtCard
import com.driverfinance.ui.screen.debt.components.DebtEmptyState
import com.driverfinance.ui.screen.debt.components.DebtSummaryHeader
import com.driverfinance.ui.theme.Spacing

/**
 * F006 — Debt List Screen.
 *
 * Layout: Summary header → Active debts → Paid-off debts → FAB add.
 * Accessed from "Lain" tab → "\uD83D\uDCB0 Hutang".
 */
@Composable
fun DebtListScreen(
    onDebtClick: (String) -> Unit,
    onAddDebtClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DebtListViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            if (!state.isEmpty) {
                FloatingActionButton(
                    onClick = onAddDebtClick,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Tambah Hutang",
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenPadding, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hutang",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (state.isEmpty) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DebtEmptyState(onAddClick = onAddDebtClick)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = Spacing.screenPadding)
                ) {
                    // Summary
                    item(key = "summary") {
                        DebtSummaryHeader(
                            totalRemaining = state.totalRemainingAmount,
                            totalMonthlyInstallment = state.totalMonthlyInstallment
                        )
                        Spacer(modifier = Modifier.height(Spacing.lg))
                    }

                    // Active debts section
                    if (state.activeDebts.isNotEmpty()) {
                        item(key = "active-header") {
                            SectionHeader(text = "Aktif")
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }

                        items(state.activeDebts, key = { it.id }) { debt ->
                            DebtCard(
                                debt = debt,
                                onClick = { onDebtClick(debt.id) }
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    }

                    // Paid-off debts section
                    if (state.paidOffDebts.isNotEmpty()) {
                        item(key = "paidoff-header") {
                            Spacer(modifier = Modifier.height(Spacing.lg))
                            SectionHeader(text = "Lunas")
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }

                        items(state.paidOffDebts, key = { it.id }) { debt ->
                            DebtCard(
                                debt = debt,
                                onClick = { onDebtClick(debt.id) }
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                        }
                    }

                    // Bottom spacing for FAB
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = "\u2500\u2500\u2500\u2500 $text \u2500\u2500\u2500\u2500",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
