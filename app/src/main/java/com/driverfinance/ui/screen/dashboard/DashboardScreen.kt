package com.driverfinance.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DashboardScreen(
    onTargetDetailClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            Column(
                modifier = modifier.fillMaxSize().padding(Spacing.md),
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
        is DashboardUiState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Coba Lagi")
                    }
                }
            }
        }
        is DashboardUiState.Success -> {
            DashboardContent(
                state = state,
                onTargetDetailClick = onTargetDetailClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState.Success,
    onTargetDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val data = state.data
    val today = LocalDate.now().format(
        DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale("id", "ID"))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = today,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Hero Card: Target / Profit
        HeroCard(
            dailyTarget = state.dailyTarget,
            profit = data.profit,
            targetProgress = state.targetProgress,
            targetRemaining = state.targetRemaining,
            isTargetAchieved = state.isTargetAchieved,
            onDetailClick = onTargetDetailClick
        )

        // Profit Card
        ProfitCard(
            profit = data.profit,
            shopeeEarning = data.shopeeEarning,
            otherIncome = data.otherIncome,
            totalExpense = data.totalExpense
        )

        // Trip Summary
        TripSummaryCard(
            tripCount = data.tripCount,
            orderCount = state.orderCount,
            totalPoints = state.totalPoints,
            tripsByService = data.tripsByService
        )

        // Pemasukan Lain (only if exists)
        if (data.otherIncome > 0) {
            OtherIncomeCard(
                totalIncome = data.otherIncome,
                incomeCount = data.otherIncomeCount,
                items = data.otherIncomeItems
            )
        }

        // Data Ambigu Indicator
        if (state.pendingReviewCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "\u26A0\uFE0F ${state.pendingReviewCount} pesanan belum masuk hitungan",
                    modifier = Modifier.padding(Spacing.md),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun HeroCard(
    dailyTarget: Int?,
    profit: Int,
    targetProgress: Float,
    targetRemaining: Int,
    isTargetAchieved: Boolean,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onDetailClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            if (dailyTarget != null) {
                if (isTargetAchieved) {
                    Text(
                        text = "\uD83C\uDF89 Target Tercapai!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                    Text(
                        text = "Lebih Rp${formatRupiah(profit - dailyTarget)} dari target",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF2E7D32)
                    )
                } else {
                    Text(
                        text = "Kurang Rp${formatRupiah(targetRemaining)} lagi",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(Spacing.sm))
                LinearProgressIndicator(
                    progress = { targetProgress },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = if (isTargetAchieved) Color(0xFF2E7D32)
                        else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Target hari ini: Rp${formatRupiah(dailyTarget)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Profit Bersih",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rp${formatRupiah(profit)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (profit < 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Set target harian di Settings",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfitCard(
    profit: Int,
    shopeeEarning: Int,
    otherIncome: Int,
    totalExpense: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Profit Bersih",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Rp${formatRupiah(profit)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (profit < 0) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                )
            }
            val totalRevenue = shopeeEarning + otherIncome
            Text(
                text = "Pendapatan Rp${formatRupiah(totalRevenue)} \u2212 Keluar Rp${formatRupiah(totalExpense)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TripSummaryCard(
    tripCount: Int,
    orderCount: Int,
    totalPoints: Int,
    tripsByService: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "$tripCount trip \u00B7 $orderCount pesanan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (tripsByService.isNotEmpty()) {
                val breakdown = tripsByService.entries.joinToString(" \u00B7 ") {
                    "${it.value} ${it.key}"
                }
                Text(
                    text = breakdown,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$totalPoints poin hari ini",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun OtherIncomeCard(
    totalIncome: Int,
    incomeCount: Int,
    items: List<com.driverfinance.domain.usecase.dashboard.IncomeItem>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pemasukan Lain",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "Rp${formatRupiah(totalIncome)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            if (items.size <= 3) {
                items.forEach { item ->
                    Text(
                        text = "Rp${formatRupiah(item.amount)}${item.note?.let { " \u2014 $it" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Text(
                    text = "$incomeCount pemasukan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatRupiah(amount: Int): String {
    val abs = kotlin.math.abs(amount)
    val formatted = String.format("%,d", abs).replace(',', '.')
    return if (amount < 0) "-$formatted" else formatted
}
