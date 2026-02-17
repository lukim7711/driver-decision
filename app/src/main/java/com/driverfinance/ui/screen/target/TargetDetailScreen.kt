package com.driverfinance.ui.screen.target

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
import com.driverfinance.ui.screen.obligation.components.AmbitiousModeSection
import com.driverfinance.ui.screen.obligation.components.WeekScheduleSection
import com.driverfinance.ui.screen.target.components.CalculationBreakdownSection
import com.driverfinance.ui.screen.target.components.DeadlineWarningCard
import com.driverfinance.ui.screen.target.components.HeroTargetSection
import com.driverfinance.ui.screen.target.components.NoWorkDaysWarning
import com.driverfinance.ui.screen.target.components.ObligationBreakdownSection
import com.driverfinance.ui.theme.Spacing

/**
 * F007 — Target Detail Screen.
 *
 * Full detail view of daily target calculation.
 * F007 spec mockup B: hero + obligation breakdown + progress +
 * deadline warnings + embedded F009 schedule + ambitious mode.
 *
 * Accessed by tapping hero number on Dashboard (F005).
 */
@Composable
fun TargetDetailScreen(
    onBackClick: () -> Unit,
    viewModel: TargetDetailViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Target Harian",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                horizontal = Spacing.screenPadding,
                vertical = Spacing.md
            )
        )

        // ══ 1. Hero Section ══
        HeroTargetSection(
            hero = state.hero,
            modifier = Modifier.padding(horizontal = Spacing.screenPadding)
        )

        Spacer(modifier = Modifier.height(Spacing.xl))

        // ══ 2. Obligation Breakdown ══
        if (state.hasObligations) {
            ObligationBreakdownSection(
                debtInstallments = state.debtInstallments,
                fixedExpenses = state.fixedExpenses,
                subtotalDebt = state.subtotalDebt,
                subtotalFixedExpense = state.subtotalFixedExpense,
                totalObligation = state.totalObligation,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )

            Spacer(modifier = Modifier.height(Spacing.xl))

            // ══ 3. Progress & Calculation Breakdown ══
            CalculationBreakdownSection(
                profitAccumulated = state.profitAccumulated,
                debtPaymentsMade = state.debtPaymentsMade,
                profitAvailable = state.profitAvailable,
                remainingObligation = state.remainingObligation,
                amountToCollect = state.amountToCollect,
                remainingWorkDays = state.remainingWorkDays,
                targetPerDay = state.targetPerDay,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )

            Spacer(modifier = Modifier.height(Spacing.lg))
        }

        // ══ 4. Deadline Warnings ══
        if (state.hasDeadlineWarnings) {
            state.deadlineWarnings.forEach { warning ->
                DeadlineWarningCard(
                    warning = warning,
                    modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                )
                Spacer(modifier = Modifier.height(Spacing.sm))
            }
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // ══ 4b. No Work Days Warning ══
        if (state.noWorkDaysWarning) {
            NoWorkDaysWarning(
                remainingObligation = state.amountToCollect,
                modifier = Modifier.padding(horizontal = Spacing.screenPadding)
            )
            Spacer(modifier = Modifier.height(Spacing.md))
        }

        // ══ 5. Embedded F009: Jadwal Minggu Ini ══
        state.workScheduleThisWeek?.let { week ->
            Column(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
                WeekScheduleSection(
                    week = week,
                    onDayToggle = viewModel::toggleWorkDay
                )
            }
            Spacer(modifier = Modifier.height(Spacing.xl))
        }

        // ══ 6. Embedded F009: Mode Ambisius ══
        Column(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
            AmbitiousModeSection(
                state = state.ambitiousModeState,
                onToggle = viewModel::toggleAmbitiousMode,
                onSelectPresetMonths = viewModel::selectAmbitiousPresetMonths,
                onCustomMonthsChange = viewModel::updateAmbitiousCustomMonths
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xxl))
    }
}
