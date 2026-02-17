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
import com.driverfinance.domain.model.CaptureMode
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureManagerScreen(
    onBack: () -> Unit,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CaptureManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        when (val state = uiState) {
            is CaptureManagerUiState.Loading -> {
                SkeletonLoading(modifier = Modifier.padding(innerPadding))
            }
            is CaptureManagerUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
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
                CaptureManagerContent(
                    state = state,
                    onTripClick = onTripClick,
                    modifier = Modifier.padding(innerPadding)
                )
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
private fun CaptureManagerContent(
    state: CaptureManagerUiState.Success,
    onTripClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        item { StatusCard(mode = state.mode) }

        item {
            TodayStatsCard(
                orderCount = state.todayOrderCount,
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
    orderCount: Int,
    tripCount: Int,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text("Hari ini:", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "\uD83D\uDCE6 $orderCount order ter-capture",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "\uD83D\uDE97 $tripCount trip",
                style = MaterialTheme.typography.bodyLarge
            )
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
    val timeDisplay = if (trip.capturedAt.length >= 16) {
        trip.capturedAt.substring(11, 16)
    } else {
        ""
    }

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
