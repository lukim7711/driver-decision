package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.local.entity.DataReviewEntity
import com.driverfinance.data.repository.ExtractionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPendingReviewsUseCase @Inject constructor(
    private val extractionRepository: ExtractionRepository
) {

    operator fun invoke(): Flow<List<DataReviewEntity>> {
        return extractionRepository.getPendingReviews()
    }
}
