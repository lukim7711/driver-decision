package com.driverfinance.domain.usecase.extraction

import com.driverfinance.data.local.entity.MatchedOrderEntity
import com.driverfinance.data.repository.CaptureRepository
import com.driverfinance.data.repository.ExtractionRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

/**
 * Matches F001 captured_orders with F002 history_details by order_sn.
 * Called per order_sn when new data arrives from either F001 or F002.
 * Creates/updates matched_orders entries and links history_details.
 */
class MatchOrdersUseCase @Inject constructor(
    private val captureRepository: CaptureRepository,
    private val extractionRepository: ExtractionRepository
) {

    suspend operator fun invoke(orderSn: String): MatchStatus {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val capturedOrder = captureRepository.getOrderByOrderSn(orderSn)
        val historyDetail = captureRepository.getHistoryDetailByOrderSn(orderSn)

        val capturedOrderId = capturedOrder?.id
        val historyDetailId = historyDetail?.id

        val status = when {
            capturedOrderId != null && historyDetailId != null -> STATUS_MATCHED
            capturedOrderId != null -> STATUS_F001_ONLY
            historyDetailId != null -> STATUS_F002_ONLY
            else -> return MatchStatus.NO_DATA
        }

        val confidence = if (status == STATUS_MATCHED) 1.0 else 0.0

        val existing = capturedOrderId?.let {
            extractionRepository.getMatchedOrderByCapturedOrderId(it)
        } ?: historyDetailId?.let {
            extractionRepository.getMatchedOrderByHistoryDetailId(it)
        }

        if (existing != null) {
            val updated = existing.copy(
                capturedOrderId = capturedOrderId ?: existing.capturedOrderId,
                historyDetailId = historyDetailId ?: existing.historyDetailId,
                matchStatus = status,
                matchConfidence = confidence,
                matchedAt = now
            )
            extractionRepository.updateMatchedOrder(updated)
        } else {
            extractionRepository.saveMatchedOrder(
                MatchedOrderEntity(
                    id = UUID.randomUUID().toString(),
                    capturedOrderId = capturedOrderId,
                    historyDetailId = historyDetailId,
                    matchStatus = status,
                    matchConfidence = confidence,
                    matchedAt = now,
                    createdAt = now
                )
            )
        }

        if (status == STATUS_MATCHED && historyDetail != null && capturedOrder != null) {
            captureRepository.updateHistoryDetail(
                historyDetail.copy(linkedOrderId = capturedOrder.id)
            )
        }

        return when (status) {
            STATUS_MATCHED -> MatchStatus.MATCHED
            STATUS_F001_ONLY -> MatchStatus.F001_ONLY
            STATUS_F002_ONLY -> MatchStatus.F002_ONLY
            else -> MatchStatus.NO_DATA
        }
    }

    enum class MatchStatus { MATCHED, F001_ONLY, F002_ONLY, NO_DATA }

    companion object {
        const val STATUS_MATCHED = "MATCHED"
        const val STATUS_F001_ONLY = "F001_ONLY"
        const val STATUS_F002_ONLY = "F002_ONLY"
    }
}
