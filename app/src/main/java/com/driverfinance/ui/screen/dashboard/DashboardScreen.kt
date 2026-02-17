package com.driverfinance.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.driverfinance.domain.model.DashboardData
import com.driverfinance.ui.screen.dashboard.components.DashboardHeader
import com.driverfinance.ui.screen.dashboard.components.DataWarningBanner
import com.driverfinance.ui.screen.dashboard.components.HeroTargetSection
import com.driverfinance.ui.screen.dashboard.components.OtherIncomeCard
import com.driverfinance.ui.screen.dashboard.components.ProfitSummaryCard
import com.driverfinance.ui.screen.dashboard.components.TripSummaryCard
import com.driverfinance.ui.navigation.Screen
import com.driverfinance.ui.theme.Spacing

/**
 * F005 — Dashboard Harian.
 * Glanceable: semua info terbaca 2 detik tanpa scroll.
 *
 * Sections: Header → Hero Target → Profit → Trip Summary → Other Income → Warning
 */
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        is DashboardUiState.Success -> {
            DashboardContent(
                data = state.data,
                onTripSummaryClick = {
                    navController.navigate(Screen.Order.route) {
                        popUpTo(Screen.Dashboard.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onTargetClick = { /* TODO: navigate to Screen.TargetDetail */ },
                onPendingReviewClick = { /* TODO: navigate to Screen.DataReview */ },
                onBellClick = { /* TODO: notification dropdown */ }
            )
        }

        is DashboardUiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Spacing.screenPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(Spacing.lg))
                Button(onClick = viewModel::loadDashboardData) {
                    Text("Coba Lagi")
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    data: DashboardData,
    onTripSummaryClick: () -> Unit,
    onTargetClick: () -> Unit,
    onPendingReviewClick: () -> Unit,
    onBellClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screenPadding)
    ) {
        Spacer(modifier = Modifier.height(Spacing.screenTopPadding))

        DashboardHeader(
            notificationCount = data.notificationCount,
            onBellClick = onBellClick
        )

        Spacer(modifier = Modifier.height(Spacing.xxl))

        HeroTargetSection(
            data = data,
            onClick = onTargetClick
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = Spacing.lg),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        ProfitSummaryCard(data = data)

        Spacer(modifier = Modifier.height(Spacing.lg))

        TripSummaryCard(
            data = data,
            onClick = onTripSummaryClick
        )

        if (data.hasOtherIncome) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            OtherIncomeCard(data = data)
        }

        if (data.hasPendingReviews) {
            Spacer(modifier = Modifier.height(Spacing.lg))
            DataWarningBanner(
                count = data.pendingReviewCount,
                onClick = onPendingReviewClick
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxxl))
    }
}
