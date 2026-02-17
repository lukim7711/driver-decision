package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.repository.QuickEntryRepository
import javax.inject.Inject

/**
 * Deletes a quick entry by ID.
 */
class DeleteQuickEntryUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    suspend operator fun invoke(entryId: String): Boolean {
        val entry = repository.getEntryById(entryId) ?: return false
        repository.deleteEntry(entry)
        return true
    }
}
