package com.driverfinance.domain.usecase.dashboard

import com.driverfinance.data.repository.ExtractionRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Counts data_reviews with confidence < 30% and status PENDING for today.
 * Used for the dashboard "X pesanan belum masuk hitungan" indicator.
 */
class GetPendingReviewCountUseCase @Inject constructor(
    private val extractionRepository: ExtractionRepository
) {

    suspend operator fun invoke(): Int {
        val today = LocalDate.now().toString()
        return extractionRepository.getTodayPendingCount(today)
    }
}
