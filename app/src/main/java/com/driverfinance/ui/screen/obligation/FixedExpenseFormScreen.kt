package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.driverfinance.ui.theme.Spacing

/**
 * F009 — Add/Edit Fixed Expense Form.
 *
 * F009 spec mockup B: emoji picker, name, amount, note.
 */
@Composable
fun FixedExpenseFormScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: FixedExpenseFormViewModel = hiltViewModel()
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
            text = if (state.isEditMode) "Edit Biaya Tetap" else "Tambah Biaya Tetap",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = Spacing.md)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Emoji picker
            Text(
                text = "Emoji:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            // Simple text field for emoji (TODO: replace with emoji picker component)
            OutlinedTextField(
                value = state.emoji,
                onValueChange = { if (it.length <= 2) viewModel.updateEmoji(it) },
                label = { Text("Tap untuk pilih") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.lg))

            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nama") },
                placeholder = { Text("Misal: Pulsa / Paket Data") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Amount
            OutlinedTextField(
                value = state.amount,
                onValueChange = viewModel::updateAmount,
                label = { Text("Nominal Per Bulan") },
                prefix = { Text("Rp ") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            // Note
            OutlinedTextField(
                value = state.note,
                onValueChange = viewModel::updateNote,
                label = { Text("Catatan (opsional)") },
                placeholder = { Text("Misal: Telkomsel + paket data 15GB") },
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
