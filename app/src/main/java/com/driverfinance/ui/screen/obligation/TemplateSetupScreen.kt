package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.theme.Spacing

/**
 * F009 — Fixed Expense Template Setup Screen.
 *
 * F009 spec mockup C: checklist of 7 default templates.
 * Shown only on first-time setup (no records in fixed_expenses).
 */
@Composable
fun TemplateSetupScreen(
    onComplete: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: TemplateSetupViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
    ) {
        // Header
        Text(
            text = "Pilih biaya tetap yang kamu punya:",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = Spacing.lg)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(state.templates) { index, template ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleTemplate(index) }
                        .padding(vertical = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = template.isSelected,
                        onCheckedChange = { viewModel.toggleTemplate(index) }
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = template.emoji,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = template.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(Spacing.md))
                Text(
                    text = "Driver centang yang relevan, lalu isi nominal masing-masing.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Confirm button
        Button(
            onClick = { viewModel.confirmSelection(onComplete) },
            enabled = state.hasSelection && !state.isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md)
        ) {
            Text(
                text = if (state.hasSelection) "Lanjut \u2192 (${state.selectedCount} dipilih)"
                else "Pilih minimal 1"
            )
        }
    }
}
