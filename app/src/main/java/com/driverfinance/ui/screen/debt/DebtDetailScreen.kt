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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.data.local.entity.DebtPaymentEntity
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebtDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DebtDetailEvent.PaymentSuccess -> {
                    val msg = if (event.isPaidOff) "\uD83C\uDF89 Hutang lunas!"
                        else "\u2705 Dibayar Rp${formatDetailRupiah(event.amount)}"
                    snackbarHostState.showSnackbar(msg)
                }
                is DebtDetailEvent.Deleted -> onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (uiState as? DebtDetailUiState.Success)?.debt?.name ?: "Detail Hutang"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    val state = uiState as? DebtDetailUiState.Success
                    if (state != null && state.debt.status == "ACTIVE") {
                        IconButton(onClick = { onEdit(state.debt.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { viewModel.delete() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is DebtDetailUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(5) {
                        Card(
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { }
                    }
                }
            }
            is DebtDetailUiState.NotFound -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md)
                ) {
                    Text("Hutang tidak ditemukan", color = MaterialTheme.colorScheme.error)
                }
            }
            is DebtDetailUiState.Success -> {
                DebtDetailContent(
                    state = state,
                    onPay = { amount, type, asExpense, catId ->
                        viewModel.pay(amount, type, asExpense, catId)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DebtDetailContent(
    state: DebtDetailUiState.Success,
    onPay: (Int, String, Boolean, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val debt = state.debt

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Info Card
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(Spacing.md)) {
                val typeLabel = when (debt.debtType) {
                    "FIXED_INSTALLMENT" -> "Cicilan Tetap"
                    "PINJOL_PAYLATER" -> "Pinjol/Paylater"
                    "PERSONAL" -> "Pinjaman Personal"
                    else -> debt.debtType
                }
                Text("Tipe: $typeLabel", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(Spacing.md))
                DetailRow("Total Hutang Awal", "Rp${formatDetailRupiah(debt.originalAmount)}")
                DetailRow("Sisa Hutang", "Rp${formatDetailRupiah(debt.remainingAmount)}")

                if (state.penalty.totalPenalty > 0) {
                    DetailRow("Denda Berjalan", "Rp${formatDetailRupiah(state.penalty.totalPenalty)}")
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    DetailRow("Sisa Hutang Real", "Rp${formatDetailRupiah(state.realRemaining)}")
                }

                DetailRow("Sudah Dibayar", "Rp${formatDetailRupiah(state.paidAmount)}")

                Spacer(modifier = Modifier.height(Spacing.sm))
                LinearProgressIndicator(
                    progress = { state.progressPercent },
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )
                Text(
                    text = "${(state.progressPercent * 100).toInt()}% lunas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Spacing.sm))
                val installment = debt.monthlyInstallment
                if (installment != null && installment > 0) {
                    DetailRow("Cicilan/Bulan", "Rp${formatDetailRupiah(installment)}")
                }
                val dueDay = debt.dueDateDay
                if (dueDay != null) {
                    DetailRow("Jatuh Tempo", "Tanggal $dueDay")
                }
                state.remainingMonths?.let {
                    DetailRow("Sisa Cicilan", "$it bulan lagi")
                }
            }
        }

        // Penalty Warning
        if (state.penalty.isLate) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "\u26A0\uFE0F Denda bertambah Rp${formatDetailRupiah(debt.penaltyAmount)}/${if (debt.penaltyType == "MONTHLY") "bulan" else "hari"} sampai dibayar!",
                    modifier = Modifier.padding(Spacing.md),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Payments History
        if (state.payments.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(Spacing.md)) {
                    Text(
                        text = "Riwayat Bayar",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    state.payments.take(10).forEach { payment ->
                        PaymentRow(payment)
                    }
                    if (state.payments.size > 10) {
                        Text(
                            text = "... ${state.payments.size - 10} lagi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons
        if (debt.status == "ACTIVE") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Button(
                    onClick = {
                        val amount = debt.monthlyInstallment ?: debt.remainingAmount
                        onPay(amount, "INSTALLMENT", false, null)
                    },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isPaying
                ) {
                    Text("\uD83D\uDCB0 Bayar")
                }

                if (state.penalty.totalPenalty > 0) {
                    OutlinedButton(
                        onClick = {
                            onPay(state.penalty.totalPenalty, "PENALTY", true, null)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !state.isPaying
                    ) {
                        Text("Bayar Denda")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun PaymentRow(payment: DebtPaymentEntity) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = payment.paymentDate,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Rp${formatDetailRupiah(payment.amount)} \u2705",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDetailRupiah(amount: Int): String {
    val abs = kotlin.math.abs(amount)
    return String.format("%,d", abs).replace(',', '.')
}
