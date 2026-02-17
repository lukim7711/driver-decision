package com.driverfinance.ui.screen.capture

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.model.CaptureMode
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureManagerScreen(
    onBack: () -> Unit,
    onTripClick: (String) -> Unit,
    onHistoryTripClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaptureManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Order Masuk", "Riwayat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capture Manager") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (val state = uiState) {
                is CaptureManagerUiState.Loading -> {
                    SkeletonLoading()
                }
                is CaptureManagerUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                is CaptureManagerUiState.Success -> {
                    when (selectedTab) {
                        0 -> OrderMasukTab(
                            state = state,
                            onTripClick = onTripClick
                        )
                        1 -> RiwayatTab(
                            state = state,
                            onHistoryTripClick = onHistoryTripClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SkeletonLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        repeat(3) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) { }
        }
    }
}

@Composable
private fun OrderMasukTab(
    state: CaptureManagerUiState.Success,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item { StatusCard(mode = state.orderCaptureMode) }
        item {
            TodayStatsCard(
                emoji = "\uD83D\uDCE6",
                label = "order ter-capture",
                count = state.todayOrderCount,
                tripLabel = "trip",
                tripCount = state.trips.size
            )
        }
        if (state.trips.isNotEmpty()) {
            item {
                Text(
                    text = "Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.trips, key = { it.trip.id }) { tripWithCount ->
                TripCard(
                    tripWithCount = tripWithCount,
                    onClick = { onTripClick(tripWithCount.trip.id) }
                )
            }
        }
    }
}

@Composable
private fun RiwayatTab(
    state: CaptureManagerUiState.Success,
    onHistoryTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item { StatusCard(mode = state.historyCaptureMode) }
        item {
            TodayStatsCard(
                emoji = "\uD83D\uDCCB",
                label = "trip dari riwayat",
                count = state.historyTrips.size,
                tripLabel = "rincian lengkap",
                tripCount = state.historyTripsWithDetails
            )
        }
        if (state.historyTrips.isNotEmpty()) {
            item {
                Text(
                    text = "Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(state.historyTrips, key = { it.historyTrip.id }) { item ->
                HistoryTripCard(
                    item = item,
                    onClick = { onHistoryTripClick(item.historyTrip.id) }
                )
            }
        }
    }
}

@Composable
private fun StatusCard(mode: CaptureMode, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            when (mode) {
                is CaptureMode.Discovery -> {
                    Text(
                        text = "Status: \uD83D\uDFE1 Sedang Belajar",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text("App sedang mempelajari tampilan Shopee Driver kamu. Cukup kerja seperti biasa!")
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    LinearProgressIndicator(
                        progress = {
                            mode.samplesCollected.toFloat() / mode.samplesNeeded.coerceAtLeast(1)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "${mode.samplesCollected}/${mode.samplesNeeded} contoh",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is CaptureMode.Parsing -> {
                    Text(
                        text = "Status: \uD83D\uDFE2 Aktif (Auto Capture)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TodayStatsCard(
    emoji: String,
    label: String,
    count: Int,
    tripLabel: String,
    tripCount: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text("Hari ini:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(text = "$emoji $count $label", style = MaterialTheme.typography.bodyLarge)
            Text(text = "\u2705 $tripCount $tripLabel", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun TripCard(
    tripWithCount: TripWithOrderCount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val trip = tripWithCount.trip
    val timeDisplay = if (trip.capturedAt.length >= 16) trip.capturedAt.substring(11, 16) else ""

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Trip ${trip.tripCode ?: ""} \u2014 $timeDisplay",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${trip.serviceType} \u00B7 ${tripWithCount.orderCount} pesanan",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "\u2705 Semua pesanan ter-capture",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun HistoryTripCard(
    item: HistoryTripWithDetailCount,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ht = item.historyTrip
    val hasDetails = item.detailCount > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "\uD83D\uDCCB Trip ${ht.tripTime} \u2014 ${ht.serviceType}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pendapatan: Rp${formatRupiah(ht.totalEarning)}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (ht.isCombined == 1) {
                Text(
                    text = "Pesanan Gabungan",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = if (hasDetails) "\u2705 Rincian lengkap" else "\u26A0\uFE0F Rincian belum dibuka",
                style = MaterialTheme.typography.bodySmall,
                color = if (hasDetails) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun formatRupiah(amount: Int): String {
    return String.format("%,d", amount).replace(',', '.')
}
