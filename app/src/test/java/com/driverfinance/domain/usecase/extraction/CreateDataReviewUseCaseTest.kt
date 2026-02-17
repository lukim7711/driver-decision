package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.local.entity.DataReviewEntity
import com.driverfinance.data.repository.ExtractionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Unit tests for F003 CreateDataReviewUseCase.
 * Tests the confidence tier system per F003 §6.3.2.
 *
 * Thresholds:
 *   85-100% → AUTO_ACCEPTED (no review saved)
 *   60-84%  → DASHBOARD_WITH_REVIEW
 *   30-59%  → DASHBOARD_MARKED
 *   0-29%   → EXCLUDED
 *
 * Uses JUnit 5 + MockK per CONSTITUTION §7.
 */
class CreateDataReviewUseCaseTest {

    private lateinit var useCase: CreateDataReviewUseCase
    private val extractionRepository: ExtractionRepository = mockk(relaxed = true)
    private val savedReview = slot<DataReviewEntity>()

    @BeforeEach
    fun setup() {
        useCase = CreateDataReviewUseCase(extractionRepository)
        coEvery { extractionRepository.saveDataReview(capture(savedReview)) } returns Unit
    }

    // ========== Helper ==========

    private suspend fun invokeWith(confidence: Double): CreateDataReviewUseCase.ReviewAction {
        return useCase(
            sourceTable = "history_trips",
            sourceId = "trip-123",
            fieldName = "total_earning",
            originalValue = "45000",
            suggestedValue = "46000",
            confidence = confidence
        )
    }

    // ========== HIGH Confidence (>= 85%) ==========

    @Nested
    @DisplayName("HIGH confidence tier (≥ 85%)")
    inner class HighConfidence {

        @Test
        @DisplayName("Confidence 95% → AUTO_ACCEPTED, no review saved")
        fun confidence95() = runTest {
            val result = invokeWith(0.95)

            assertEquals(CreateDataReviewUseCase.ReviewAction.AUTO_ACCEPTED, result)
            coVerify(exactly = 0) { extractionRepository.saveDataReview(any()) }
        }

        @Test
        @DisplayName("Boundary: exactly 85% → AUTO_ACCEPTED")
        fun boundaryExactly85() = runTest {
            val result = invokeWith(0.85)

            assertEquals(CreateDataReviewUseCase.ReviewAction.AUTO_ACCEPTED, result)
            coVerify(exactly = 0) { extractionRepository.saveDataReview(any()) }
        }

        @Test
        @DisplayName("Confidence 100% → AUTO_ACCEPTED")
        fun confidence100() = runTest {
            val result = invokeWith(1.0)

            assertEquals(CreateDataReviewUseCase.ReviewAction.AUTO_ACCEPTED, result)
            coVerify(exactly = 0) { extractionRepository.saveDataReview(any()) }
        }
    }

    // ========== MEDIUM Confidence (60-84%) ==========

    @Nested
    @DisplayName("MEDIUM confidence tier (60-84%)")
    inner class MediumConfidence {

        @Test
        @DisplayName("Confidence 75% → DASHBOARD_WITH_REVIEW, review saved")
        fun confidence75() = runTest {
            val result = invokeWith(0.75)

            assertEquals(CreateDataReviewUseCase.ReviewAction.DASHBOARD_WITH_REVIEW, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
            assertEquals("PENDING", savedReview.captured.reviewStatus)
            assertEquals(0.75, savedReview.captured.confidence)
        }

        @Test
        @DisplayName("Boundary: exactly 60% → DASHBOARD_WITH_REVIEW")
        fun boundaryExactly60() = runTest {
            val result = invokeWith(0.60)

            assertEquals(CreateDataReviewUseCase.ReviewAction.DASHBOARD_WITH_REVIEW, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
        }
    }

    // ========== LOW Confidence (30-59%) ==========

    @Nested
    @DisplayName("LOW confidence tier (30-59%)")
    inner class LowConfidence {

        @Test
        @DisplayName("Confidence 45% → DASHBOARD_MARKED, review saved")
        fun confidence45() = runTest {
            val result = invokeWith(0.45)

            assertEquals(CreateDataReviewUseCase.ReviewAction.DASHBOARD_MARKED, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
        }

        @Test
        @DisplayName("Boundary: exactly 30% → DASHBOARD_MARKED")
        fun boundaryExactly30() = runTest {
            val result = invokeWith(0.30)

            assertEquals(CreateDataReviewUseCase.ReviewAction.DASHBOARD_MARKED, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
        }
    }

    // ========== EXCLUDED (< 30%) ==========

    @Nested
    @DisplayName("EXCLUDED tier (< 30%)")
    inner class Excluded {

        @Test
        @DisplayName("Confidence 20% → EXCLUDED, review saved")
        fun confidence20() = runTest {
            val result = invokeWith(0.20)

            assertEquals(CreateDataReviewUseCase.ReviewAction.EXCLUDED, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
        }

        @Test
        @DisplayName("Boundary: 29% → EXCLUDED (just below LOW threshold)")
        fun boundaryJustBelow30() = runTest {
            val result = invokeWith(0.29)

            assertEquals(CreateDataReviewUseCase.ReviewAction.EXCLUDED, result)
            coVerify(exactly = 1) { extractionRepository.saveDataReview(any()) }
        }
    }
}
