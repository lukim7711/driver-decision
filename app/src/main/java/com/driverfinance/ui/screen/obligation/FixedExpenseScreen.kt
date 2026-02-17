package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.data.local.entity.FixedExpenseEntity
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FixedExpenseScreen(
    onBack: () -> Unit,
    onAddExpense: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FixedExpenseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biaya Tetap Bulanan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState is FixedExpenseUiState.ExpenseList) {
                ExtendedFloatingActionButton(
                    onClick = onAddExpense,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Tambah Biaya Tetap") }
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is FixedExpenseUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(4) {
                        Card(modifier = Modifier.fillMaxWidth().height(64.dp)) { }
                    }
                }
            }
            is FixedExpenseUiState.ShowTemplate -> {
                TemplateContent(
                    state = state,
                    onToggle = viewModel::toggleTemplate,
                    onConfirm = viewModel::confirmTemplates,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            is FixedExpenseUiState.ExpenseList -> {
                ExpenseListContent(
                    data = state.data,
                    onDelete = viewModel::deleteExpense,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun TemplateContent(
    state: FixedExpenseUiState.ShowTemplate,
    onToggle: (Int) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.md)
    ) {
        Text(
            text = "Pilih biaya tetap yang kamu punya:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Spacing.md))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            itemsIndexed(state.templates) { index, template ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onToggle(index) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(Spacing.md),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = index in state.selected,
                            onCheckedChange = { onToggle(index) }
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(text = template.emoji, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = template.name, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = "Rp${formatObligationRupiah(template.defaultAmount)}/bulan",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))
        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Lanjut \u2192")
        }
    }
}

@Composable
private fun ExpenseListContent(
    data: com.driverfinance.domain.usecase.obligation.FixedExpenseListData,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(
                        text = "Total: Rp${formatObligationRupiah(data.total)}/bulan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        items(data.expenses, key = { it.id }) { expense ->
            ExpenseCard(expense = expense, onDelete = { onDelete(expense.id) })
        }

        if (data.expenses.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Belum ada biaya tetap", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Tap + untuk tambah",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun ExpenseCard(
    expense: FixedExpenseEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = expense.emoji, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Rp${formatObligationRupiah(expense.amount)}/bulan",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun formatObligationRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
