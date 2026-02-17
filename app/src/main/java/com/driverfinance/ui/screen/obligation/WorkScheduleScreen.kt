package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.domain.usecase.obligation.DaySchedule
import com.driverfinance.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkScheduleScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kerja") },
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
            is WorkScheduleUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    repeat(2) {
                        Card(modifier = Modifier.fillMaxWidth().height(120.dp)) { }
                    }
                }
            }
            is WorkScheduleUiState.Success -> {
                val data = state.data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(Spacing.md)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg)
                ) {
                    WeekSection(
                        title = "Minggu Ini",
                        subtitle = "${data.thisWeekStart.takeLast(5)} - ${data.thisWeekEnd.takeLast(5)}",
                        days = data.thisWeek,
                        workingDays = data.workingDaysThisWeek,
                        offDays = data.offDaysThisWeek,
                        onToggle = viewModel::toggleDay
                    )

                    WeekSection(
                        title = "Minggu Depan",
                        subtitle = "${data.nextWeekStart.takeLast(5)} - ${data.nextWeekEnd.takeLast(5)}",
                        days = data.nextWeek,
                        workingDays = data.workingDaysNextWeek,
                        offDays = data.offDaysNextWeek,
                        onToggle = viewModel::toggleDay
                    )

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "\u2139\uFE0F Jadwal mempengaruhi target harian di Dashboard",
                            modifier = Modifier.padding(Spacing.md),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeekSection(
    title: String,
    subtitle: String,
    days: List<DaySchedule>,
    workingDays: Int,
    offDays: Int,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Day labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Min", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xs))

            // Day toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                days.forEach { day ->
                    DayToggle(
                        day = day,
                        onClick = { onToggle(day.date) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "$workingDays hari narik \u00B7 $offDays hari libur",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap hari untuk toggle narik/libur",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayToggle(
    day: DaySchedule,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        day.isToday && day.isWorking -> Color(0xFF2E7D32)
        day.isToday && !day.isWorking -> Color(0xFFD32F2F)
        day.isWorking -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }
    val textColor = when {
        day.isToday -> Color.White
        day.isWorking -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onErrorContainer
    }
    val emoji = if (day.isWorking) "\u2705" else "\u274C"

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .size(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
    }
}
