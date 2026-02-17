package com.driverfinance.domain.usecase.obligation

import com.driverfinance.data.local.entity.WorkScheduleEntity
import com.driverfinance.data.repository.WorkScheduleRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class ToggleWorkScheduleUseCase @Inject constructor(
    private val repository: WorkScheduleRepository
) {

    suspend operator fun invoke(date: String) {
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val existing = repository.getScheduleForDate(date)

        if (existing != null) {
            repository.upsert(
                existing.copy(
                    isWorking = if (existing.isWorking == 1) 0 else 1,
                    updatedAt = now
                )
            )
        } else {
            repository.upsert(
                WorkScheduleEntity(
                    id = UUID.randomUUID().toString(),
                    date = date,
                    isWorking = 0,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }
}
