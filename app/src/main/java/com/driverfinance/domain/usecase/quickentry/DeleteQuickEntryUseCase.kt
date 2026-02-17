package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.repository.QuickEntryRepository
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Soft deletes a quick entry (is_deleted = 1).
 */
class DeleteQuickEntryUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    suspend operator fun invoke(entryId: String): Boolean {
        val entry = repository.getEntryById(entryId) ?: return false
        val now = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        repository.updateEntry(
            entry.copy(isDeleted = 1, updatedAt = now)
        )
        return true
    }
}
