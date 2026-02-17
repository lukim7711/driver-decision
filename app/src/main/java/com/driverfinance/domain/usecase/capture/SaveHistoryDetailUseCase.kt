package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.data.repository.CaptureRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SaveHistoryDetailUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Saves a history detail with duplicate detection by order_sn.
     */
    suspend operator fun invoke(
        historyTripId: String,
        orderSn: String,
        orderIdLong: String?,
        deliveryFee: Int,
        totalEarning: Int,
        bonusType: String?,
        bonusPoints: Int,
        cashCompensation: Int,
        cashCollected: Int,
        orderAdjustment: Int,
        timeAccepted: String?,
        timeArrived: String?,
        timePickedUp: String?,
        timeCompleted: String?,
        rawText: String,
        parseConfidence: Float,
        sourceSnapshotId: String
    ): SaveResult {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val existing = repository.getHistoryDetailByOrderSn(orderSn)
        if (existing != null) {
            val updated = existing.copy(
                deliveryFee = deliveryFee,
                totalEarning = totalEarning,
                bonusType = bonusType ?: existing.bonusType,
                bonusPoints = bonusPoints,
                cashCompensation = cashCompensation,
                cashCollected = cashCollected,
                orderAdjustment = orderAdjustment,
                timeAccepted = timeAccepted ?: existing.timeAccepted,
                timeArrived = timeArrived ?: existing.timeArrived,
                timePickedUp = timePickedUp ?: existing.timePickedUp,
                timeCompleted = timeCompleted ?: existing.timeCompleted,
                parseConfidence = parseConfidence
            )
            repository.updateHistoryDetail(updated)
            return SaveResult.Updated(existing.id)
        }

        val id = UUID.randomUUID().toString()
        val entity = HistoryDetailEntity(
            id = id,
            historyTripId = historyTripId,
            linkedOrderId = null,
            orderSn = orderSn,
            orderIdLong = orderIdLong,
            deliveryFee = deliveryFee,
            totalEarning = totalEarning,
            bonusType = bonusType,
            bonusPoints = bonusPoints,
            cashCompensation = cashCompensation,
            cashCollected = cashCollected,
            orderAdjustment = orderAdjustment,
            timeAccepted = timeAccepted,
            timeArrived = timeArrived,
            timePickedUp = timePickedUp,
            timeCompleted = timeCompleted,
            rawText = rawText,
            parseConfidence = parseConfidence,
            sourceSnapshotId = sourceSnapshotId,
            capturedAt = now,
            createdAt = now
        )
        repository.saveHistoryDetail(entity)
        return SaveResult.Created(id)
    }

    sealed interface SaveResult {
        data class Created(val id: String) : SaveResult
        data class Updated(val id: String) : SaveResult
    }
}
