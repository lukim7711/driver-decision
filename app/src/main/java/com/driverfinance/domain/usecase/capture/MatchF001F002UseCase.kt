package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.repository.CaptureRepository
import javax.inject.Inject

class MatchF001F002UseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Matches F002 history_details with F001 captured_orders by order_sn.
     * Updates linked_order_id in history_details when a match is found.
     * Returns the number of newly matched records.
     */
    suspend operator fun invoke(historyTripId: String): Int {
        val details = repository.getDetailsByHistoryTripIdOnce(historyTripId)
        var matchCount = 0

        details.forEach { detail ->
            if (detail.linkedOrderId != null) return@forEach

            val capturedOrder = repository.getOrderByOrderSn(detail.orderSn)
            if (capturedOrder != null) {
                repository.updateHistoryDetail(
                    detail.copy(linkedOrderId = capturedOrder.id)
                )
                matchCount++
            }
        }

        return matchCount
    }
}
