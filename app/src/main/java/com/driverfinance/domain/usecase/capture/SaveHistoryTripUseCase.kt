package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.HistoryTripEntity
import com.driverfinance.data.repository.CaptureRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SaveHistoryTripUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    /**
     * Saves a history trip with duplicate detection.
     * Duplicate check: date + time + service_type + total_earning.
     */
    suspend operator fun invoke(
        tripDate: String,
        tripTime: String,
        serviceType: String,
        totalEarning: Int,
        isCombined: Boolean,
        restaurantName: String?,
        rawText: String,
        sourceSnapshotId: String
    ): SaveResult {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val existing = repository.findDuplicateHistoryTrip(
            date = tripDate,
            time = tripTime,
            serviceType = serviceType,
            totalEarning = totalEarning
        )

        if (existing != null) {
            val updated = existing.copy(
                isCombined = if (isCombined) 1 else existing.isCombined,
                restaurantName = restaurantName ?: existing.restaurantName
            )
            repository.updateHistoryTrip(updated)
            return SaveResult.Updated(existing.id)
        }

        val id = UUID.randomUUID().toString()
        val entity = HistoryTripEntity(
            id = id,
            linkedTripId = null,
            tripDate = tripDate,
            tripTime = tripTime,
            serviceType = serviceType,
            totalEarning = totalEarning,
            isCombined = if (isCombined) 1 else 0,
            restaurantName = restaurantName,
            rawText = rawText,
            sourceSnapshotId = sourceSnapshotId,
            capturedAt = now,
            createdAt = now
        )
        repository.saveHistoryTrip(entity)
        return SaveResult.Created(id)
    }

    sealed interface SaveResult {
        data class Created(val id: String) : SaveResult
        data class Updated(val id: String) : SaveResult
    }
}
