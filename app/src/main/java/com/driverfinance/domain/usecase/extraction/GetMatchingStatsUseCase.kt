package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.repository.ExtractionRepository
import javax.inject.Inject

class GetMatchingStatsUseCase @Inject constructor(
    private val extractionRepository: ExtractionRepository
) {

    suspend operator fun invoke(): MatchingStats {
        val matched = extractionRepository.getMatchedOrdersByStatus("MATCHED").size
        val f001Only = extractionRepository.getMatchedOrdersByStatus("F001_ONLY").size
        val f002Only = extractionRepository.getMatchedOrdersByStatus("F002_ONLY").size
        val mismatch = extractionRepository.getMatchedOrdersByStatus("MISMATCH").size

        return MatchingStats(
            matched = matched,
            f001Only = f001Only,
            f002Only = f002Only,
            mismatch = mismatch
        )
    }

    data class MatchingStats(
        val matched: Int,
        val f001Only: Int,
        val f002Only: Int,
        val mismatch: Int
    )
}
