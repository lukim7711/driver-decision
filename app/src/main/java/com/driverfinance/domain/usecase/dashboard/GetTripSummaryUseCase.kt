package com.driverfinance.domain.usecase.dashboard

import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.data.repository.CaptureRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Gets detailed trip summary including order count and bonus points
 * from history_details (F002). Supplements GetDashboardDataUseCase.
 */
class GetTripSummaryUseCase @Inject constructor(
    private val captureRepository: CaptureRepository
) {

    suspend operator fun invoke(): TripSummary {
        val today = LocalDate.now().toString()
        val historyTrips = captureRepository.getTodayHistoryTrips(today).first()

        var totalOrders = 0
        var totalPoints = 0

        historyTrips.forEach { trip ->
            val detailCount = captureRepository.getDetailsCountByHistoryTripId(trip.id)
            totalOrders += detailCount

            val details = captureRepository.getDetailsByHistoryTripId(trip.id).first()
            totalPoints += details.sumOf { it.bonusPoints }
        }

        return TripSummary(
            orderCount = totalOrders,
            totalPoints = totalPoints
        )
    }

    data class TripSummary(
        val orderCount: Int,
        val totalPoints: Int
    )
}
