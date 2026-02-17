package com.driverfinance.ui.screen.debt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.model.DebtType
import com.driverfinance.domain.model.PenaltyType
import com.driverfinance.ui.theme.Spacing

/**
 * F006 — Add/Edit Debt Form.
 *
 * F006 spec mockup E: Form fields vary by debt type.
 * - Cicilan Tetap: name, amounts, installment, due date
 * - Pinjol/Paylater: + penalty type, penalty amount
 * - Personal: name, amounts, due date (optional), note
 */
@Composable
fun DebtFormScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DebtFormViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(state.savedSuccessfully) {
        if (state.savedSuccessfully) onSaved()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        // Header
        Text(
            text = if (state.isEditMode) "Edit Hutang" else "Tambah Hutang",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = Spacing.md)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Debt type chips
            Text(
                text = "Tipe:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                DebtType.entries.forEach { type ->
                    FilterChip(
                        selected = state.debtType == type,
                        onClick = { viewModel.updateDebtType(type) },
                        label = { Text(type.label) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.lg))

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nama Hutang") },
                placeholder = { Text("Misal: Cicilan Motor Honda BeAT") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Original amount
            OutlinedTextField(
                value = state.originalAmount,
                onValueChange = viewModel::updateOriginalAmount,
                label = { Text("Total Hutang Awal") },
                prefix = { Text("Rp ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Remaining amount
            OutlinedTextField(
                value = state.remainingAmount,
                onValueChange = viewModel::updateRemainingAmount,
                label = { Text("Sisa Hutang Saat Ini") },
                prefix = { Text("Rp ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Monthly installment (not for Personal)
            if (state.showInstallmentField) {
                OutlinedTextField(
                    value = state.monthlyInstallment,
                    onValueChange = viewModel::updateMonthlyInstallment,
                    label = { Text("Cicilan Per Bulan") },
                    prefix = { Text("Rp ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // Due date day
            OutlinedTextField(
                value = state.dueDateDay,
                onValueChange = viewModel::updateDueDateDay,
                label = { Text("Tanggal Jatuh Tempo (1-31)") },
                placeholder = { Text("Opsional untuk personal") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Penalty fields (Pinjol/Paylater only)
            if (state.showPenaltyFields) {
                Text(
                    text = "Denda Keterlambatan:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    listOf(PenaltyType.DAILY, PenaltyType.MONTHLY).forEach { type ->
                        FilterChip(
                            selected = state.penaltyType == type,
                            onClick = { viewModel.updatePenaltyType(type) },
                            label = { Text(type.label) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.sm))

                OutlinedTextField(
                    value = state.penaltyAmount,
                    onValueChange = viewModel::updatePenaltyAmount,
                    label = { Text("Nominal Denda") },
                    prefix = { Text("Rp ") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(Spacing.md))
            }

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::updateNote,
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            Spacer(modifier = Modifier.height(Spacing.xxl))
        }

        // Save button
        Button(
            onClick = viewModel::save,
            enabled = state.canSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md)
        ) {
            Text("\uD83D\uDCBE Simpan")
        }
    }
}
