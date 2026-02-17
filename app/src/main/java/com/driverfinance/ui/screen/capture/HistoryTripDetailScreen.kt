package com.driverfinance.ui.screen.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTripDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryTripDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Riwayat Trip") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        when (val state = uiState) {
            is HistoryTripDetailUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(3) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) { }
                    }
                }
            }
            is HistoryTripDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is HistoryTripDetailUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    if (state.details.isNotEmpty()) {
                        val firstDetail = state.details.first()
                        item { EarningsSummaryCard(state.details) }

                        firstDetail.bonusType?.let { bonus ->
                            item {
                                BonusCard(
                                    bonusType = bonus,
                                    bonusPoints = firstDetail.bonusPoints
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Per Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(state.details, key = { it.id }) { detail ->
                        HistoryDetailCard(detail = detail)
                    }
                }
            }
        }
    }
}

@Composable
private fun EarningsSummaryCard(
    details: List<HistoryDetailEntity>,
    modifier: Modifier = Modifier
) {
    val totalDeliveryFee = details.sumOf { it.deliveryFee }
    val totalEarning = details.sumOf { it.totalEarning }

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Rangkuman Pendapatan",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            EarningsRow("Biaya Pengantaran", totalDeliveryFee)
            EarningsRow("Total Pendapatan", totalEarning)
        }
    }
}

@Composable
private fun EarningsRow(label: String, amount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "Rp${formatRupiah(amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BonusCard(
    bonusType: String,
    bonusPoints: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Insentif",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = bonusType, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "$bonusPoints Poin",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HistoryDetailCard(
    detail: HistoryDetailEntity,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Pesanan #${detail.orderSn}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.xs))

            if (detail.cashCollected > 0 || detail.cashCompensation > 0) {
                EarningsRow("Tagih Tunai", detail.cashCollected)
                EarningsRow("Kompensasi", detail.cashCompensation)
            }
            if (detail.orderAdjustment != 0) {
                EarningsRow("Penyesuaian", detail.orderAdjustment)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = Spacing.xs))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TimelineItem("Diterima", detail.timeAccepted)
                TimelineItem("Tiba", detail.timeArrived)
                TimelineItem("Diambil", detail.timePickedUp)
                TimelineItem("Selesai", detail.timeCompleted)
            }

            Spacer(modifier = Modifier.height(Spacing.xs))
            val matchStatus = if (detail.linkedOrderId != null) {
                "\uD83D\uDD17 Cocok dengan F001 \u2705"
            } else {
                "\u26A0\uFE0F Belum tercocokkan"
            }
            Text(
                text = matchStatus,
                style = MaterialTheme.typography.bodySmall,
                color = if (detail.linkedOrderId != null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun TimelineItem(label: String, time: String?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = time ?: "--:--",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
