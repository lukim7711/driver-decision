package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.ScreenSnapshotEntity
import com.driverfinance.data.repository.CaptureRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SaveScreenSnapshotUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    suspend operator fun invoke(
        screenType: String,
        rawText: String?,
        nodeTreeJson: String?,
        imagePath: String?
    ): String {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val id = UUID.randomUUID().toString()

        val snapshot = ScreenSnapshotEntity(
            id = id,
            screenType = screenType,
            imagePath = imagePath ?: "",
            rawText = rawText,
            nodeTreeJson = nodeTreeJson,
            isProcessed = 0,
            capturedAt = now,
            createdAt = now
        )
        repository.saveSnapshot(snapshot)
        return id
    }
}
