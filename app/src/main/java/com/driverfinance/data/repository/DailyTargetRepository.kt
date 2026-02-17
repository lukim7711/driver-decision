package com.driverfinance.data.repository

import com.driverfinance.data.local.dao.DailyTargetCacheDao
import com.driverfinance.data.local.entity.DailyTargetCacheEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyTargetRepository @Inject constructor(
    private val dao: DailyTargetCacheDao
) {

    fun getTodayCache(): Flow<DailyTargetCacheEntity?> {
        return dao.getByDate(LocalDate.now().toString())
    }

    suspend fun getTodayCacheSync(): DailyTargetCacheEntity? {
        return dao.getByDateSync(LocalDate.now().toString())
    }

    suspend fun saveCache(entity: DailyTargetCacheEntity) {
        dao.upsert(entity)
    }

    suspend fun invalidateToday() {
        dao.deleteByDate(LocalDate.now().toString())
    }
}
