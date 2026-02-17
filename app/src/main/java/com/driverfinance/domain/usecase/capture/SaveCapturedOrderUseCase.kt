package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.CapturedOrderEntity
import com.driverfinance.data.repository.CaptureRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SaveCapturedOrderUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Saves a captured order with duplicate detection.
     * If order_sn already exists, updates the existing record instead of creating a new one.
     */
    suspend operator fun invoke(
        tripId: String,
        orderSn: String,
        orderId: String?,
        pickupAddress: String?,
        deliveryAddress: String?,
        sellerName: String?,
        parcelWeight: String?,
        parcelDimensions: String?,
        paymentMethod: String?,
        paymentAmount: Int?,
        rawText: String,
        parseConfidence: Float,
        sourceSnapshotId: String
    ): SaveResult {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val existing = repository.getOrderByOrderSn(orderSn)

        if (existing != null) {
            val updated = existing.copy(
                pickupAddress = pickupAddress ?: existing.pickupAddress,
                deliveryAddress = deliveryAddress ?: existing.deliveryAddress,
                sellerName = sellerName ?: existing.sellerName,
                parcelWeight = parcelWeight ?: existing.parcelWeight,
                parcelDimensions = parcelDimensions ?: existing.parcelDimensions,
                paymentMethod = paymentMethod ?: existing.paymentMethod,
                paymentAmount = paymentAmount ?: existing.paymentAmount,
                parseConfidence = parseConfidence
            )
            repository.updateCapturedOrder(updated)
            return SaveResult.Updated(existing.id)
        }

        val id = UUID.randomUUID().toString()
        val order = CapturedOrderEntity(
            id = id,
            tripId = tripId,
            orderSn = orderSn,
            orderId = orderId,
            pickupAddress = pickupAddress,
            deliveryAddress = deliveryAddress,
            sellerName = sellerName,
            parcelWeight = parcelWeight,
            parcelDimensions = parcelDimensions,
            paymentMethod = paymentMethod,
            paymentAmount = paymentAmount,
            rawText = rawText,
            parseConfidence = parseConfidence,
            sourceSnapshotId = sourceSnapshotId,
            capturedAt = now,
            createdAt = now
        )
        repository.saveCapturedOrder(order)
        return SaveResult.Created(id)
    }

    sealed interface SaveResult {
        data class Created(val id: String) : SaveResult
        data class Updated(val id: String) : SaveResult
    }
}
