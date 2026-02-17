package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.local.entity.AiCorrectionLogEntity
import com.driverfinance.data.repository.ExtractionRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

/**
 * Submits a driver correction for a data review.
 * Updates the review status and logs the correction for AI learning (F003 §6.3.5).
 */
class SubmitCorrectionUseCase @Inject constructor(
    private val extractionRepository: ExtractionRepository
) {

    suspend operator fun invoke(
        reviewId: String,
        correctedValue: String?,
        action: CorrectionAction
    ): Boolean {
        val review = extractionRepository.getDataReviewById(reviewId) ?: return false
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val newStatus = when (action) {
            CorrectionAction.CONFIRM -> "CONFIRMED"
            CorrectionAction.CORRECT -> "CORRECTED"
            CorrectionAction.DISMISS -> "DISMISSED"
        }

        val updatedReview = review.copy(
            reviewStatus = newStatus,
            correctedValue = correctedValue ?: review.correctedValue,
            reviewedAt = now
        )
        extractionRepository.updateDataReview(updatedReview)

        if (action == CorrectionAction.CORRECT && correctedValue != null) {
            extractionRepository.saveAiCorrectionLog(
                AiCorrectionLogEntity(
                    id = UUID.randomUUID().toString(),
                    dataReviewId = reviewId,
                    patternId = null,
                    originalValue = review.originalValue,
                    correctedValue = correctedValue,
                    correctionType = inferCorrectionType(review.fieldName),
                    appliedToPattern = 0,
                    createdAt = now
                )
            )
        }

        return true
    }

    private fun inferCorrectionType(fieldName: String): String = when {
        fieldName.contains("amount", ignoreCase = true) ||
            fieldName.contains("earning", ignoreCase = true) ||
            fieldName.contains("fee", ignoreCase = true) -> "AMOUNT"
        fieldName.contains("address", ignoreCase = true) -> "ADDRESS"
        fieldName.contains("service", ignoreCase = true) -> "SERVICE_TYPE"
        fieldName.contains("order", ignoreCase = true) ||
            fieldName.contains("code", ignoreCase = true) -> "ORDER_CODE"
        fieldName.contains("time", ignoreCase = true) -> "TIMELINE"
        else -> "OTHER"
    }

    enum class CorrectionAction { CONFIRM, CORRECT, DISMISS }
}
