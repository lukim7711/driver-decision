package com.driverfinance.domain.usecase.target

import com.driverfinance.data.local.entity.AmbitiousModeEntity
import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.data.repository.AmbitiousModeRepository
import com.driverfinance.data.repository.DailyTargetRepository
import com.driverfinance.data.repository.DebtRepository
import com.driverfinance.data.repository.FixedExpenseRepository
import com.driverfinance.data.repository.QuickEntryRepository
import com.driverfinance.data.repository.WorkScheduleRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit tests for F007 CalculateDailyTargetUseCase.
 * Tests the deadline-aware daily target algorithm per F007 §6.3.
 *
 * Uses JUnit 5 + MockK per CONSTITUTION §7.
 */
class CalculateDailyTargetUseCaseTest {

    private lateinit var useCase: CalculateDailyTargetUseCase

    private val dailyTargetRepository: DailyTargetRepository = mockk(relaxed = true)
    private val debtRepository: DebtRepository = mockk()
    private val fixedExpenseRepository: FixedExpenseRepository = mockk()
    private val workScheduleRepository: WorkScheduleRepository = mockk()
    private val ambitiousModeRepository: AmbitiousModeRepository = mockk()
    private val quickEntryRepository: QuickEntryRepository = mockk()

    private val savedEntity = slot<DailyTargetCacheEntity>()

    @BeforeEach
    fun setup() {
        useCase = CalculateDailyTargetUseCase(
            dailyTargetRepository = dailyTargetRepository,
            debtRepository = debtRepository,
            fixedExpenseRepository = fixedExpenseRepository,
            workScheduleRepository = workScheduleRepository,
            ambitiousModeRepository = ambitiousModeRepository,
            quickEntryRepository = quickEntryRepository
        )

        // Default: no ambitious mode
        coEvery { ambitiousModeRepository.checkAndDeactivateAmbitiousMode() } returns false
        coEvery { ambitiousModeRepository.getAmbitiousModeSync() } returns null

        // Default: save cache captures entity
        coEvery { dailyTargetRepository.saveCache(capture(savedEntity)) } returns Unit
    }

    // ========== Helper ==========

    private fun setupDefaults(
        totalCicilan: Int = 1_000_000,
        totalBiayaTetap: Int = 250_000,
        monthlyIncome: Int = 500_000,
        monthlyExpense: Int = 100_000,
        nonExpensePayments: Int = 0,
        remainingWorkDays: Int = 10,
        totalRemainingDebt: Int = 0,
        ambitiousMode: AmbitiousModeEntity? = null
    ) {
        coEvery { debtRepository.getTotalMonthlyInstallment() } returns totalCicilan
        coEvery { fixedExpenseRepository.getTotalActive() } returns totalBiayaTetap
        coEvery { quickEntryRepository.getTodaySummary(any(), "INCOME") } returns monthlyIncome
        coEvery { quickEntryRepository.getTodaySummary(any(), "EXPENSE") } returns monthlyExpense
        coEvery { debtRepository.getNonExpensePaymentsForMonth(any()) } returns nonExpensePayments
        coEvery { workScheduleRepository.getRemainingWorkDays() } returns remainingWorkDays
        coEvery { debtRepository.getTotalRemainingAmount() } returns totalRemainingDebt
        coEvery { ambitiousModeRepository.getAmbitiousModeSync() } returns ambitiousMode
    }

    // ========== Tests ==========

    @Test
    @DisplayName("Basic: target = ceil(remainingObligation / remainingWorkDays)")
    fun basicCalculation() = runTest {
        // Obligation: 1,000,000 + 250,000 = 1,250,000
        // Profit: 500,000 - 100,000 = 400,000
        // Profit available: 400,000 - 0 = 400,000
        // Remaining obligation: 1,250,000 - 400,000 = 850,000
        // Target: ceil(850,000 / 10) = 85,000
        setupDefaults()

        val result = useCase()

        assertEquals(85_000, result.targetAmount)
        assertEquals(1_250_000, result.totalObligation)
        assertEquals(850_000, result.remainingObligation)
        assertEquals(400_000, result.profitAvailable)
        assertEquals(10, result.remainingWorkDays)

        coVerify(exactly = 1) { dailyTargetRepository.saveCache(any()) }
    }

    @Test
    @DisplayName("No obligation: status = NO_OBLIGATION, target = 0")
    fun noObligation() = runTest {
        setupDefaults(totalCicilan = 0, totalBiayaTetap = 0)

        val result = useCase()

        assertEquals(0, result.targetAmount)
        assertEquals("NO_OBLIGATION", result.status)
        assertEquals(0, result.totalObligation)
    }

    @Test
    @DisplayName("All obligations met: profit > obligation → ACHIEVED, target = 0")
    fun allObligationsMet() = runTest {
        // Obligation: 500,000 + 100,000 = 600,000
        // Profit: 800,000 - 50,000 = 750,000
        // Remaining obligation: max(600,000 - 750,000, 0) = 0
        setupDefaults(
            totalCicilan = 500_000,
            totalBiayaTetap = 100_000,
            monthlyIncome = 800_000,
            monthlyExpense = 50_000
        )

        val result = useCase()

        assertEquals(0, result.targetAmount)
        assertEquals("ACHIEVED", result.status)
    }

    @Test
    @DisplayName("Ambitious mode: uses max(ambitious, normal) + fixed expenses")
    fun ambitiousModeHigherThanNormal() = runTest {
        // Normal cicilan: 500,000
        // Remaining debt: 6,000,000 / 3 months = 2,000,000 ambitious cicilan
        // max(2,000,000, 500,000) = 2,000,000
        // Total obligation: 2,000,000 + 250,000 = 2,250,000
        // Profit available: 500,000 - 100,000 = 400,000
        // Remaining: 2,250,000 - 400,000 = 1,850,000
        // Target: ceil(1,850,000 / 10) = 185,000
        val ambitious = AmbitiousModeEntity(
            id = "test",
            isActive = 1,
            targetMonths = 3,
            activatedAt = "2026-02-01T00:00:00+07:00",
            deactivatedReason = null,
            updatedAt = "2026-02-01T00:00:00+07:00"
        )
        setupDefaults(
            totalCicilan = 500_000,
            totalRemainingDebt = 6_000_000,
            ambitiousMode = ambitious
        )

        val result = useCase()

        assertEquals(2_250_000, result.totalObligation)
        assertEquals(185_000, result.targetAmount)
    }

    @Test
    @DisplayName("Ambitious mode: normal cicilan higher than ambitious → uses normal")
    fun ambitiousModeNormalHigher() = runTest {
        // Normal cicilan: 1,000,000
        // Remaining debt: 2,000,000 / 6 months = 333,334 (ceil)
        // max(333,334, 1,000,000) = 1,000,000
        // Total obligation: 1,000,000 + 250,000 = 1,250,000 (same as no ambitious)
        val ambitious = AmbitiousModeEntity(
            id = "test",
            isActive = 1,
            targetMonths = 6,
            activatedAt = "2026-02-01T00:00:00+07:00",
            deactivatedReason = null,
            updatedAt = "2026-02-01T00:00:00+07:00"
        )
        setupDefaults(
            totalCicilan = 1_000_000,
            totalRemainingDebt = 2_000_000,
            ambitiousMode = ambitious
        )

        val result = useCase()

        assertEquals(1_250_000, result.totalObligation)
    }

    @Test
    @DisplayName("Zero work days remaining: target = 0 (no divide-by-zero)")
    fun zeroWorkDays() = runTest {
        setupDefaults(remainingWorkDays = 0)

        val result = useCase()

        assertEquals(0, result.targetAmount)
    }

    @Test
    @DisplayName("ON_TRACK: profit available >= 70% of total obligation")
    fun onTrackStatus() = runTest {
        // Obligation: 1,000,000 + 0 = 1,000,000
        // 70% = 700,000
        // Profit: 800,000 - 50,000 = 750,000 (>= 700,000)
        // Remaining: 1,000,000 - 750,000 = 250,000
        // Target: ceil(250,000 / 10) = 25,000 (> 0 so not ACHIEVED)
        setupDefaults(
            totalCicilan = 1_000_000,
            totalBiayaTetap = 0,
            monthlyIncome = 800_000,
            monthlyExpense = 50_000
        )

        val result = useCase()

        assertEquals("ON_TRACK", result.status)
        assertEquals(25_000, result.targetAmount)
    }

    @Test
    @DisplayName("BEHIND: profit available < 70% of total obligation")
    fun behindStatus() = runTest {
        // Obligation: 1,000,000 + 250,000 = 1,250,000
        // 70% = 875,000
        // Profit: 500,000 - 100,000 = 400,000 (< 875,000)
        setupDefaults()

        val result = useCase()

        assertEquals("BEHIND", result.status)
    }

    @Test
    @DisplayName("Non-expense payments reduce profit available")
    fun nonExpensePaymentsReduceProfit() = runTest {
        // Profit accumulated: 500,000 - 100,000 = 400,000
        // Non-expense payments: 200,000
        // Profit available: 400,000 - 200,000 = 200,000
        // Obligation: 1,250,000
        // Remaining: 1,250,000 - 200,000 = 1,050,000
        // Target: ceil(1,050,000 / 10) = 105,000
        setupDefaults(nonExpensePayments = 200_000)

        val result = useCase()

        assertEquals(200_000, result.profitAvailable)
        assertEquals(1_050_000, result.remainingObligation)
        assertEquals(105_000, result.targetAmount)
    }

    @Test
    @DisplayName("Negative profit: expenses > income → remaining obligation = full amount")
    fun negativeProfit() = runTest {
        // Profit: 100,000 - 300,000 = -200,000
        // Profit available: -200,000 - 0 = -200,000
        // Remaining: max(1,250,000 - max(-200,000, 0), 0) = max(1,250,000 - 0, 0) = 1,250,000
        // Target: ceil(1,250,000 / 10) = 125,000
        setupDefaults(
            monthlyIncome = 100_000,
            monthlyExpense = 300_000
        )

        val result = useCase()

        assertEquals(-200_000, result.profitAccumulated)
        assertEquals(-200_000, result.profitAvailable)
        assertEquals(1_250_000, result.remainingObligation)
        assertEquals(125_000, result.targetAmount)
    }
}
