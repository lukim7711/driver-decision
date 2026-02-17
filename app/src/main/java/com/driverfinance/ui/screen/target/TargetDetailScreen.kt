package com.driverfinance.ui.screen.target

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.usecase.target.ObligationItem
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TargetDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.events.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(event) {
        val e = event ?: return@LaunchedEffect
        when (e) {
            is TargetDetailEvent.ShowSnackbar -> {
                snackbarHostState.showSnackbar(e.message)
                viewModel.consumeEvent()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Target Harian") },
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
        when (val state = uiState) {
            is TargetDetailUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(5) {
                        Card(modifier = Modifier.fillMaxWidth().height(80.dp)) { }
                    }
                }
            }
            is TargetDetailUiState.NoObligation -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.xl),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("\uD83D\uDCCA", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is TargetDetailUiState.Success -> {
                val detail = state.detail
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Spacing.md)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    // Hero: Target Hari Ini
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.lg)) {
                            Text(
                                text = "Target Hari Ini: Rp${formatTargetRupiah(detail.cache.targetAmount)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))
                            LinearProgressIndicator(
                                progress = { (detail.todayProgress / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                            )
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Profit hari ini: Rp${formatTargetRupiah(detail.todayProfit)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "${detail.todayProgress}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (detail.todayRemaining > 0) {
                                Text(
                                    text = "Kurang: Rp${formatTargetRupiah(detail.todayRemaining)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    text = "\u2705 Target tercapai! Lebih Rp${formatTargetRupiah(-detail.todayRemaining)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    // Urgent Deadlines
                    detail.urgentDeadlines.forEach { urgent ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(
                                    text = "\u26A0\uFE0F Kewajiban Mendekat",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${urgent.emoji} ${urgent.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Jatuh tempo: tgl ${urgent.dueDateDay}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "Butuh: Rp${formatTargetRupiah(urgent.amount)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    // Overdue Deadlines
                    detail.overdueDeadlines.forEach { overdue ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFFCDD2)
                            )
                        ) {
                            Text(
                                text = "\uD83D\uDD34 ${overdue.name} sudah lewat jatuh tempo!",
                                modifier = Modifier.padding(Spacing.md),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }

                    // Kewajiban Bulan Ini
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "Kewajiban Bulan Ini",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))

                            if (detail.debtObligations.isNotEmpty()) {
                                Text("Cicilan Hutang:", style = MaterialTheme.typography.labelMedium)
                                detail.debtObligations.forEach { item ->
                                    ObligationRow(item)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal Cicilan:", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Rp${formatTargetRupiah(detail.subtotalCicilan)}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(Spacing.sm))
                            }

                            if (detail.fixedExpenseObligations.isNotEmpty()) {
                                Text("Biaya Tetap:", style = MaterialTheme.typography.labelMedium)
                                detail.fixedExpenseObligations.forEach { item ->
                                    ObligationRow(item)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Subtotal Biaya Tetap:", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Rp${formatTargetRupiah(detail.subtotalBiayaTetap)}",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Total Kewajiban:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Rp${formatTargetRupiah(detail.cache.totalObligation)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Progress Bulan Ini
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(Spacing.md)) {
                            Text(
                                text = "Progress Bulan Ini",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(Spacing.sm))

                            ProgressRow("Profit terkumpul", detail.cache.profitAccumulated)
                            ProgressRow("Sudah bayar cicilan", -detail.cache.obligationPaid)
                            ProgressRow("Profit tersedia", detail.cache.profitAvailable)
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            ProgressRow("Sisa kewajiban", detail.cache.remainingObligation)
                            ProgressRow("Profit tersedia", -detail.cache.profitAvailable)

                            Spacer(modifier = Modifier.height(Spacing.sm))
                            val harusKumpul = (detail.cache.remainingObligation - detail.cache.profitAvailable).coerceAtLeast(0)
                            ProgressRow("Harus dikumpulkan", harusKumpul)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Sisa hari kerja")
                                Text("\u00F7 ${detail.cache.remainingWorkDays} hari")
                            }
                            Spacer(modifier = Modifier.height(Spacing.xs))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "= Target per hari:",
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Rp${formatTargetRupiah(detail.cache.targetAmount)}",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Jadwal Minggu Ini (from F009)
                    state.schedule?.let { schedule ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Text(
                                    text = "Jadwal Minggu Ini",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = schedule.thisWeekStart.takeLast(5) + " - " + schedule.thisWeekEnd.takeLast(5),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(Spacing.sm))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach {
                                        Text(it, style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.weight(1f))
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    schedule.thisWeek.forEach { day ->
                                        Text(
                                            text = if (day.isWorking) "\u2705" else "\u274C",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(Spacing.xs))
                                Text(
                                    text = "${schedule.workingDaysThisWeek} hari narik \u00B7 ${schedule.offDaysThisWeek} hari libur",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Mode Ambisius (from F009)
                    state.ambitiousMode?.let { ambitious ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(Spacing.md)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "\uD83D\uDE80 Mode Ambisius",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Switch(
                                        checked = ambitious.isActive,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                viewModel.activateAmbitiousMode(ambitious.targetMonths)
                                            } else {
                                                viewModel.deactivateAmbitiousMode()
                                            }
                                        }
                                    )
                                }

                                if (ambitious.isActive && ambitious.hasActiveDebts) {
                                    Spacer(modifier = Modifier.height(Spacing.sm))
                                    Text(
                                        text = "Total sisa hutang: Rp${formatTargetRupiah(ambitious.totalRemainingDebt)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Lunas dalam: ${ambitious.targetMonths} bulan",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(Spacing.xs))
                                    Text(
                                        text = "Cicilan normal: Rp${formatTargetRupiah(ambitious.normalInstallment)}/bln",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "Mode ambisius: Rp${formatTargetRupiah(ambitious.ambitiousInstallment)}/bln",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Tambahan/bulan: +Rp${formatTargetRupiah(ambitious.additionalPerMonth)}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFE65100)
                                    )
                                } else if (!ambitious.hasActiveDebts) {
                                    Text(
                                        text = "Tidak ada hutang aktif untuk dipercepat",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "Aktifkan untuk percepat pelunasan hutang",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.xl))
                }
            }
        }
    }
}

@Composable
private fun ObligationRow(item: ObligationItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${item.emoji} ${item.name}" + if (item.dueDateDay != null) " (tgl ${item.dueDateDay})" else "",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "Rp${formatTargetRupiah(item.amount)}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ProgressRow(label: String, amount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall)
        val prefix = if (amount < 0) "-" else ""
        Text(
            text = "${prefix}Rp${formatTargetRupiah(kotlin.math.abs(amount))}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatTargetRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
