package com.driverfinance.ui.screen.order

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.navigation.NavController
import com.driverfinance.ui.screen.order.components.CaptureStatusBanner
import com.driverfinance.ui.screen.order.components.DailySummaryRow
import com.driverfinance.ui.screen.order.components.EmptyOrderState
import com.driverfinance.ui.screen.order.components.EndShiftConfirmDialog
import com.driverfinance.ui.screen.order.components.HistoryReminderCard
import com.driverfinance.ui.screen.order.components.ShiftControlSection
import com.driverfinance.ui.screen.order.components.TripCard
import com.driverfinance.ui.screen.order.components.TripDetailSheet
import com.driverfinance.ui.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Tab Order — Shift management + daily trip/order list.
 *
 * F001 spec: Order capture with trip grouping.
 * F002 spec: History capture status per trip.
 *
 * Layout:
 * 1. Date header
 * 2. Shift control (Mulai/Akhiri)
 * 3. Capture status banner
 * 4. Daily summary (trips, orders, earning)
 * 5. History reminder (if trips missing detail)
 * 6. Trip list (expandable cards)
 */
@Composable
fun OrderScreen(
    navController: NavController,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val dateFmt = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id", "ID"))

    // End shift confirm dialog
    if (state.showEndShiftConfirm) {
        EndShiftConfirmDialog(
            onConfirm = viewModel::confirmEndShift,
            onDismiss = viewModel::dismissEndShiftConfirm
        )
    }

    // Trip detail bottom sheet
    state.selectedTrip?.let { trip ->
        TripDetailSheet(
            trip = trip,
            onDismiss = viewModel::clearSelectedTrip
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ══ 1. Date Header ══
        Text(
            text = state.date.format(dateFmt),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            )
        )

        // ══ 2. Shift Control ══
        ShiftControlSection(
            shift = state.shift,
            onStartShift = viewModel::startShift,
            onEndShift = viewModel::requestEndShift,
            onNewShift = viewModel::startNewShift,
            modifier = Modifier.padding(horizontal = Spacing.screenPadding)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // ══ 3. Capture Status Banner ══
        CaptureStatusBanner(
            capture = state.capture,
            modifier = Modifier.padding(horizontal = Spacing.screenPadding)
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // ══ 4. Daily Summary ══
        if (state.hasTodayData) {
            DailySummaryRow(
                tripCount = state.todayTrips,
                orderCount = state.todayOrders,
                totalEarning = state.todayEarning,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )

            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // ══ 5. History Reminder ══
        AnimatedVisibility(
            visible = state.showHistoryReminder,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HistoryReminderCard(
                tripsWithoutDetail = state.tripsWithoutDetail,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // ══ 6. Trip List / Empty State ══
        if (state.hasTodayData) {
            Text(
                text = "Trip Hari Ini",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = Spacing.screenPadding,
                    vertical = Spacing.xs
                )
            )

            state.trips.forEach { trip ->
                TripCard(
                    trip = trip,
                    onTripClick = { viewModel.selectTrip(trip.id) },
                    modifier = Modifier.padding(
                        horizontal = Spacing.screenPadding,
                        vertical = Spacing.xs
                    )
                )
            }
        } else {
            EmptyOrderState(
                shiftActive = state.shift.isActive,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}
