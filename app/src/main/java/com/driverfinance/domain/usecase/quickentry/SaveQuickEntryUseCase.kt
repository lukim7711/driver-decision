package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.local.entity.QuickEntryEntity
import com.driverfinance.data.repository.QuickEntryRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

class SaveQuickEntryUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    suspend operator fun invoke(
        type: String,
        categoryId: String,
        amount: Int,
        note: String?
    ): SaveResult {
        if (amount <= 0) return SaveResult.InvalidAmount

        val now = OffsetDateTime.now()
        val entity = QuickEntryEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            categoryId = categoryId,
            amount = amount,
            note = note?.takeIf { it.isNotBlank() },
            entryDate = LocalDate.now().toString(),
            entryTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
            isDeleted = 0,
            createdAt = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            updatedAt = now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
        repository.saveEntry(entity)
        return SaveResult.Success(entity.id)
    }

    sealed interface SaveResult {
        data class Success(val id: String) : SaveResult
        data object InvalidAmount : SaveResult
    }
}
