package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.HistoryDetailEntity
import com.driverfinance.data.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHistoryTripDetailsUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    operator fun invoke(historyTripId: String): Flow<List<HistoryDetailEntity>> {
        return repository.getDetailsByHistoryTripId(historyTripId)
    }
}
