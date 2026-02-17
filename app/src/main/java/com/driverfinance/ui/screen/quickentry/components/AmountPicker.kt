package com.driverfinance.ui.screen.quickentry.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.driverfinance.domain.model.CategoryUiModel
import com.driverfinance.ui.theme.Spacing
import com.driverfinance.util.toRupiah

/**
 * Preset amount buttons for a selected category.
 * Tap preset → auto-save (no explicit save button).
 *
 * F004 spec mockup C:
 *   \uD83C\uDF5A Makan \u2014 Berapa?
 *   [10.000] [15.000] [20.000]
 *   [25.000] [30.000] [50.000]
 *   [Ketik nominal lain]
 *   Catatan (opsional): _______
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AmountPicker(
    category: CategoryUiModel,
    note: String,
    isSaving: Boolean,
    onAmountClick: (Int) -> Unit,
    onCustomClick: () -> Unit,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
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

        // Preset amounts grid
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            category.presetAmounts.forEach { amount ->
                FilledTonalButton(
                    onClick = { if (!isSaving) onAmountClick(amount) },
                    enabled = !isSaving,
                    shape = RoundedCornerShape(Spacing.sm)
                ) {
                    Text(text = amount.toRupiah())
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Custom amount button
        OutlinedButton(
            onClick = onCustomClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Spacing.sm)
        ) {
            Text(text = "Ketik nominal lain")
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Note field
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Catatan (opsional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
