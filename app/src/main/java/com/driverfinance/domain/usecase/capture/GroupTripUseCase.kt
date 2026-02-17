package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.TripEntity
import com.driverfinance.data.repository.CaptureRepository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class GroupTripUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Groups orders into trips using a time-window approach.
     * If an active trip exists within [GROUPING_WINDOW_MINUTES] minutes, returns that trip ID.
     * Otherwise creates a new trip.
     */
    suspend operator fun invoke(
        serviceType: String,
        snapshotId: String
    ): String {
        val now = OffsetDateTime.now()
        val today = LocalDate.now().toString()
        val cutoffTime = now.minusMinutes(GROUPING_WINDOW_MINUTES)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val activeTrip = repository.getActiveTripWithinMinutes(today, cutoffTime)
        if (activeTrip != null) {
            return activeTrip.id
        }

        val tripId = UUID.randomUUID().toString()
        val nowFormatted = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val trip = TripEntity(
            id = tripId,
            tripDate = today,
            tripCode = null,
            serviceType = serviceType,
            sourceSnapshotId = snapshotId,
            capturedAt = nowFormatted,
            createdAt = nowFormatted
        )
        repository.saveTrip(trip)
        return tripId
    }

    companion object {
        const val GROUPING_WINDOW_MINUTES = 5L
    }
}
