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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDebtScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddDebtViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddDebtEvent.Success -> onBack()
                is AddDebtEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Hutang") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        val form = uiState as AddDebtUiState.Form

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Spacing.md)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Debt Type Selector
            Text("Tipe:", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                listOf(
                    "FIXED_INSTALLMENT" to "Cicilan",
                    "PINJOL_PAYLATER" to "Pinjol",
                    "PERSONAL" to "Personal"
                ).forEach { (type, label) ->
                    FilterChip(
                        selected = form.debtType == type,
                        onClick = { viewModel.updateType(type) },
                        label = { Text(label) }
                    )
                }
            }

            OutlinedTextField(
                value = form.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Nama Hutang") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = form.originalAmount,
                onValueChange = { viewModel.updateOriginalAmount(it) },
                label = { Text("Total Hutang Awal (Rp)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = form.remainingAmount,
                onValueChange = { viewModel.updateRemainingAmount(it) },
                label = { Text("Sisa Hutang Saat Ini (Rp)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = { Text("Kosongkan jika sama dengan total awal") }
            )

            if (form.debtType != "PERSONAL") {
                OutlinedTextField(
                    value = form.monthlyInstallment,
                    onValueChange = { viewModel.updateMonthlyInstallment(it) },
                    label = { Text("Cicilan Per Bulan (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = form.dueDateDay,
                onValueChange = { viewModel.updateDueDateDay(it) },
                label = { Text("Tanggal Jatuh Tempo (1-31)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                supportingText = {
                    if (form.debtType == "PERSONAL") Text("Opsional")
                }
            )

            if (form.debtType == "PINJOL_PAYLATER") {
                Text("Denda Keterlambatan:", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    listOf(
                        "DAILY" to "Per Hari",
                        "MONTHLY" to "Per Bulan"
                    ).forEach { (type, label) ->
                        FilterChip(
                            selected = form.penaltyType == type,
                            onClick = { viewModel.updatePenaltyType(type) },
                            label = { Text(label) }
                        )
                    }
                }

                OutlinedTextField(
                    value = form.penaltyAmount,
                    onValueChange = { viewModel.updatePenaltyAmount(it) },
                    label = { Text("Nominal Denda (Rp)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = form.note,
                onValueChange = { viewModel.updateNote(it) },
                label = { Text("Catatan (opsional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            Button(
                onClick = { viewModel.save() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !form.isSaving
            ) {
                Text("\uD83D\uDCBE Simpan")
            }
        }
    }
}
