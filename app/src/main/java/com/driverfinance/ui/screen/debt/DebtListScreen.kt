package com.driverfinance.ui.screen.debt

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.usecase.debt.DebtDisplayItem
import com.driverfinance.domain.usecase.debt.DebtStatus
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtListScreen(
    onBack: () -> Unit,
    onDebtClick: (String) -> Unit,
    onAddDebt: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebtListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hutang") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddDebt,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Tambah Hutang") }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is DebtListUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(4) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { }
                    }
                }
            }
            is DebtListUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md)
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is DebtListUiState.Success -> {
                val data = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    item {
                        TotalSummaryCard(
                            totalRemaining = data.totalRemaining,
                            totalMonthly = data.totalMonthlyInstallment
                        )
                    }

                    if (data.activeDebts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = "\u2500\u2500\u2500 Aktif \u2500\u2500\u2500",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(data.activeDebts, key = { it.debt.id }) { item ->
                            DebtCard(
                                item = item,
                                onClick = { onDebtClick(item.debt.id) }
                            )
                        }
                    }

                    if (data.paidOffDebts.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Text(
                                text = "\u2500\u2500\u2500 Lunas \u2500\u2500\u2500",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        items(data.paidOffDebts, key = { it.debt.id }) { item ->
                            DebtCard(
                                item = item,
                                onClick = { onDebtClick(item.debt.id) }
                            )
                        }
                    }

                    if (data.activeDebts.isEmpty() && data.paidOffDebts.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Belum ada hutang \uD83C\uDF89",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "Tap + untuk tambah hutang",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun TotalSummaryCard(
    totalRemaining: Int,
    totalMonthly: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Total Sisa Hutang",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Rp${formatDebtRupiah(totalRemaining)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Cicilan Per Bulan: Rp${formatDebtRupiah(totalMonthly)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DebtCard(
    item: DebtDisplayItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = item.debt.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            when (val status = item.status) {
                is DebtStatus.PaidOff -> {
                    Text(
                        text = "\u2705 Lunas \u00B7 ${status.date.take(10)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32)
                    )
                }
                else -> {
                    Text(
                        text = "Sisa: Rp${formatDebtRupiah(item.realRemaining)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val installment = item.debt.monthlyInstallment
                    if (installment != null && installment > 0) {
                        Text(
                            text = "Cicilan: Rp${formatDebtRupiah(installment)}/bln",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val dueDateDay = item.debt.dueDateDay
                    if (dueDateDay != null) {
                        Text(
                            text = "Jatuh tempo: tgl $dueDateDay",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(status = status)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: DebtStatus, modifier: Modifier = Modifier) {
    val (text, color) = when (status) {
        is DebtStatus.OnTrack -> "\uD83D\uDFE2 On-track" to Color(0xFF2E7D32)
        is DebtStatus.Approaching -> "\uD83D\uDFE1 ${status.daysLeft} hari lagi" to Color(0xFFED6C02)
        is DebtStatus.Late -> "\uD83D\uDD34 Telat ${status.units} ${status.unitLabel} \u00B7 Denda Rp${formatDebtRupiah(status.penaltyAmount)}" to Color(0xFFD32F2F)
        is DebtStatus.NoDueDate -> "\uD83D\uDFE2 Tidak ada deadline" to Color(0xFF2E7D32)
        is DebtStatus.PaidOff -> "" to Color.Transparent
    }
    if (text.isNotEmpty()) {
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            fontWeight = FontWeight.SemiBold,
            modifier = modifier
        )
    }
}

private fun formatDebtRupiah(amount: Int): String {
    val abs = kotlin.math.abs(amount)
    val formatted = String.format("%,d", abs).replace(',', '.')
    return if (amount < 0) "-$formatted" else formatted
}
