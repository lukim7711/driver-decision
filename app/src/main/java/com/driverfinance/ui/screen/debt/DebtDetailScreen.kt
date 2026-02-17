package com.driverfinance.ui.screen.debt

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.DebtVisualStatus
import com.driverfinance.domain.model.PenaltyType
import com.driverfinance.ui.screen.debt.components.ConfirmDeleteDialog
import com.driverfinance.ui.screen.debt.components.PaymentConfirmDialog
import com.driverfinance.ui.screen.debt.components.PaymentHistoryRow
import com.driverfinance.ui.screen.debt.components.StatusBadge
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.util.Locale

/**
 * F006 — Debt Detail Screen.
 *
 * Shows full debt info with:
 * - Visual status badge
 * - Progress bar (% paid)
 * - Summary fields (type-specific)
 * - Penalty warning (Pinjol only)
 * - Payment history
 * - Action buttons: Bayar, (Bayar Denda), Edit, Hapus
 */
@Composable
fun DebtDetailScreen(
    onEditClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: DebtDetailViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentDialogState.collectAsStateWithLifecycle()
    val debt = state.debt
    val fmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

    // Dialogs
    if (state.showPayDialog || state.showPayPenaltyDialog) {
        PaymentConfirmDialog(
            state = paymentState,
            onAmountChange = viewModel::updatePaymentAmount,
            onIncludeAsExpenseChange = viewModel::updateIncludeAsExpense,
            onConfirm = viewModel::confirmPayment,
            onDismiss = viewModel::dismissPayDialog
        )
    }

    if (state.showDeleteDialog) {
        ConfirmDeleteDialog(
            debtName = debt?.name ?: "",
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = debt?.name ?: "Detail Hutang",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        if (debt != null) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Spacing.screenPadding)
            ) {
                // Type & Status
                item(key = "status") {
                    Text(
                        text = "Tipe: ${debt.debtType.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    StatusBadge(status = state.visualStatus)
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                // Amounts
                item(key = "amounts") {
                    DetailRow("Total Hutang Awal", "Rp${fmt.format(debt.originalAmount)}")
                    DetailRow("Sisa Hutang", "Rp${fmt.format(debt.remainingAmount)}")

                    if (state.currentPenalty > 0) {
                        DetailRow(
                            "Denda Berjalan",
                            "Rp${fmt.format(state.currentPenalty)}",
                            isWarning = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                        DetailRow(
                            "Sisa Hutang Real",
                            "Rp${fmt.format(state.realRemainingAmount)}",
                            isBold = true
                        )
                    } else {
                        DetailRow("Sudah Dibayar", "Rp${fmt.format(debt.amountPaid)}")
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))
                }

                // Progress bar
                item(key = "progress") {
                    LinearProgressIndicator(
                        progress = { debt.paidPercentage / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Text(
                        text = "${debt.paidPercentage}% lunas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = Spacing.xxs)
                    )
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                // Detail fields (type-specific)
                item(key = "details") {
                    if (debt.monthlyInstallment != null) {
                        DetailRow("Cicilan/Bulan", "Rp${fmt.format(debt.monthlyInstallment)}")
                    }
                    if (debt.dueDateDay != null) {
                        DetailRow("Jatuh Tempo", "Tanggal ${debt.dueDateDay}")
                    }
                    if (debt.debtType == DebtType.PINJOL_PAYLATER && debt.penaltyAmount > 0) {
                        DetailRow(
                            "Denda",
                            "Rp${fmt.format(debt.penaltyAmount)}/${debt.penaltyType.label.lowercase()}"
                        )
                    }
                    debt.estimatedMonthsRemaining?.let { months ->
                        DetailRow("Sisa Cicilan", "$months bulan lagi")
                    }
                    if (!debt.note.isNullOrBlank()) {
                        DetailRow("Catatan", "\"${debt.note}\"")
                    }
                    Spacer(modifier = Modifier.height(Spacing.lg))
                }

                // Penalty warning (Pinjol overdue)
                if (state.currentPenalty > 0 && debt.debtType == DebtType.PINJOL_PAYLATER) {
                    item(key = "penalty-warning") {
                        Text(
                            text = "\u26A0\uFE0F Denda bertambah Rp${fmt.format(debt.penaltyAmount)} " +
                                    "setiap ${debt.penaltyType.label.lowercase()} sampai dibayar!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(Spacing.lg))
                    }
                }

                // Payment history
                if (state.payments.isNotEmpty()) {
                    item(key = "history-header") {
                        Text(
                            text = "\u2500\u2500\u2500\u2500 Riwayat Bayar \u2500\u2500\u2500\u2500",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Spacing.sm))
                    }

                    items(state.payments, key = { it.id }) { payment ->
                        PaymentHistoryRow(payment = payment)
                        Spacer(modifier = Modifier.height(Spacing.xs))
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            // Bottom action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(
                    onClick = viewModel::showPayDialog,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("\uD83D\uDCB0 Bayar")
                }

                if (state.currentPenalty > 0) {
                    OutlinedButton(
                        onClick = viewModel::showPayPenaltyDialog,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Bayar Denda")
                    }
                }

                OutlinedButton(
                    onClick = { debt.id.let(onEditClick) }
                ) {
                    Text("\u270F\uFE0F")
                }

                OutlinedButton(
                    onClick = viewModel::showDeleteDialog,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("\uD83D\uDDD1\uFE0F")
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isWarning: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xxs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = when {
                isWarning -> MaterialTheme.colorScheme.error
                isBold -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
