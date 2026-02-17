package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.AmbitiousModeDao
import com.driverfinance.data.local.entity.AmbitiousModeEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for F009 AmbitiousModeRepository.
 * Tests auto-off logic per F009 §6.3.7 + F006 §6.3.11.
 *
 * Key behavior:
 *   - Ambitious mode auto-deactivates when all debts are paid off
 *   - Cannot activate if no active debts
 *   - Manual deactivate sets reason = MANUAL
 *
 * Uses JUnit 5 + MockK per CONSTITUTION §7.
 */
class AmbitiousModeRepositoryTest {

    private lateinit var repository: AmbitiousModeRepository
    private val dao: AmbitiousModeDao = mockk(relaxed = true)
    private val debtRepository: DebtRepository = mockk()
    private val upsertedEntity = slot<AmbitiousModeEntity>()

    private val activeMode = AmbitiousModeEntity(
        id = "mode-1",
        isActive = 1,
        targetMonths = 6,
        activatedAt = "2026-02-01T00:00:00+07:00",
        deactivatedReason = null,
        updatedAt = "2026-02-01T00:00:00+07:00"
    )

    private val inactiveMode = activeMode.copy(
        isActive = 0,
        deactivatedReason = "MANUAL"
    )

    @BeforeEach
    fun setup() {
        repository = AmbitiousModeRepository(dao, debtRepository)
        coEvery { dao.upsert(capture(upsertedEntity)) } returns Unit
    }

    // ========== checkAndDeactivateAmbitiousMode ==========

    @Nested
    @DisplayName("checkAndDeactivateAmbitiousMode")
    inner class CheckAndDeactivate {

        @Test
        @DisplayName("Active mode + 0 active debts → auto deactivate, returns true")
        fun autoDeactivateWhenNoDebts() = runTest {
            coEvery { dao.get() } returns activeMode
            coEvery { debtRepository.getActiveDebtCount() } returns 0

            val result = repository.checkAndDeactivateAmbitiousMode()

            assertTrue(result)
            coVerify(exactly = 1) { dao.upsert(any()) }
            assertEquals(0, upsertedEntity.captured.isActive)
            assertEquals("AUTO_ALL_PAID_OFF", upsertedEntity.captured.deactivatedReason)
        }

        @Test
        @DisplayName("Active mode + active debts exist → stays active, returns false")
        fun staysActiveWithDebts() = runTest {
            coEvery { dao.get() } returns activeMode
            coEvery { debtRepository.getActiveDebtCount() } returns 3

            val result = repository.checkAndDeactivateAmbitiousMode()

            assertFalse(result)
            coVerify(exactly = 0) { dao.upsert(any()) }
        }

        @Test
        @DisplayName("Already inactive → no change, returns false")
        fun alreadyInactive() = runTest {
            coEvery { dao.get() } returns inactiveMode

            val result = repository.checkAndDeactivateAmbitiousMode()

            assertFalse(result)
            coVerify(exactly = 0) { dao.upsert(any()) }
        }

        @Test
        @DisplayName("No mode entity in DB → returns false")
        fun noModeEntity() = runTest {
            coEvery { dao.get() } returns null

            val result = repository.checkAndDeactivateAmbitiousMode()

            assertFalse(result)
            coVerify(exactly = 0) { dao.upsert(any()) }
        }
    }

    // ========== activate ==========

    @Nested
    @DisplayName("activate")
    inner class Activate {

        @Test
        @DisplayName("Activate with active debts → Success")
        fun activateSuccess() = runTest {
            coEvery { debtRepository.getActiveDebtCount() } returns 2
            coEvery { dao.get() } returns null

            val result = repository.activate(targetMonths = 4)

            assertEquals(AmbitiousModeRepository.ActivateResult.Success, result)
            coVerify(exactly = 1) { dao.upsert(any()) }
            assertEquals(1, upsertedEntity.captured.isActive)
            assertEquals(4, upsertedEntity.captured.targetMonths)
        }

        @Test
        @DisplayName("Activate with no active debts → NoActiveDebts")
        fun activateNoDebts() = runTest {
            coEvery { debtRepository.getActiveDebtCount() } returns 0

            val result = repository.activate(targetMonths = 6)

            assertEquals(AmbitiousModeRepository.ActivateResult.NoActiveDebts, result)
            coVerify(exactly = 0) { dao.upsert(any()) }
        }

        @Test
        @DisplayName("Activate updates existing inactive entity")
        fun activateUpdatesExisting() = runTest {
            coEvery { debtRepository.getActiveDebtCount() } returns 1
            coEvery { dao.get() } returns inactiveMode

            val result = repository.activate(targetMonths = 3)

            assertEquals(AmbitiousModeRepository.ActivateResult.Success, result)
            coVerify(exactly = 1) { dao.upsert(any()) }
            assertEquals(1, upsertedEntity.captured.isActive)
            assertEquals(3, upsertedEntity.captured.targetMonths)
            assertEquals("mode-1", upsertedEntity.captured.id) // Same entity updated
        }
    }

    // ========== deactivateManual ==========

    @Nested
    @DisplayName("deactivateManual")
    inner class DeactivateManual {

        @Test
        @DisplayName("Deactivate sets isActive=0, reason=MANUAL")
        fun deactivateManual() = runTest {
            coEvery { dao.get() } returns activeMode

            repository.deactivateManual()

            coVerify(exactly = 1) { dao.upsert(any()) }
            assertEquals(0, upsertedEntity.captured.isActive)
            assertEquals("MANUAL", upsertedEntity.captured.deactivatedReason)
        }

        @Test
        @DisplayName("Deactivate with no entity → no-op")
        fun deactivateNoEntity() = runTest {
            coEvery { dao.get() } returns null

            repository.deactivateManual()

            coVerify(exactly = 0) { dao.upsert(any()) }
        }
    }
}
