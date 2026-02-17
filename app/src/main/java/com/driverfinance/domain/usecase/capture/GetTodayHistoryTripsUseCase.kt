package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.HistoryTripEntity
import com.driverfinance.data.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTodayHistoryTripsUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    operator fun invoke(): Flow<List<HistoryTripEntity>> {
        val today = LocalDate.now().toString()
        return repository.getTodayHistoryTrips(today)
    }
}
