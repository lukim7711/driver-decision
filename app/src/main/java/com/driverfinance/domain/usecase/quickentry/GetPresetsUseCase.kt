package com.driverfinance.domain.usecase.quickentry

import com.driverfinance.data.local.entity.QuickEntryPresetEntity
import com.driverfinance.data.repository.QuickEntryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPresetsUseCase @Inject constructor(
    private val repository: QuickEntryRepository
) {

    operator fun invoke(categoryId: String): Flow<List<QuickEntryPresetEntity>> {
        return repository.getPresetsByCategory(categoryId)
    }
}
