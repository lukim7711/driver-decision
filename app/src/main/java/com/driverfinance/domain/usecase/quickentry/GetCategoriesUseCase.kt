package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.local.entity.QuickEntryCategoryEntity
import com.driverfinance.data.repository.QuickEntryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Returns active, non-system categories for the given type.
 * Grid query: is_active = 1 AND is_system = 0 (F004 §6.3.5)
 */
class GetCategoriesUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    operator fun invoke(type: String): Flow<List<QuickEntryCategoryEntity>> {
        return repository.getActiveNonSystemCategories(type)
    }
}
