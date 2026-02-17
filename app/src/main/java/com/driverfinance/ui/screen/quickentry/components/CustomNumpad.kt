package com.driverfinance.ui.screen.quickentry.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.driverfinance.domain.model.CategoryUiModel
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.util.toRupiah

/**
 * Custom numpad for entering amounts not in presets.
 * Keys: 1-9, 000, 0, backspace.
 *
 * F004 spec mockup D:
 *   \uD83C\uDF5A Makan \u2014 Berapa?
 *   Rp 37.000
 *   [1] [2] [3]
 *   [4] [5] [6]
 *   [7] [8] [9]
 *   [000] [0] [\u232B]
 *   Catatan (opsional): _______
 *   [Simpan]
 */
@Composable
fun CustomNumpad(
    category: CategoryUiModel,
    digits: String,
    note: String,
    isSaving: Boolean,
    canSave: Boolean,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Header: back + category name
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali"
                )
            }
            Spacer(modifier = Modifier.width(Spacing.xs))
            Text(
                text = "${category.emoji} ${category.name} \u2014 Berapa?",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Display amount
        val displayAmount = if (digits.isEmpty()) "Rp0" else (digits.toIntOrNull() ?: 0).toRupiah()
        Text(
            text = displayAmount,
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Numpad grid: 4 rows x 3 columns
        val numpadRows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("000", "0", "DEL")
        )

        numpadRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                row.forEach { key ->
                    if (key == "DEL") {
                        FilledTonalButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .weight(1f)
                                .height(Spacing.huge),
                            shape = RoundedCornerShape(Spacing.sm)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Backspace,
                                contentDescription = "Hapus"
                            )
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { onDigit(key) },
                            modifier = Modifier
                                .weight(1f)
                                .height(Spacing.huge),
                            shape = RoundedCornerShape(Spacing.sm)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        // Note field
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Save button
        Button(
            onClick = onSave,
            enabled = canSave && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.huge),
            shape = RoundedCornerShape(Spacing.sm)
        ) {
            Text(
                text = if (isSaving) "Menyimpan..." else "Simpan",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
