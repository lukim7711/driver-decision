package com.driverfinance.domain.usecase.capture

import com.driverfinance.data.local.entity.TripEntity
import com.driverfinance.data.repository.CaptureRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTodayTripsUseCase @Inject constructor(
    private val repository: CaptureRepository
) {

    operator fun invoke(): Flow<List<TripEntity>> {
        val today = LocalDate.now().toString()
        return repository.getTodayTrips(today)
    }
}
