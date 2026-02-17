package com.driverfinance.ui.screen.obligation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.driverfinance.ui.screen.obligation.components.WeekScheduleSection
import com.driverfinance.ui.theme.Spacing

/**
 * F009 — Work Schedule Screen.
 *
 * F009 spec mockup D: 2 weeks (this + next), 7-day toggles per week.
 * Accessed from "Lain" tab → "📅 Jadwal Kerja".
 */
@Composable
fun WorkScheduleScreen(
    onBackClick: () -> Unit,
    viewModel: WorkScheduleViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.screenPadding)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Jadwal Kerja",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(vertical = Spacing.md)
        )

        // This week
        state.thisWeek?.let { week ->
            WeekScheduleSection(
                week = week,
                onDayToggle = viewModel::toggleDay
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Next week
        state.nextWeek?.let { week ->
            WeekScheduleSection(
                week = week,
                onDayToggle = viewModel::toggleDay
            )
        }

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Info
        Text(
            text = "\u2139\uFE0F Jadwal mempengaruhi target harian di Dashboard",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Tap hari untuk toggle narik/libur",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}
