package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.local.entity.DataReviewEntity
import com.driverfinance.data.repository.ExtractionRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

/**
 * Creates a data review entry based on confidence tier system (F003 §6.3.2).
 * - 85-100%: auto accepted, no review
 * - 60-84%: dashboard + review created
 * - 30-59%: dashboard (marked) + review in summary
 * - 0-29%: excluded from dashboard + review in summary
 */
class CreateDataReviewUseCase @Inject constructor(
    private val extractionRepository: ExtractionRepository
) {

    suspend operator fun invoke(
        sourceTable: String,
        sourceId: String,
        fieldName: String,
        originalValue: String,
        suggestedValue: String?,
        confidence: Double
    ): ReviewAction {
        if (confidence >= HIGH_CONFIDENCE) return ReviewAction.AUTO_ACCEPTED

        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val review = DataReviewEntity(
            id = UUID.randomUUID().toString(),
            sourceTable = sourceTable,
            sourceId = sourceId,
            fieldName = fieldName,
            originalValue = originalValue,
            suggestedValue = suggestedValue,
            correctedValue = null,
            confidence = confidence,
            reviewStatus = REVIEW_PENDING,
            reviewedAt = null,
            createdAt = now
        )
        extractionRepository.saveDataReview(review)

        return when {
            confidence >= MEDIUM_CONFIDENCE -> ReviewAction.DASHBOARD_WITH_REVIEW
            confidence >= LOW_CONFIDENCE -> ReviewAction.DASHBOARD_MARKED
            else -> ReviewAction.EXCLUDED
        }
    }

    enum class ReviewAction {
        AUTO_ACCEPTED,
        DASHBOARD_WITH_REVIEW,
        DASHBOARD_MARKED,
        EXCLUDED
    }

    companion object {
        const val HIGH_CONFIDENCE = 0.85
        const val MEDIUM_CONFIDENCE = 0.60
        const val LOW_CONFIDENCE = 0.30
        const val REVIEW_PENDING = "PENDING"
    }
}
