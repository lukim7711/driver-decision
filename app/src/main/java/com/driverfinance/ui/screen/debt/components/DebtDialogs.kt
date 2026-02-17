package com.driverfinance.ui.screen.debt.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.driverfinance.domain.model.PaymentDialogState
import com.driverfinance.ui.theme.Spacing
import java.text.NumberFormat
import java.util.Locale

private val rupiahFmt = NumberFormat.getNumberInstance(Locale("id", "ID"))

/**
 * Payment confirmation dialog.
 *
 * F006 spec mockup F (cicilan) & G (personal):
 * - Shows debt name, amount, before/after remaining
 * - Cicilan tetap/pinjol: "\u2139\uFE0F Tidak masuk pengeluaran harian"
 * - Personal: "Masukkan ke pengeluaran hari ini? [Ya] [Tidak]"
 * - Denda: "\u26A0\uFE0F Masuk pengeluaran hari ini"
 */
@Composable
fun PaymentConfirmDialog(
    state: PaymentDialogState,
    onAmountChange: (String) -> Unit,
    onIncludeAsExpenseChange: (Boolean) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (state.isPenaltyPayment) "Bayar Denda" else "Bayar Cicilan"
            )
        },
        text = {
            Column {
                Text(
                    text = state.debtName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(Spacing.md))

                // Amount input
                OutlinedTextField(
                    value = state.inputAmount,
                    onValueChange = { onAmountChange(it.filter { c -> c.isDigit() }) },
                    label = { Text("Bayar") },
                    prefix = { Text("Rp ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(Spacing.md))

                // Before → After
                if (!state.isPenaltyPayment && state.isValid) {
                    Text(
                        text = "Sisa hutang:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Rp${rupiahFmt.format(state.currentRemaining)} \u2192 Rp${rupiahFmt.format(state.newRemaining)}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                }

                HorizontalDivider()
                Spacer(modifier = Modifier.height(Spacing.sm))

                // Expense inclusion info
                when {
                    state.isPenaltyPayment -> {
                        Text(
                            text = "\u26A0\uFE0F Masuk pengeluaran hari ini (tidak direncanakan)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.needsExpenseChoice -> {
                        Text(
                            text = "Masukkan ke pengeluaran hari ini?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(Spacing.xs))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                        ) {
                            FilterChip(
                                selected = state.includeAsExpense == true,
                                onClick = { onIncludeAsExpenseChange(true) },
                                label = { Text("Ya") }
                            )
                            FilterChip(
                                selected = state.includeAsExpense == false,
                                onClick = { onIncludeAsExpenseChange(false) },
                                label = { Text("Tidak") }
                            )
                        }
                    }
                    else -> {
                        Text(
                            text = "\u2139\uFE0F Tidak masuk pengeluaran harian (sudah dihitung di target)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = state.isValid && (state.includeAsExpense != null || !state.needsExpenseChoice)
            ) {
                Text("\u2705 Bayar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * Delete confirmation dialog.
 * F006 spec: "Yakin hapus?" with Batal + Hapus buttons.
 */
@Composable
fun ConfirmDeleteDialog(
    debtName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Hutang") },
        text = {
            Text("Yakin hapus \"$debtName\"?\n\nHutang akan dihapus dari list aktif, tapi data tetap tersimpan.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("\uD83D\uDDD1\uFE0F Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
