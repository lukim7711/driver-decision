package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.CapturedOrderEntity
import com.driverfinance.data.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripOrdersUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    operator fun invoke(tripId: String): Flow<List<CapturedOrderEntity>> {
        return repository.getOrdersByTripId(tripId)
    }
}
