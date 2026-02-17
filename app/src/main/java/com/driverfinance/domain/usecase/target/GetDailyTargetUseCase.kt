package com.driverfinance.domain.usecase.target

import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import com.driverfinance.data.repository.DailyTargetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Reads daily_target_cache. If missing for today, triggers calculation.
 * Used by F005 Dashboard hero number and F008 AI Chat.
 */
class GetDailyTargetUseCase @Inject constructor(
    private val dailyTargetRepository: DailyTargetRepository,
    private val calculateDailyTargetUseCase: CalculateDailyTargetUseCase
) {

    operator fun invoke(): Flow<DailyTargetResult> {
        return dailyTargetRepository.getTodayCache().map { cached ->
            if (cached != null) {
                DailyTargetResult.Ready(cached)
            } else {
                // Cache empty for today → trigger first calculation (§6.3.9)
                val calculated = calculateDailyTargetUseCase()
                DailyTargetResult.Ready(calculated)
            }
        }
    }

    suspend fun recalculate(): DailyTargetCacheEntity {
        return calculateDailyTargetUseCase()
    }
}

sealed interface DailyTargetResult {
    data object Loading : DailyTargetResult
    data class Ready(val cache: DailyTargetCacheEntity) : DailyTargetResult
}
